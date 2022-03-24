package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;

public class EditAccount extends AppCompatActivity {
    AccountInfo account;
    TextView email;
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_edit_account);

        account = getIntent().getExtras().getParcelable("account");

        email = findViewById(R.id.dyn_email_edit);
        username = findViewById(R.id.dyn_emailFill_edit);

        email.setText(account.getEmail());
        username.setHint(account.getUsername());
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