package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import be.kuleuven.buddy.R;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goEditAccount(View caller) {
        Intent goToEditAccount = new Intent(this, EditAccount.class);
        startActivity(goToEditAccount);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goStart(View caller) {
        Intent goToStart = new Intent(this, Start.class);
        startActivity(goToStart);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}