package com.example.buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class account_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity( new Intent(getApplicationContext(), login_activity.class));
        finish();
    }
}