package be.kuleuven.buddy.other;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.activities.Start;

public class TokenCheck {

    private final String email;
    private final Context context;

    public TokenCheck(String email, Context context) {
        this.email = email;
        this.context = context;
    }

    public void checkExpired() {
        JSONObject tokenCheck = new JSONObject();
        try { tokenCheck.put("email", email);
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://a21iot03.studev.groept.be/public/api/isExpired";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");
                        if(!Rmessage.equals("NotExpired")) goStart();

                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> goStart())
        {
            @Override
            public String getBodyContentType() { return "application/json; charset=utf-8"; }

            @Override
            public byte[] getBody() { return tokenCheck.toString().getBytes(StandardCharsets.UTF_8); }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public void goStart() {
        Toast toast = Toast.makeText(context, R.string.expired, Toast.LENGTH_LONG);
        toast.show();

        Intent goToStart = new Intent(context, Start.class);
        context.startActivity(goToStart);
    }
}
