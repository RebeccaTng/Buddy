package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;

public class Account extends AppCompatActivity {
    AccountInfo accountInfo;
    TextView username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_account);

        username = findViewById(R.id.dyn_username_acc);
        email = findViewById(R.id.dyn_email_acc);

        if(getIntent().hasExtra("accountInfo")) {
            accountInfo = getIntent().getExtras().getParcelable("accountInfo");
            username.setText(accountInfo.getUsername());
            email.setText(accountInfo.getEmail());
        }
    }

    @Override
    public void onBackPressed() {
        Intent goToHome = new Intent(this, Home.class);
        goToHome.putExtra("accountInfo", accountInfo);
        startActivity(goToHome);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goEditAccount(View caller) {
        Intent goToEditAccount = new Intent(this, EditAccount.class);
        goToEditAccount.putExtra("accountInfo", accountInfo);
        startActivity(goToEditAccount);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goStart(View caller) {
        // Logout
        Intent goToStart = new Intent(this, Start.class);
        startActivity(goToStart);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}