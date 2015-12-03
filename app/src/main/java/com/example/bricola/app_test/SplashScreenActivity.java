package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_NEXT_IMAGE = 100;
    private ImageView splashScreenImageView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashScreenImageView = (ImageView) findViewById(R.id.splashScreen_imageView);

        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method
            @Override
            public void run() {
                splashScreenImageView.setImageResource(R.mipmap.splash01);
                new Handler().postDelayed(new Runnable() {

                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {
                        splashScreenImageView.setImageResource(R.mipmap.splash02);
                        new Handler().postDelayed(new Runnable() {

                            // Using handler with postDelayed called runnable run method
                            @Override
                            public void run() {
                                splashScreenImageView.setImageResource(R.mipmap.splash03);
                                new Handler().postDelayed(new Runnable() {

                                    // Using handler with postDelayed called runnable run method
                                    @Override
                                    public void run() {
                                        splashScreenImageView.setImageResource(R.mipmap.splash04);
                                        new Handler().postDelayed(new Runnable() {

                                            // Using handler with postDelayed called runnable run method
                                            @Override
                                            public void run() {
                                                splashScreenImageView.setImageResource(R.mipmap.splash05);
                                                new Handler().postDelayed(new Runnable() {

                                                    // Using handler with postDelayed called runnable run method
                                                    @Override
                                                    public void run() {
                                                        splashScreenImageView.setImageResource(R.mipmap.splash06);
                                                        new Handler().postDelayed(new Runnable() {

                                                            // Using handler with postDelayed called runnable run method
                                                            @Override
                                                            public void run() {
                                                                splashScreenImageView.setImageResource(R.mipmap.splash07);
                                                                new Handler().postDelayed(new Runnable() {

                                                                    // Using handler with postDelayed called runnable run method
                                                                    @Override
                                                                    public void run() {
                                                                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                }, SPLASH_NEXT_IMAGE);
                                                            }
                                                        }, SPLASH_NEXT_IMAGE);
                                                    }
                                                }, SPLASH_NEXT_IMAGE);
                                            }
                                        }, SPLASH_NEXT_IMAGE);
                                    }
                                }, SPLASH_NEXT_IMAGE);
                            }
                        }, SPLASH_NEXT_IMAGE);
                    }
                }, SPLASH_NEXT_IMAGE);
            }
        }, 1500);
    }
}
