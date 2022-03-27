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
    boolean correctPassword, correctConfirmPassword, validEmail;
    ImageView infoBtn;
    ProgressBar loading;

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

        // Textwatcher password
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String currentPassword = password.getText().toString();
                // If longer than 8, contains a number and upper case
                if (currentPassword.length() == 0) {
                    password.setBackgroundResource(R.drawable.bg_fill);

                } else if (currentPassword.length() >= 8 && currentPassword.matches("(.*[0-9].*)") && currentPassword.matches("(.*[A-Z].*)") ){
                    // Display that the password is correct format
                    correctPassword = true;
                    System.out.println("Password is the correct format!\n");
                    password.setBackgroundResource(R.drawable.bg_fill_green);

                } else {
                    correctPassword = false;
                    System.out.println("Password is not the correct format\n");
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
                if (confirmPassword.length() == 0) {
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill);

                } else if (confirmPassword.getText().toString().equals(password.getText().toString())){
                    System.out.println("your password is the same, good job \n");
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill_green);
                    correctConfirmPassword = true;

                } else {
                    System.out.println("Password is not the same, try a new one\n");
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill_red);
                    correctConfirmPassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Textwatcher email. It checks if email is already in database
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!(email.getText().toString().isEmpty()))
                    checkValidEmail();
                else {
                    errorMessage.setVisibility(View.INVISIBLE);
                    email.setBackgroundResource(R.drawable.bg_fill);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
        if (username.getText().toString().isEmpty() || email.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()){
            errorMessage.setText(R.string.fillFields);
            errorMessage.setVisibility(View.VISIBLE);
            email.setBackgroundResource(R.drawable.bg_fill);

        } else if (correctConfirmPassword && correctPassword && validEmail && !username.getText().toString().isEmpty()) {
            System.out.println("Everything is filled in correctly, ready to register");
            register();

            email.setBackgroundResource(R.drawable.bg_fill);
            errorMessage.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);

            Intent goToHome = new Intent(this, Home.class);

            AccountInfo accountInfo = new AccountInfo(username.getText().toString(), email.getText().toString());
            goToHome.putExtra("account", accountInfo);

            startActivity(goToHome);
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        }
    }

    public void register(){
        // Make the json object
        JSONObject user = new JSONObject();
        try {
            user.put("username", username.getText().toString());
            user.put("email", email.getText().toString());
            user.put("password", password.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://a21iot03.studev.groept.be/public/user";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY", response);
            System.out.println(response);

        }, error -> Log.e("VOLLEY", error.toString())) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    // request body goes here
                    String userString = user.toString();
                    return userString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", user, "utf-8");
                    return null;
                }
            }
        };
        Log.d("string", stringRequest.toString());
        requestQueue.add(stringRequest);
    }


    public void checkValidEmail(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://a21iot03.studev.groept.be/public/checkEmail/";
        URL = URL + email.getText().toString();
        System.out.println(URL);

        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL, response -> {
            System.out.println(response);
            if (Integer.parseInt(response) == 1){
                // Email may be used since it is unique
                validEmail = true;
                email.setBackgroundResource(R.drawable.bg_fill);
                errorMessage.setVisibility(View.INVISIBLE);
            }
            else{
                // Email can't be used since it is already in database
                validEmail = false;
                email.setBackgroundResource(R.drawable.bg_fill_red);
                errorMessage.setText(R.string.emailused);
                errorMessage.setVisibility(View.VISIBLE);
            }
        }, error -> {}
        );

        Log.d("string", stringRequest.toString());
        requestQueue.add(stringRequest);
    }
}