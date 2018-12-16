package neolabs.kok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    Boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String ischeck = pref.getString("useremail",  null);

        if(ischeck != null) {
            isLogin = true;
        } else {
            isLogin = false;
        }

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 2000); // 1초 후에 hd handler 실행  3000ms = 3초

        //출처: http://yoo-hyeok.tistory.com/31 [유혁의 엉터리 개발]
    }

    private class splashhandler implements Runnable{
        public void run(){
            if(isLogin) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}
