package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.fragments.PasswInfoFragment;

public class Register extends AppCompatActivity {
    TextView username, email, password, confirmPassword, errorMessage;
    boolean correctPassword, correctConfirmPassword;
    ImageView infoBtn;
    ProgressBar loading;
    AccountInfo accountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.usernameFill_register);
        email = findViewById(R.id.emailFill_register);
        password = findViewById(R.id.passwFill_register);
        confirmPassword = findViewById(R.id.confPasswFill_register);
        infoBtn = findViewById(R.id.infoIcon);
        errorMessage = findViewById(R.id.errorMessage_register);
        loading = findViewById(R.id.loading_register);

        // Display password requirements
        infoBtn.setOnClickListener(view -> {
            PasswInfoFragment passwInfo = new PasswInfoFragment();
            passwInfo.show(getSupportFragmentManager(), "passwFragment");
        });

        textWatchers();
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goLogin(View caller) {
        Intent goToLogin = new Intent(this, Login.class);
        startActivity(goToLogin);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goHome(View caller) {
        // Error message when some fields are not filled in
        if (username.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                confirmPassword.getText().toString().isEmpty() || username.getText().toString().isEmpty()){
            errorMessage.setText(R.string.fillFields);
            errorMessage.setVisibility(View.VISIBLE);

        } else if (correctPassword && correctConfirmPassword){
            register();
        }
        else{
            errorMessage.setText(R.string.fillFields);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    public void register(){
        // Make the json object for the body of the post request
        JSONObject login = new JSONObject();
        try {
            login.put("email", email.getText().toString());
            login.put("username", username.getText().toString());
            login.put("password", password.getText().toString());
        } catch (JSONException e) { e.printStackTrace();}

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/register";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");
                        //check if login is valid
                        if (Rmessage.equals("UserRegisterSuccess")){
                            accountInfo = new AccountInfo(username.getText().toString(), email.getText().toString(), null);
                            accountInfo.printAccount();
                            loading.setVisibility(View.VISIBLE);
                            Intent goToHome = new Intent(this, Home.class);
                            goToHome.putExtra("accountInfo", accountInfo);
                            startActivity(goToHome);
                            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        }
                        else {
                            errorMessage.setText(R.string.emailAlreadyUsed);
                            errorMessage.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e){ e.printStackTrace();}},

                error -> {
                    //process an error
                    errorMessage.setText(R.string.incorrectEmail);
                    errorMessage.setVisibility(View.VISIBLE);
                    username.setBackgroundResource(R.drawable.bg_fill_red);
                    email.setBackgroundResource(R.drawable.bg_fill_red);
                    password.setBackgroundResource(R.drawable.bg_fill_red);
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill_red);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    // request body goes here
                    return login.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", login, "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(jsonObjectRequest);
    }



    public void textWatchers(){
        //textwatcher username
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
                username.setBackgroundResource(R.drawable.bg_fill);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //textwatcher email
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
                email.setBackgroundResource(R.drawable.bg_fill);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Textwatcher password
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                errorMessage.setVisibility(View.INVISIBLE);
                password.setBackgroundResource(R.drawable.bg_fill);
                String currentPassword = password.getText().toString();
                if (currentPassword.length() >= 8 && currentPassword.matches("(.*[0-9].*)") && currentPassword.matches("(.*[A-Z].*)") ){
                    correctPassword = true;
                    password.setBackgroundResource(R.drawable.bg_fill_green);
                } else {
                    correctPassword = false;
                    password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }
        });

        // Textwatcher confirmPassword
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
                confirmPassword.setBackgroundResource(R.drawable.bg_fill);
                if (confirmPassword.getText().toString().equals(password.getText().toString())){
                    correctConfirmPassword = true;
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill_green);

                } else {
                    correctConfirmPassword = false;
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

}