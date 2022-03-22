package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import be.kuleuven.buddy.R;

public class Register extends AppCompatActivity {
    TextView username, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goLogin(View caller) {
        Intent goToLogin = new Intent(this, Login.class);
        startActivity(goToLogin);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goHome(View caller) {
        // TODO check if passwords match and if valid mail (not already used mail + existing)
        Intent goToHome = new Intent(this, Home.class);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void checkRegister(View caller){
        username = (TextView) findViewById(R.id.mqidjf);
        email = (TextView) findViewById(R.id.emailFill);
        password = (TextView) findViewById(R.id.passwFill);
        



    }
}