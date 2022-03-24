package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

public class Register extends AppCompatActivity {
    TextView username, email, password, confirmPassword;
    boolean correctPassword, correctConfirmPassword, validEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        //define the variables
        username = findViewById(R.id.usernameFill_register);
        email = findViewById(R.id.emailFill_register);
        password = findViewById(R.id.passwFill_register);
        confirmPassword = findViewById(R.id.confPasswFill_register);

        //textwatcher password
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String currentPassword = password.getText().toString();
                //if longer than 8, contains a number and upper case
                if (currentPassword.length() >= 8 && currentPassword.matches("(.*[0-9].*)") && currentPassword.matches("(.*[A-Z].*)") ){
                    //display that the password is correct format
                    correctPassword = true;
                    System.out.println("Password is the correct format!\n");
                    password.setBackgroundResource(R.drawable.bg_fill_green);
                }
                else{
                    correctPassword = false;
                    System.out.println("Password is not the correct format\n");
                    password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }
        });

        //textwatcher confirmPassword
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (confirmPassword.getText().toString().equals(password.getText().toString())){
                    System.out.println("your password is the same, good job \n");
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill_green);
                    correctConfirmPassword = true;
                }
                else {
                    System.out.println("Password is not the same, try a new one\n");
                    confirmPassword.setBackgroundResource(R.drawable.bg_fill_red);
                    correctConfirmPassword = false;

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //textwatcher email. It checks if email is already in database
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (email.toString().isEmpty() == false)
                checkValidEmail();
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
        // TODO check if passwords match and if valid mail (not already used mail + existing)
        System.out.println(correctConfirmPassword);
        System.out.println(correctPassword);
        System.out.println(validEmail);
        if (correctConfirmPassword && correctPassword && validEmail && !username.getText().toString().isEmpty()){
            System.out.println("Everything is filled in correctly, ready to register");

            register();
            Intent goToHome = new Intent(this, Home.class);

            AccountInfo accountInfo = new AccountInfo(username.getText().toString(), email.getText().toString());
            goToHome.putExtra("account", accountInfo);

            startActivity(goToHome);
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
    }

    public void register(){
        //make the json object
        JSONObject user = new JSONObject();
        try {
            user.put("username", username.getText().toString());
            user.put("email", email.getText().toString());
            user.put("password", password.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //connect to database
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


        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                if (Integer.parseInt(response) == 1){
                    //email may be used since it is unique
                    validEmail = true;
                }
                else{
                    //email can't be used since it is already in database
                    validEmail = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        Log.d("string", stringRequest.toString());
        requestQueue.add(stringRequest);

    }
}