package com.example.babu;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginScreen extends AppCompatActivity {

    private Button loginButton, signinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                successfulLogin();
            }
        });

        signinButton = findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                successfulSignin();
            }
        });
    }

    public void successfulLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void successfulSignin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
