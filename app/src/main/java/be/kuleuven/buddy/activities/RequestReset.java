package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import be.kuleuven.buddy.R;

public class RequestReset extends AppCompatActivity {

    private TextView errorMessage;
    private TextView email;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_request_reset);

        email = findViewById(R.id.emailFill_request);
        errorMessage = findViewById(R.id.errorMessage_reset);
        loading = findViewById(R.id.loading_reset);
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

    public void sendmail(View caller) {
        errorMessage.setVisibility(View.INVISIBLE);
        email.setBackgroundResource(R.drawable.bg_fill);

        JSONObject emailBody = new JSONObject();
        try {
            emailBody.put("email", email.getText().toString());
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/forgotPassword";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if (Rmessage.equals("NoAccount")) {
                            errorMessage.setText(R.string.incorrectEmail);
                            errorMessage.setVisibility(View.VISIBLE);
                            email.setBackgroundResource(R.drawable.bg_fill_red);

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.mailSucces, Toast.LENGTH_LONG);
                            toast.show();
                            loading.setVisibility(View.VISIBLE);
                            goLogin(caller);
                        }

                    } catch (JSONException e) { e.printStackTrace(); }
                },

                error -> {
                    System.out.println(error.toString());
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.mailSucces, Toast.LENGTH_LONG);
                    toast.show();
                    loading.setVisibility(View.VISIBLE);
                    goLogin(caller);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() { return emailBody.toString().getBytes(StandardCharsets.UTF_8); }
        };

        requestQueue.add(jsonObjectRequest);
    }
}