package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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

    public void goRequestReset(View caller) {
        Intent goToRequestReset = new Intent(this, RequestReset.class);
        startActivity(goToRequestReset);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goHome(View caller) {
        Intent goToHome = new Intent(this, Home.class);
        goToHome.putExtra("accountInfo", accountInfo);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();
    }
    public AccountInfo getAccount(){
        return accountInfo;
    }

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
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) { e.printStackTrace();}

            // Connect to database
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "https://a21iot03.studev.groept.be/public/api/login";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, null,
                    response -> {
                    //process the response
                        System.out.println("response: " + response);
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
                            }
                            else {
                                errorMessage.setText(R.string.incorrectEmail);
                                errorMessage.setVisibility(View.VISIBLE);
                                email.setBackgroundResource(R.drawable.bg_fill_red);
                                password.setBackgroundResource((R.drawable.bg_fill));
                            }

                        } catch (JSONException e){ e.printStackTrace();}},

                    error -> {
                    //process an error
                        errorMessage.setText(R.string.incorrectEmail);
                        errorMessage.setVisibility(View.VISIBLE);
                        email.setBackgroundResource(R.drawable.bg_fill_red);
                        password.setBackgroundResource((R.drawable.bg_fill));
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
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}