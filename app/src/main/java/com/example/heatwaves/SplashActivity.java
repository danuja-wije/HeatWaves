package com.example.heatwaves;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView appName = findViewById(R.id.appName); // Make sure you have a TextView with id appName in your layout
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in); // Ensure you have a fade_in.xml animation resource
        appName.startAnimation(fadeIn);
        ImageView wavyLine1 = findViewById(R.id.wavyLine1);
        ImageView wavyLine2 = findViewById(R.id.wavyLine2);

        Animation wave1Animation = AnimationUtils.loadAnimation(this, R.anim.wave1_animation);
        Animation wave2Animation = AnimationUtils.loadAnimation(this, R.anim.wave2_animation);

        wavyLine1.startAnimation(wave1Animation);
        wavyLine2.startAnimation(wave2Animation);
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, 3000); // 3 seconds delay
    }
}