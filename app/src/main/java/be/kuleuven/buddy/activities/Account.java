package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;

public class Account extends AppCompatActivity {

    private AccountInfo accountInfo;
    private TextView username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_account);

        username = findViewById(R.id.dyn_username_acc);
        email = findViewById(R.id.dyn_email_acc);
        AppCompatButton delete = findViewById(R.id.deleteAcc_btn);

        if(getIntent().hasExtra("accountInfo")) {
            accountInfo = getIntent().getExtras().getParcelable("accountInfo");
            username.setText(accountInfo.getUsername());
            email.setText(accountInfo.getEmail());
        }

        delete.setOnClickListener(view -> {
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlert));
            deleteBuilder.setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> deleteFromDatabase())
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        });
    }

    @Override
    public void onBackPressed() {
        Intent goToHome = new Intent(this, Home.class);
        goToHome.putExtra("accountInfo", accountInfo);
        startActivity(goToHome);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goEditAccount(View caller) {
        Intent goToEditAccount = new Intent(this, EditAccount.class);
        goToEditAccount.putExtra("accountInfo", accountInfo);
        startActivity(goToEditAccount);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goStart(View caller) {
        Intent goToStart = new Intent(this, Start.class);
        startActivity(goToStart);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void deleteFromDatabase() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/user/deleteAcc";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if(Rmessage.equals("AccountDeleted")) {
                            String Rcomment = response.getString("comment");
                            Toast toast = Toast.makeText(getApplicationContext(), Rcomment, Toast.LENGTH_LONG);
                            toast.show();

                            Intent goToStart = new Intent(this, Start.class);
                            goToStart.putExtra("accountInfo", accountInfo);
                            startActivity(goToStart);
                            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                            finish();

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG);
                    toast.show();
                })
        {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accountInfo.getToken());
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
}