package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.other.InputFilterMinMax;

public class AddLibrary extends AppCompatActivity {

    EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageMonths;
    int moistMinDB, moistMaxDB, lightMinDB, lightMaxDB, tempMinDB, tempMaxDB;
    ImageView image;
    TextView species;
    AccountInfo accountInfo;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_library);

        if(getIntent().hasExtra("accountInfo")) accountInfo = getIntent().getExtras().getParcelable("accountInfo");
        if(getIntent().hasExtra("libId")) getSpeciesData(getIntent().getExtras().getInt("libId"));

        image = findViewById(R.id.plantImage_addLib);
        species = findViewById(R.id.plantSpecies_addLib);
        moistMin = findViewById(R.id.moistMin_addLib);
        moistMax = findViewById(R.id.moistMax_addLib);
        lightMin = findViewById(R.id.lightMin_addLib);
        lightMax = findViewById(R.id.lightMax_addLib);
        tempMin = findViewById(R.id.tempMin_addLib);
        tempMax = findViewById(R.id.tempMax_addLib);
        waterlvl = findViewById(R.id.waterMin_addLib);
        ageMonths = findViewById(R.id.ageMonth_addLib);
        loading = findViewById(R.id.loading_addLib);

        loading.setVisibility(View.VISIBLE);
        image.setVisibility(View.INVISIBLE);
        species.setVisibility(View.INVISIBLE);

        // Set minimum and maximum value
        moistMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        moistMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        waterlvl.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        ageMonths.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "12")});
    }

    @Override
    public void onBackPressed() {
        Intent goToLibrary = new Intent(this, Library.class);
        goToLibrary.putExtra("accountInfo", accountInfo);
        startActivity(goToLibrary);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void getSpeciesData(int id) {
        // Make the json object for the body of the get request
        JSONObject loadSpecies = new JSONObject();
        try {
            loadSpecies.put("type", "UsersInfo");
            loadSpecies.put("speciesId", id);
        } catch (JSONException e) { e.printStackTrace(); }
        System.out.println(loadSpecies);

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/library/getSpecies";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        System.out.println(response);
                        JSONObject data = response.getJSONObject("data");

                        //check if login is valid
                        if(Rmessage.equals("SpeciesLoaded")){

                            species.setText(data.getString("species"));
                            byte[] decodedImage = Base64.decode(data.getString("image"), Base64.DEFAULT);
                            image.setImageBitmap(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length));
                            moistMinDB = data.getInt("minMoist");
                            moistMaxDB = data.getInt("maxMoist");
                            lightMinDB = data.getInt("minLight");
                            lightMaxDB = data.getInt("maxLight");
                            tempMinDB = data.getInt("minTemp");
                            tempMaxDB = data.getInt("maxTemp");
                            System.out.println(tempMaxDB);

                            loading.setVisibility(View.INVISIBLE);
                            image.setVisibility(View.VISIBLE);

                        } else{
                            loading.setVisibility(View.INVISIBLE);
                            species.setText(R.string.error);
                            species.setTextSize(20);
                            species.setTextColor(ContextCompat.getColor(this, R.color.red));
                        }
                        species.setVisibility(View.VISIBLE);
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    //process an error
                    loading.setVisibility(View.INVISIBLE);
                    species.setText(R.string.error);
                    species.setTextSize(20);
                    species.setTextColor(ContextCompat.getColor(this, R.color.red));
                    species.setVisibility(View.VISIBLE);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                // Request body goes here
                return loadSpecies.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accountInfo.getToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    // TODO go to addPlant after delete btn
}