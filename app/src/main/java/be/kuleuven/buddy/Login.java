package be.kuleuven.buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Login extends AppCompatActivity {
    TextView email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goRegister(View caller) {
        Intent goToRegister = new Intent(this, Register.class);
        startActivity(goToRegister);
    }

    public void goHome(View caller){
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