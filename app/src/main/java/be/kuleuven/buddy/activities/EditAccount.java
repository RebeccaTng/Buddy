package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.fragments.PasswInfoFragment;

public class EditAccount extends AppCompatActivity {
    //declare variables
    AccountInfo accountInfo;
    TextView email, errorMessage;
    EditText username, current_password, new_password, confirm_password;
    ImageView infoBtn;

    boolean correctNewPassword, correctConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_edit_account);

        accountInfo = getIntent().getExtras().getParcelable("accountInfo");

        // Link the variables
        email = findViewById(R.id.dyn_email_edit);
        username = findViewById(R.id.dyn_emailFill_edit);
        current_password = findViewById(R.id.currPasswFill_edit);
        new_password = findViewById(R.id.newPasswFill_edit);
        confirm_password = findViewById(R.id.confPasswFill_edit);
        errorMessage = findViewById(R.id.errorMessage_editAccount);
        infoBtn = findViewById(R.id.infoIcon_edit);

        // Initial values
        email.setText(accountInfo.getEmail());
        username.setHint(accountInfo.getUsername());

        // Display password requirements
        infoBtn.setOnClickListener(view -> {
            PasswInfoFragment passwInfo = new PasswInfoFragment();
            passwInfo.show(getSupportFragmentManager(), "passwFragment");
        });

        textWatchers();
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goAccount(View caller) {
        Intent goToAccount = new Intent(this, Account.class);
        goToAccount.putExtra("accountInfo", accountInfo);
        startActivity(goToAccount);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();
    }

    public void processChanges(View caller){
        String name = username.getText().toString();
        String currentPassword = current_password.getText().toString();
        String newPassword = new_password.getText().toString();
        String confirmPassword = confirm_password.getText().toString();

        clearBorders();

        // If only name is changed
        if (currentPassword.length() == 0 && newPassword.length() == 0 && confirmPassword.length() == 0) {
            updateDatabase(caller, name, "", "");
        }

        // If password is changed
        else if (correctConfirmPassword && correctNewPassword){
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedhashOldPassword = digest.digest(currentPassword.getBytes(StandardCharsets.UTF_8));
                byte[] encodedhashNewPassword = digest.digest(newPassword.getBytes(StandardCharsets.UTF_8));
                currentPassword = bytesToHex(encodedhashOldPassword);
                newPassword = bytesToHex(encodedhashNewPassword);
            } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

            updateDatabase(caller, name, currentPassword, newPassword);
        }

        // Give an error message
        else {
            errorMessage.setText(R.string.fillFields);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    public void setName(){
        // Check if username is changed
        if (username.getText().toString().equals("")) {
            username.setText(accountInfo.getUsername());
        }
        accountInfo.setUsername(username.getText().toString());
    }


    public void textWatchers(){
        // New password textwatcher
        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
                String newPassword = new_password.getText().toString();
                confirm_password.setText("");
                //if longer than 8, contains a number and upper case
                if (newPassword.length() >= 8 && newPassword.matches("(.*[0-9].*)") && newPassword.matches("(.*[A-Z].*)") ){
                    correctNewPassword = true;
                    new_password.setBackgroundResource(R.drawable.bg_fill_green);

                } else {
                    correctNewPassword = false;
                    new_password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Confirm password textwatcher
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
                if (confirm_password.getText().toString().equals(new_password.getText().toString()) && confirm_password.getText().length() !=0 ){
                    correctConfirmPassword = true;
                    confirm_password.setBackgroundResource(R.drawable.bg_fill_green);

                } else if (confirm_password.getText().equals("")){
                    new_password.setBackgroundResource(R.drawable.bg_fill);

                } else {
                    correctConfirmPassword = false;
                    confirm_password.setBackgroundResource(R.drawable.bg_fill_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        current_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
                current_password.setBackgroundResource(R.drawable.bg_fill);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public void updateDatabase(View caller, String username, String oldPassword, String newPassword){
        // Make the json object for the body of the post request
        JSONObject login = new JSONObject();
        try {
            // Hash password
            login.put("username", username);
            login.put("oldpassword",oldPassword);
            login.put("newpassword",newPassword);

        } catch (JSONException e) { e.printStackTrace(); }

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/user/account";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.PUT, url, null,
                response -> {
                    //process the response
                    System.out.println("response: " + response);
                    try {
                        String Rmessage = response.getString("message");

                        if (Rmessage.equals("UserUpdateSuccess")){
                            setName();
                            goAccount(caller);

                        } else if (Rmessage.equals("PasswordMatchFailed")){
                            errorMessage.setText(R.string.wrongPassword);
                            errorMessage.setVisibility(View.VISIBLE);
                            current_password.setBackgroundResource(R.drawable.bg_fill_red);

                        } else {
                            errorMessage.setText(R.string.fillFields);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e){ e.printStackTrace(); }},

                error -> {
                    //TODO: handle the error

                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    // request body goes here
                    return login.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", login, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accountInfo.getToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void clearBorders(){
        errorMessage.setVisibility(View.INVISIBLE);
        username.setBackgroundResource(R.drawable.bg_fill);
        new_password.setBackgroundResource(R.drawable.bg_fill);
        confirm_password.setBackgroundResource(R.drawable.bg_fill);
        current_password.setBackgroundResource(R.drawable.bg_fill);
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
}