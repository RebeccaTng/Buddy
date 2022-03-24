package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import be.kuleuven.buddy.R;

public class RequestReset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_request_reset);
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goLogin(View caller) {
        // TODO check if mail is valid and registered mail
        Intent goToLogin = new Intent(this, Login.class);
        startActivity(goToLogin);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    //TODO when click on request, send mail
}