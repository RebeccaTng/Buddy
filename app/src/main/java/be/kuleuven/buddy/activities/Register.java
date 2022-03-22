package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import be.kuleuven.buddy.R;

public class Register extends AppCompatActivity {
    TextView username, email, password, confirmPassword;
    boolean correctPassword, validEmail;


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
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (email.length() == 0){
                    //display that you need to fill in a password
                    System.out.println("You need to fill in a password!\n");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String currentPassword = password.getText().toString();
                //if longer than 8, contains a number and upper case
                if (currentPassword.length() >= 8 && currentPassword.matches("(.*[0-9].*)") && currentPassword.matches("(.*[A-Z].*)") ){
                    //display that the password is correct format
                    System.out.println("Password is the correct format!\n");
                }
                else{
                    System.out.println("Password is not the correct format\n");
                }
            }
        });

        //textwatcher confirmPassword
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (confirmPassword.getText().toString().equals(password.getText().toString())){
                    System.out.println("your password is correct\n");
                }
                else {
                    System.out.println("Password is incorrect!\n");
                }
            }
        });

        //textwatcher email. It checks if email is already in database
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
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
        Intent goToHome = new Intent(this, Home.class);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void checkRegister(View caller){

    }


    public void checkValidEmail(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://a21iot03.studev.groept.be/public/checkEmail/";
        URL = URL + email.getText().toString();
        System.out.println(URL);

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, URL, null,

                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        //String valid = response.getJSONObject(0).getBoolean("valid");
                        System.out.println(response);
                    }
                },

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("onresponse is fout");
                    }
                }
        );
        requestQueue.add(submitRequest);

    }
}