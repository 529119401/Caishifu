package weather.caishifu.code.aigestudio.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wp on 2017/2/8.
 *
 * @description
 */

public class GestureLockView extends View{
    public  enum Mode{
        STATUS_NO_FINGER , STATUS_FINGER_ON , STATUS_FINGER_UP
    }

    /**
     * 模式
     */
    private Mode mode = Mode.STATUS_NO_FINGER ;

    private int mWidth ;

    private int mHeight ;

    private int mRadius ;

    private int mStrokeWidth = 2 ;

    private int mCenterX ;

    private int mCenterY ;

    private Paint mPaint ;

    /**
     * 三角形相关
     */
    private float mArrowRate = 0.333f ;

    private int mArrowDegree = -1 ;

    private Path mPath ;

    /**
     * 内圆半径比例
     */
    private float mInnerCircleRadiusRate = 0.3f ;

    /**
     * 四个颜色，可由用户自定义，初始化时由GestureLockLayout传入
     */
    private int mColorNoFingerInner;

    private int mColorNoFingerOutter;

    private int mColorFingerOn;

    private int mColorFingerUp;


    public GestureLockView(Context context) {
        this(context , null);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mWidth = Math.min(mWidth , mHeight);
        mRadius = mCenterX = mCenterY = mWidth / 2 ;

        /**
         * 三角箭头的路径
         */
        float mArrowLength = mWidth / 2 * mArrowRate ;
        mPath.moveTo(mWidth/2 , mStrokeWidth + 2 );
        mPath.lineTo(mWidth / 2 - mArrowLength , mStrokeWidth + 2 + mArrowLength);
        mPath.lineTo(mWidth / 2 + mArrowLength , mStrokeWidth + 2 + mArrowLength);
        mPath.close();
        mPath.setFillType(Path.FillType.WINDING);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (mode){
            case STATUS_NO_FINGER: {
                //绘制外圆
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.GRAY);
                canvas.drawCircle(mCenterX , mCenterY , mRadius , mPaint);
                //绘制内圆
                mPaint.setColor(Color.BLUE);
                canvas.drawCircle(mCenterX , mCenterY , mRadius * mInnerCircleRadiusRate , mPaint);
                break;
            }
            case STATUS_FINGER_ON: {
                //绘制外圆
                mPaint.setStrokeWidth(mStrokeWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.GREEN);
                canvas.drawCircle(mCenterX , mCenterY , mRadius - mStrokeWidth , mPaint);
                //绘制内圆
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCenterX , mCenterY , mRadius * mInnerCircleRadiusRate , mPaint);
                break;
            }
            case STATUS_FINGER_UP: {
                //绘制外圆
                mPaint.setStrokeWidth(mStrokeWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.RED);
                canvas.drawCircle(mCenterX , mCenterY , mRadius - mStrokeWidth , mPaint);
                //绘制内圆
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCenterX , mCenterY , mRadius * mInnerCircleRadiusRate , mPaint);
                //绘制箭头
                drawArrow(canvas);
                break;
            }

        }
    }

    /**
     * 设置模式
     * @param mode
     */
    public void setMode(Mode mode){
        this.mode = mode ;
        invalidate();
    }

    /**
     * 绘制箭头
     * @param canvas
     */
    private void drawArrow(Canvas canvas){
        if(getDegree() != -1){
            mPaint.setStyle(Paint.Style.FILL);
            canvas.save();
            canvas.rotate(getDegree() , mCenterX , mCenterY);
            canvas.drawPath(mPath , mPaint);
            canvas.restore();
        }
    }

    /**
     * 设置箭头角度
     * @param degree
     */
    public void setDegree(int degree){
        this.mArrowDegree = degree ;
    }

    public int  getDegree(){
        return this.mArrowDegree ;
    }
}
