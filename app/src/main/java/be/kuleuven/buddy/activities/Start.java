package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.activities.Info;
import be.kuleuven.buddy.activities.Login;
import be.kuleuven.buddy.activities.Register;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_start);
    }

    @Override
    public void onBackPressed() {
        // Prevent going back after logout
        moveTaskToBack(true);
    }

    public void goLogin(View caller) {
        Intent goToLogin = new Intent(this, Login.class);
        startActivity(goToLogin);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goRegister(View caller) {
        Intent goToRegister = new Intent(this, Register.class);
        startActivity(goToRegister);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}