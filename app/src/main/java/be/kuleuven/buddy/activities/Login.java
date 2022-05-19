package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;

public class Login extends AppCompatActivity {
    //instantiate variables
    TextView email, password, errorMessage;
    ProgressBar loading;
    AccountInfo accountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_login);

        //declare variables
        email = findViewById(R.id.emailFill_login);
        password = findViewById(R.id.passwFill_login);
        errorMessage = findViewById(R.id.errorMessage_login);
        loading = findViewById(R.id.loading_login);

        textWatchers();
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goRegister(View caller) {
        Intent goToRegister = new Intent(this, Register.class);
        startActivity(goToRegister);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goHome(View caller) {
        Intent goToHome = new Intent(this, Home.class);
        goToHome.putExtra("accountInfo", accountInfo);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();
    }

    public void goRequestReset(View caller) {
        Intent goToRequestReset = new Intent(this, RequestReset.class);
        startActivity(goToRequestReset);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }


    public AccountInfo getAccount(){ return accountInfo; }

    public void checkLogin(View caller){
        // Error message when some fields are not filled in
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            errorMessage.setText(R.string.fillFields);
            errorMessage.setVisibility(View.VISIBLE);
            email.setBackgroundResource(R.drawable.bg_fill);
            password.setBackgroundResource(R.drawable.bg_fill);

        } else {
            // Make the json object for the body of the post request
            JSONObject login = new JSONObject();
            try {
                login.put("type", "UsersInfo");
                login.put("email", email.getText().toString());

                //hash password
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] encodedhash = digest.digest(password.getText().toString().getBytes(StandardCharsets.UTF_8));
                    login.put("password", bytesToHex(encodedhash));
                } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

            } catch (JSONException e) { e.printStackTrace(); }

            // Connect to database
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "https://a21iot03.studev.groept.be/public/api/login";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, null,
                    response -> {
                    //process the response
                        try {
                            String Rmessage = response.getString("message");
                            String Rusername = response.getJSONObject("data").getString("username");
                            String Rmail = response.getJSONObject("data").getString("email");
                            String Rtoken = response.getJSONObject("data").getString("token");

                            //check if login is valid
                            if (Rmessage.equals("LoginSuccess")){
                                errorMessage.setVisibility(View.INVISIBLE);
                                email.setBackgroundResource(R.drawable.bg_fill);
                                password.setBackgroundResource(R.drawable.bg_fill);
                                loading.setVisibility(View.VISIBLE);
                                accountInfo = new AccountInfo(Rusername, Rmail, Rtoken);
                                accountInfo.printAccount();
                                goHome(caller);

                            } else {
                                errorMessage.setText(R.string.incorrectEmail);
                                errorMessage.setVisibility(View.VISIBLE);
                                email.setBackgroundResource(R.drawable.bg_fill_red);
                                password.setBackgroundResource((R.drawable.bg_fill_red));
                            }

                        } catch (JSONException e){ e.printStackTrace(); }},

                    error -> {
                    //process an error
                        errorMessage.setText(R.string.error);
                        errorMessage.setVisibility(View.VISIBLE);
                        email.setBackgroundResource(R.drawable.bg_fill_red);
                        password.setBackgroundResource((R.drawable.bg_fill_red));
                    })
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    // request body goes here
                    return login.toString().getBytes(StandardCharsets.UTF_8);
                }
            };
            requestQueue.add(jsonObjectRequest);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void clearBorders(){
        errorMessage.setVisibility(View.INVISIBLE);
        email.setBackgroundResource(R.drawable.bg_fill);
        password.setBackgroundResource(R.drawable.bg_fill);
    }

    private void textWatchers(){
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearBorders();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearBorders();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}