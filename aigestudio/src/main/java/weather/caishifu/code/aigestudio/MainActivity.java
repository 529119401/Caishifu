package weather.caishifu.code.aigestudio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import weather.caishifu.code.aigestudio.view.GestureLockLayout;

public class MainActivity extends AppCompatActivity {
    private GestureLockLayout view ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (GestureLockLayout) findViewById(R.id.view);

        view.setOnGestureLockLayoutListener(new GestureLockLayout.OnGestureLockLayoutListener() {
            @Override
            public void onFingerUp(boolean flag) {
                Toast.makeText(MainActivity.this , flag + "" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHasNoTimes() {
                Toast.makeText(MainActivity.this , "没有机会了" , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
