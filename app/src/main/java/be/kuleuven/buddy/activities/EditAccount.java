package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;

public class EditAccount extends AppCompatActivity {
    //declare variables
    AccountInfo accountInfo;
    TextView email;
    EditText username, current_password, new_password, confirm_password;

    boolean correctPassword, correctNewPassword, correctConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_edit_account);

        accountInfo = getIntent().getExtras().getParcelable("accountInfo");

        //link the variables
        email = findViewById(R.id.dyn_email_edit);
        username = findViewById(R.id.dyn_emailFill_edit);
        current_password = findViewById(R.id.currPasswFill_edit);
        new_password = findViewById(R.id.newPasswFill_edit);
        confirm_password = findViewById(R.id.confPasswFill_edit);

        //initial values
        email.setText(accountInfo.getEmail());
        username.setHint(accountInfo.getUsername());

        //new password textwatcher
        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newPassword = new_password.getText().toString();
                //if longer than 8, contains a number and upper case
                if (newPassword.length() >= 8 && newPassword.matches("(.*[0-9].*)") && newPassword.matches("(.*[A-Z].*)") ){
                    correctNewPassword = true;
                    new_password.setBackgroundResource(R.drawable.bg_fill_green);
                }
                else{
                    correctNewPassword = false;
                    new_password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //confirm password textwatcher
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (confirm_password.getText().toString().equals(new_password.getText().toString())){
                    correctConfirmPassword = true;
                    confirm_password.setBackgroundResource(R.drawable.bg_fill_green);
                }
                else {
                    correctConfirmPassword = false;
                    confirm_password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //current password textwatcher
        current_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {}
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
            username.setText(accountInfo.getUsername());
        }

        //check if everything is filled correctly
        if (correctConfirmPassword && correctNewPassword && correctPassword){
            //accountInfo.setUsername(username.getText().toString());
        }
        else{ }
    }

}