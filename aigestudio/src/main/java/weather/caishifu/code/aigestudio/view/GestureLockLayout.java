package weather.caishifu.code.aigestudio.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wp on 2017/2/8.
 *
 * @description
 */

public class GestureLockLayout extends RelativeLayout {
    private GestureLockView[] mGestureLockViews ;

    private int mCount = 5;

    private int[] answer = {1, 2 , 3 , 4};

    private List<Integer> mChoose  ;

    private Paint mPaint ;

    private int mMarginBetweenLockView = 20 ;

    private int mChileWidth ;

    private int mWidth ;

    private int mHeight ;

    private Path mPath ;

    private int mLastPathX ;

    private int mLastPathY ;

    private Point mTemTarget = new Point();

    private int mTryTimes  = 4 ;

    private OnGestureLockLayoutListener listener ;

    public void setOnGestureLockLayoutListener(OnGestureLockLayoutListener l){
        this.listener = l ;
    }


    public GestureLockLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();

        mChoose  = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec , heightMeasureSpec);

            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);

            mWidth = Math.min(mWidth , mHeight);

            setMeasuredDimension(mWidth , mWidth);
            if(mGestureLockViews == null){
                mGestureLockViews = new GestureLockView[mCount * mCount];
                //获取子view宽度
                mChileWidth = (int)(4 * mWidth * 1.0f /(5 * mCount + 1));
                //获取间距
                mMarginBetweenLockView = mChileWidth / 4 ;
                //设置连线粗细
                mPaint.setStrokeWidth(mChileWidth * 0.29f );

                for(int i = 0 ; i < mGestureLockViews.length ; i++ ){
                    mGestureLockViews[i] = new GestureLockView(getContext());
                    mGestureLockViews[i].setId(i + 1);

                    RelativeLayout.LayoutParams lp = new RelativeLayout.
                                                LayoutParams(mChileWidth , mChileWidth);

                    if( i % mCount != 0){
                        lp.addRule(RelativeLayout.RIGHT_OF , mGestureLockViews[i-1].getId());
                    }

                    if( i >= mCount){
                        lp.addRule(RelativeLayout.BELOW , mGestureLockViews[i- mCount].getId());
                    }
                    int l = mMarginBetweenLockView ;
                    int t = mMarginBetweenLockView ;
                    int r = 0 ;
                    int b = 0 ;
                    if((i+1) % mCount == 0){
                        r = mMarginBetweenLockView ;
                    }
                    if(i >= (mCount * (mCount - 1))){
                        b = mMarginBetweenLockView ;
                    }
                    lp.setMargins(l , t , r ,b);
                    mGestureLockViews[i].setMode(GestureLockView.Mode.STATUS_NO_FINGER);
                    addView(mGestureLockViews[i],lp);
                }
            }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!hasTime()){
            if(listener != null){
                listener.onHasNoTimes();
            }

            return true;
        }
        int action = event.getAction() ;
        int x = (int)event.getX() ;
        int y = (int)event.getY() ;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                reset();
                mPaint.setColor(Color.BLACK);
                mPaint.setAlpha(50);
                break;
            case MotionEvent.ACTION_MOVE:
                GestureLockView c = getChildByPos(x , y);
                if(c != null){
                    int cId = c.getId() ;
                    if(!mChoose.contains(cId)){
                        mChoose.add(cId);
                        c.setMode(GestureLockView.Mode.STATUS_FINGER_ON);

                        //设置连接点坐标
                        mLastPathX = c.getLeft() + mChileWidth/2 ;
                        mLastPathY = c.getTop() + mChileWidth/2 ;

                        if(mChoose.size() == 1){
                            mPath.moveTo(mLastPathX , mLastPathY);
                        }else {
                            mPath.lineTo(mLastPathX , mLastPathY);
                        }
                    }
                }
                mTemTarget.x = x ;
                mTemTarget.y = y ;
                break;
            case MotionEvent.ACTION_UP:
                mPaint.setColor(Color.RED);
                mPaint.setAlpha(50);
                mTemTarget.x = x ;
                mTemTarget.y = y ;

                changeItemMode();

                if(!isRight(answer , mChoose)){
                    mTryTimes -- ;
                }else {
                    mTryTimes = 4 ;
                }
                if(listener != null){
                    listener.onFingerUp(isRight(answer , mChoose));
                }
                break;

        }
        invalidate();
        return true ;
    }


    private void reset(){
        mChoose.clear();
        mPath.reset();
        for(GestureLockView c : mGestureLockViews){
            c.setDegree(-1);
            c.setMode(GestureLockView.Mode.STATUS_NO_FINGER);
        }
    }

    /**
     * 通过位置获取子view
     * @return
     */
    private GestureLockView getChildByPos(int x , int y ){
        for(GestureLockView c : mGestureLockViews){
            if(checkPositionInChild(c , x , y)){
                return  c ;
            }
        }
        return null ;
    }


    /**
     * 检查当前坐标是否是该view所在的区域
     * @param c
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(GestureLockView c , int x , int y ){
        int padding = (int)(mChileWidth * 0.15);
        if(x >= c.getLeft() + padding
                && x <= c.getRight() - padding
                && y >= c.getTop() + padding
                && y <= c.getBottom() - padding)
        {
            return true ;
        }

        return false ;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制小圆之间的路径
        if(mPath != null){
            canvas.drawPath(mPath , mPaint);
        }
        //绘制多与路径
        if(mChoose.size() > 0){
            canvas.drawLine(mLastPathX , mLastPathY , mTemTarget.x , mTemTarget.y , mPaint);
        }
    }

    /**
     * 手指抬起
     */
    private void changeItemMode(){
        //计算角度
        for(int i = 0 ; i < mChoose.size() ; i++){
            //最后一个直接return
            if(i == mChoose.size() - 1){
                break;
            }
            int childId = mChoose.get(i);
            int nextId = mChoose.get(i+1);
            GestureLockView c = (GestureLockView) findViewById(childId);
            GestureLockView nextC = (GestureLockView) findViewById(nextId);
            int dx = nextC.getLeft() - c.getLeft() ;
            int dy = nextC.getTop() - c.getTop() ;

            int angle = (int)Math.toDegrees(Math.atan2(dy , dx)) + 90 ;
            c.setDegree(angle);
        }
        //设置模式
        for(GestureLockView c : mGestureLockViews){
            if(mChoose.contains(c.getId()))
            {
                c.setMode(GestureLockView.Mode.STATUS_FINGER_UP);
            }
        }
    }

    public interface OnGestureLockLayoutListener{

        void onFingerUp(boolean flag);


        void onHasNoTimes();
    }


    public boolean isRight(int[] answer , List<Integer>  finger){
        if(answer.length != finger.size()){
            return  false ;
        }
        for(int i = 0 ; i < answer.length ; i ++){
            if( answer[i] != finger.get(i)){
                return false ;
            }
        }
        return  true;
    }

    public boolean hasTime(){

        return (mTryTimes != 0 );
    }
}
