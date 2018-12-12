package com.kripto.passpocket;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SplashAct extends AppCompatActivity {

    protected int _splashTime = 3500;

    @BindView(R.id.splash1)
    TextView tv1;
    @BindView(R.id.splash2)
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        StartAnimations();
    }

    private void StartAnimations() {

        Animation loadAnimation1 = AnimationUtils.loadAnimation(this, R.anim.translate_anim);
        loadAnimation1.reset();
        tv1.clearAnimation();
        tv1.startAnimation(loadAnimation1);

        Animation loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.translate_anim2);
        loadAnimation2.reset();
        tv2.clearAnimation();
        tv2.startAnimation(loadAnimation2);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < _splashTime) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashAct.this, LoginAct.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashAct.this.finish();
                } catch (InterruptedException e) {

                } finally {
                    SplashAct.this.finish();
                }

            }
        };
        splashThread.start();

    }
}
