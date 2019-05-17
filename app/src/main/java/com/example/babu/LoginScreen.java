package com.example.babu;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginScreen extends AppCompatActivity {

    private Button loginButton, signinButton;
    TextView username, password;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        isUserCreated();

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(isCredentialsTrue(username.getText().toString(), password.getText().toString())){
                    successfulLogin();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Username or Password is not correct" , Toast.LENGTH_SHORT).show();
                }

            }
        });

        signinButton = findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(isUserNameAvailable(username.getText().toString())){
                    user = new User(username.getText().toString(), password.getText().toString());
                    successfulSignin();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Username is not available" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void successfulLogin(){
        Toast.makeText(getApplicationContext(), "Successful Login" , Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void successfulSignin(){
        Toast.makeText(getApplicationContext(), "Successful Sign in" , Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean isUserNameAvailable(String username){
        //batuhan
        return true;
    }

    public boolean isCredentialsTrue(String username, String password){
        //batuhan
        if(user == null)    return false;
        else return true;
    }

    public void isUserCreated(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains("User")) {
            successfulSignin();
        }
    }
}
