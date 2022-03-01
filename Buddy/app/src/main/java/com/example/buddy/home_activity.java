package com.example.buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class home_activity extends AppCompatActivity {
    //variables
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declaration variables
        login_button = (Button) findViewById(R.id.home_login_button);


        //onClickListeners
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_login_activity();
            }
        });


    }

    void open_login_activity(){
        //if logged in, go to account else go to login
        if(FirebaseAuth.getInstance() != null){
            Intent intent = new Intent(this, account_activity.class);
            startActivity(intent);
        } else{
            Intent intent = new Intent(this, login_activity.class);
            startActivity(intent);
        }

    }
}