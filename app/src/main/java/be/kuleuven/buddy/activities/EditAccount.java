package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;

public class EditAccount extends AppCompatActivity {
    //declare variables
    AccountInfo account;
    TextView email;
    EditText username, current_password, new_password, confirm_password;

    boolean correctPassword;
    boolean correctConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_edit_account);

        account = getIntent().getExtras().getParcelable("account");

        //link the variables
        email = findViewById(R.id.dyn_email_edit);
        username = findViewById(R.id.dyn_emailFill_edit);

        current_password = findViewById(R.id.currPasswFill_edit);
        new_password = findViewById(R.id.newPasswFill_edit);
        confirm_password = findViewById(R.id.confPasswFill_edit);

        email.setText(account.getEmail());
        username.setHint(account.getUsername());

        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentPassword = new_password.getText().toString();
                //if longer than 8, contains a number and upper case
                if (currentPassword.length() >= 8 && currentPassword.matches("(.*[0-9].*)") && currentPassword.matches("(.*[A-Z].*)") ){
                    //display that the password is correct format
                    correctPassword = true;
                    System.out.println("Password is the correct format!\n");
                    new_password.setBackgroundResource(R.drawable.bg_fill_green);
                }
                else{
                    correctPassword = false;
                    System.out.println("Password is not the correct format\n");
                    new_password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (confirm_password.getText().toString().equals(new_password.getText().toString())){
                    System.out.println("your password is the same, good job \n");
                    confirm_password.setBackgroundResource(R.drawable.bg_fill_green);
                    correctConfirmPassword = true;
                }
                else {
                    System.out.println("Password is not the same, try a new one\n");
                    confirm_password.setBackgroundResource(R.drawable.bg_fill_red);
                    correctConfirmPassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goAccount(View caller) {
        // TODO back to account after updating everything + checking if passwords match


        //check if username is changed
        if (username.getText().toString().equals("")){
            username.setText(account.getUsername());

        }

        System.out.println(username.getText().toString());
        account.setUsername(username.getText().toString());

        //check if everything is filled correctly
        if (correctConfirmPassword && correctPassword){
            System.out.println("helemaal mooi");
            Intent goToAccountSave = new Intent(this, Account.class);
            goToAccountSave.putExtra("account", account);

            startActivity(goToAccountSave);
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }


    }
}