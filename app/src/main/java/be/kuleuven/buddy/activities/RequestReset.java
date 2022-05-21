package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import be.kuleuven.buddy.account.AccountInfo;

public class RequestReset extends AppCompatActivity {

    TextView errorMessage;
    TextView email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_request_reset);

        email = findViewById(R.id.emailFill_request);
        errorMessage = findViewById(R.id.errorMessage_reset);
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goLogin(View caller) {
        Intent goToLogin = new Intent(this, Login.class);
        startActivity(goToLogin);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    //TODO when click on request, send mail
    public void sendmail(View caller) {
        System.out.println("button clicked");
        //process the error
        errorMessage.setVisibility(View.INVISIBLE);
        email.setBackgroundResource(R.drawable.bg_fill);

        // Make the json object for the body of the post request
        JSONObject emailBody = new JSONObject();
        try {
            emailBody.put("email", email.getText().toString());
        } catch (JSONException e) { e.printStackTrace(); }

        System.out.println("body made for email:  " + email.getText().toString());

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/forgotPassword";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        System.out.println("response: " + Rmessage);

                        if (Rmessage.equals("NoAccount")){
                            errorMessage.setText(R.string.incorrectEmail);
                            errorMessage.setVisibility(View.VISIBLE);
                            email.setBackgroundResource(R.drawable.bg_fill_red);
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.mailSucces, Toast.LENGTH_LONG);
                            toast.show();
                            goLogin(caller);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                error -> {
                    //process the error
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.mailSucces, Toast.LENGTH_LONG);
                    toast.show();
                    goLogin(caller);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                // request body goes here
                return emailBody.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

}