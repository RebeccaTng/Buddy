package be.kuleuven.buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    TextView email, password;

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
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        email = (TextView) findViewById(R.id.emailFill);
        password = (TextView) findViewById(R.id.passwFill);

        //make the json object
        JSONObject user = new JSONObject();
        try {
            user.put("email", email.getText().toString());
            user.put("password", password.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("test\n");
        System.out.println(user);



    }
}