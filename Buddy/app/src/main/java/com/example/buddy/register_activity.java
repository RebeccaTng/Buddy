package com.example.buddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register_activity extends AppCompatActivity {
    //variables
    EditText name, email, password;
    Button register_button;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //declare variables
        name = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register_button = findViewById(R.id.register_button);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.register_progressbar);


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get values that are entered
                String entered_email = email.getText().toString().trim();
                String entered_password = password.getText().toString().trim();

                //validation of entered data
                validate_entered_data(entered_email, entered_password);
            }
        });

    }

    void validate_entered_data(String entered_email, String entered_password){
        //is email filled in?
        if (TextUtils.isEmpty(entered_email)){
            email.setError("E-mail is required.");
            return;
        }
        //is password filled in?
        if (TextUtils.isEmpty(entered_password)){
            password.setError("Password is required.");
            return;
        }
        //is password long enough?
        if (entered_password.length() < 6){
            password.setError("Password must have more than 6 characters.");
            return;
        }

        //register user if all conditions are good + activate progressbar
        progressBar.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(entered_email, entered_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(register_activity.this, "User created.", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent(register_activity.this, home_activity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(register_activity.this, "Error has occured." + task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }
}