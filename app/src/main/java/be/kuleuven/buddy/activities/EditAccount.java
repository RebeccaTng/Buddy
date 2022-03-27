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
    AccountInfo account;
    TextView email;
    EditText username, current_password, new_password, confirm_password;

    boolean correctPassword, correctNewPassword, correctConfirmPassword;

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

        //new password textwatcher
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
                    correctNewPassword = true;
                    System.out.println("Password is the correct format!\n");
                    new_password.setBackgroundResource(R.drawable.bg_fill_green);
                }
                else{
                    correctNewPassword = false;
                    System.out.println("Password is not the correct format\n");
                    new_password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //confirm password textwatcher
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


        current_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPassword();
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
        if (correctConfirmPassword && correctNewPassword && correctPassword){
            System.out.println("helemaal mooi");
            changePassword();
            Intent goToAccountSave = new Intent(this, Home.class);
            goToAccountSave.putExtra("account", account);

            startActivity(goToAccountSave);
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
        else{
            current_password.setBackgroundResource(R.drawable.bg_fill_red);
        }


    }

    public void checkPassword(){
        // Make the json object
        JSONObject user = new JSONObject();
        try {
            user.put("email", account.getEmail());
            user.put("password", current_password.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://a21iot03.studev.groept.be/public/login";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY", response);
            System.out.println(response);

            if (response.equals("false")) {
                System.out.println("incorrect fields!");
                correctPassword = false;

            } else if (response.equals("incorrect")) {
                System.out.println("incorrect fields!");
                correctPassword = false;

            } else if (!response.equals("")) {
                System.out.println("password is correct!");
                correctPassword = true;

            }

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


    void changePassword(){
        // Make the json object
        JSONObject user = new JSONObject();
        try {
            user.put("email", account.getEmail());
            user.put("password", new_password.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(user);

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://a21iot03.studev.groept.be/public/editPassword";

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
}