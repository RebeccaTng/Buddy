package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import be.kuleuven.buddy.other.InfoFragment;

public class Register extends AppCompatActivity {

    private TextView username, email, password, confirmPassword, errorMessage;
    private boolean correctPassword, correctConfirmPassword;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.usernameFill_register);
        email = findViewById(R.id.emailFill_register);
        password = findViewById(R.id.passwFill_register);
        confirmPassword = findViewById(R.id.confPasswFill_register);
        ImageView infoBtn = findViewById(R.id.infoIcon);
        errorMessage = findViewById(R.id.errorMessage_register);
        loading = findViewById(R.id.loading_register);

        infoBtn.setOnClickListener(view -> {
            String title = getResources().getString(R.string.passwRequire);
            String body = getResources().getString(R.string.passwReqText);
            InfoFragment passwInfo = InfoFragment.newInstance(title, body);
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
                confirmPassword.getText().toString().isEmpty() || username.getText().toString().isEmpty()) {
            errorMessage.setText(R.string.fillFields);
            errorMessage.setVisibility(View.VISIBLE);

        } else if (correctPassword && correctConfirmPassword){
            register();

        } else {
            errorMessage.setText(R.string.fillFields);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    private JSONObject registerBody() {
        JSONObject register = new JSONObject();
        try {
            register.put("email", email.getText().toString());
            register.put("username", username.getText().toString());
            //hash password
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedhash = digest.digest(password.getText().toString().getBytes(StandardCharsets.UTF_8));
                register.put("password", bytesToHex(encodedhash));
            } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        } catch (JSONException e) { e.printStackTrace();}

        return register;
    }

    private void register(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/register";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if (Rmessage.equals("UserRegisterSuccess")){
                            loading.setVisibility(View.VISIBLE);

                            String Rcomment = response.getString("comment");
                            Toast toast = Toast.makeText(getApplicationContext(), Rcomment, Toast.LENGTH_LONG);
                            toast.show();

                            Intent goToLogin = new Intent(this, Login.class);
                            startActivity(goToLogin);
                            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                        } else {
                            errorMessage.setText(R.string.emailAlreadyUsed);
                            errorMessage.setVisibility(View.VISIBLE);
                            email.setBackgroundResource(R.drawable.bg_fill_red);
                        }

                    } catch (Exception e){ e.printStackTrace();}},

                error -> {
                    clearBorders();
                    errorMessage.setText(R.string.error);
                    errorMessage.setVisibility(View.VISIBLE);
                    email.setBackgroundResource(R.drawable.bg_fill_red);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() { return registerBody().toString().getBytes(StandardCharsets.UTF_8); }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void textWatchers(){
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { clearBorders(); }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { clearBorders(); }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                clearBorders();
                String currentPassword = password.getText().toString();
                correctConfirmPassword = false;
                if (currentPassword.length() >= 8 && currentPassword.matches("(.*[0-9].*)") && currentPassword.matches("(.*[A-Z].*)") ){
                    correctPassword = true;
                    password.setBackgroundResource(R.drawable.bg_fill_green);

                } else {
                    correctPassword = false;
                    password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
        username.setBackgroundResource(R.drawable.bg_fill);
        email.setBackgroundResource(R.drawable.bg_fill);
        password.setBackgroundResource(R.drawable.bg_fill);
        confirmPassword.setBackgroundResource(R.drawable.bg_fill);
    }
}