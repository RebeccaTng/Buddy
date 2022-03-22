package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import be.kuleuven.buddy.R;

public class Login extends AppCompatActivity {
    TextView email, password, errorMessage;
    int login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        System.out.println("going to Home!");
        Intent goToHome = new Intent(this, Home.class);
        startActivity(goToHome);

    }

    public void checkLogin(View caller){
        //transition
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        //make json object from filled fields
        email = findViewById(R.id.emailFill);
        password = findViewById(R.id.passwFill);
        errorMessage = findViewById(R.id.incorrectPasw);

        //error message when no field is filled in
        if (email.getText().toString().equals("") || password.getText().toString().equals("")){
            errorMessage.setVisibility(View.VISIBLE);
        }

        //make the json object
        JSONObject user = new JSONObject();
        try {
            user.put("email", email.getText().toString());
            user.put("password", password.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://a21iot03.studev.groept.be/public/login";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY", response);
            System.out.println(response);
            login = Integer.parseInt(response);

            if (login == 1){
                System.out.println("logged in!");
                goHome(caller);
            }
            else if (login == 0){
                System.out.println("incorrect fields!");
                errorMessage.setText(R.string.incorrectPassw);
                password.setBackgroundResource(R.drawable.fill_bg_red);
            }
            else if(login == -1){
                System.out.println("incorrect fields!");
                errorMessage.setText(R.string.incorrectEmail);
            }
            errorMessage.setVisibility(View.VISIBLE);
            email.setBackgroundResource(R.drawable.fill_bg_red);

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