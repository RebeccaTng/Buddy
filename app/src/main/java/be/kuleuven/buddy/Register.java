package be.kuleuven.buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void goLogin(View caller) {
        Intent goToLogin = new Intent(this, Login.class);
        startActivity(goToLogin);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goHome(View caller) {
        //TODO check if passwords match and if valid mail (not already used mail + existing)
        Intent goToHome = new Intent(this, Home.class);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}