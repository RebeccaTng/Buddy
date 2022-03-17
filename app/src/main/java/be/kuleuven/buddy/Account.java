package be.kuleuven.buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }

    public void goHome(View caller) {
        Intent goToHome = new Intent(this, Home.class);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}