package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import be.kuleuven.buddy.R;

public class EditAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goAccount(View caller) {
        // TODO back to account after updating everything + checking if passwords match
        Intent goToAccountSave = new Intent(this, Account.class);
        startActivity(goToAccountSave);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}