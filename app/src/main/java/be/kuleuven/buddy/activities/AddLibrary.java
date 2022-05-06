package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.other.FieldChecker;

public class AddLibrary extends AppCompatActivity {

    EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place, name;
    int moistMinDB, moistMaxDB, lightMinDB, lightMaxDB, tempMinDB, tempMaxDB, speciesId;
    ImageView image;
    TextView species, errorMessage;
    AccountInfo accountInfo;
    ProgressBar loading;
    AppCompatButton useStandard, delete;
    Button addPlant;
    FieldChecker fieldChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_library);

        if(getIntent().hasExtra("accountInfo")) accountInfo = getIntent().getExtras().getParcelable("accountInfo");
        if(getIntent().hasExtra("libId")) {
            speciesId = getIntent().getExtras().getInt("libId");
            getSpeciesData(speciesId);
        }

        image = findViewById(R.id.plantImage_addLib);
        species = findViewById(R.id.plantSpecies_addLib);
        moistMin = findViewById(R.id.moistMin_addLib);
        moistMax = findViewById(R.id.moistMax_addLib);
        lightMin = findViewById(R.id.lightMin_addLib);
        lightMax = findViewById(R.id.lightMax_addLib);
        tempMin = findViewById(R.id.tempMin_addLib);
        tempMax = findViewById(R.id.tempMax_addLib);
        waterlvl = findViewById(R.id.waterMin_addLib);
        ageYears = findViewById(R.id.ageYear_addLib);
        ageMonths = findViewById(R.id.ageMonth_addLib);
        place = findViewById(R.id.placeName_addLib);
        name = findViewById(R.id.nameName_addLib);
        errorMessage = findViewById(R.id.error_addLib);
        loading = findViewById(R.id.loading_addLib);
        useStandard = findViewById(R.id.standardSettings_addLib);
        delete = findViewById(R.id.delete_addLib);
        addPlant = findViewById(R.id.addPlantBtn_addLib);

        loading.setVisibility(View.VISIBLE);
        image.setVisibility(View.INVISIBLE);
        species.setVisibility(View.INVISIBLE);

        fieldChecker = new FieldChecker(moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place, name, null, errorMessage, 1);
        fieldChecker.setFilters();

        useStandard.setOnClickListener(view -> {
            moistMin.setText(String.valueOf(moistMinDB));
            moistMax.setText(String.valueOf(moistMaxDB));
            lightMin.setText(String.valueOf(lightMinDB));
            lightMax.setText(String.valueOf(lightMaxDB));
            tempMin.setText(String.valueOf(tempMinDB));
            tempMax.setText(String.valueOf(tempMaxDB));
        });

        addPlant.setOnClickListener(view -> { if(fieldChecker.addPlant()) sendData(checkStandardOrPersonalized()); });
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
        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/library/getSpecies/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
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

                            loading.setVisibility(View.INVISIBLE);
                            image.setVisibility(View.VISIBLE);
                            species.setVisibility(View.VISIBLE);
                            useStandard.setClickable(true);
                            delete.setClickable(true);
                            addPlant.setClickable(true);

                        } else{
                            errorMessage.setText(R.string.error);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    //process an error
                    loading.setVisibility(View.INVISIBLE);
                    errorMessage.setText(R.string.error);
                    errorMessage.setVisibility(View.VISIBLE);
                })
        {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accountInfo.getToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private Boolean checkStandardOrPersonalized() {
        return !moistMin.getText().toString().equals(String.valueOf(moistMinDB)) || !moistMax.getText().toString().equals(String.valueOf(moistMaxDB))
                || !lightMin.getText().toString().equals(String.valueOf(lightMinDB)) || !lightMax.getText().toString().equals(String.valueOf(lightMaxDB))
                || !tempMin.getText().toString().equals(String.valueOf(tempMinDB)) || !tempMax.getText().toString().equals(String.valueOf(tempMaxDB));
    }

    private void sendData(Boolean personalized) {
        // Make the json object for the body of the put request
        JSONObject data = new JSONObject();
        try {
            data.put("speciesId", speciesId);
            data.put("name", name.getText().toString());
            data.put("plantDate", fieldChecker.calculatePlantDate());
            data.put("place", place.getText().toString());
            data.put("waterlvl", waterlvl.getText().toString());
            if(personalized) {
                data.put("personalized", 1);
                data.put("minMoist", moistMin.getText().toString());
                data.put("maxMoist", moistMax.getText().toString());
                data.put("minLight", lightMin.getText().toString());
                data.put("maxLight", lightMax.getText().toString());
                data.put("minTemp", tempMin.getText().toString());
                data.put("maxTemp", tempMax.getText().toString());
            } else data.put("personalized", 0);
        } catch (JSONException e) { e.printStackTrace(); }

        JSONObject addPlantLibrary = new JSONObject();
        try {
            addPlantLibrary.put("type", "UsersInfo");
            addPlantLibrary.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/library/addLibrary";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.PUT, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        String Rcomment = response.getString("comment");

                        //check if login is valid
                        if(Rmessage.equals("PlantLibraryAddSucces")){
                            // Toast
                            Toast toast = Toast.makeText(getApplicationContext(), Rcomment, Toast.LENGTH_LONG);
                            toast.show();
                            System.out.println("PLANT LIBRARY ADD SUCCES");
                            // Go to back to library
                            Intent goToHome = new Intent(this, Home.class);
                            goToHome.putExtra("accountInfo", accountInfo);
                            startActivity(goToHome);
                            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                        } else if(Rmessage.equals("AddPlantFailed")) {
                            errorMessage.setText(R.string.plantExists);
                            errorMessage.setVisibility(View.VISIBLE);
                        } else{
                            errorMessage.setText(R.string.error);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    //process an error
                    errorMessage.setText(R.string.error);
                    errorMessage.setVisibility(View.VISIBLE);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                // Request body goes here
                return addPlantLibrary.toString().getBytes(StandardCharsets.UTF_8);
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