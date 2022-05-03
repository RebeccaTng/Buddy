package be.kuleuven.buddy.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.other.FieldChecker;

public class AddManual extends AppCompatActivity {

    AppCompatButton addPic;
    Button addPlant;
    ImageView picPreview;
    ActivityResultLauncher<String> getImage;
    EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place, name, species;
    TextView errorMessage;
    AccountInfo accountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_manual);

        if(getIntent().hasExtra("accountInfo")) accountInfo = getIntent().getExtras().getParcelable("accountInfo");

        addPic = findViewById(R.id.addPicBtn);
        picPreview = findViewById(R.id.picturePreview);
        addPlant = findViewById(R.id.addNewPlantBtn);
        moistMin = findViewById(R.id.moistMin_addManual);
        moistMax = findViewById(R.id.moistMax_addManual);
        lightMin = findViewById(R.id.lightMin_addManual);
        lightMax = findViewById(R.id.lightMax_addManual);
        tempMin = findViewById(R.id.tempMin_addManual);
        tempMax = findViewById(R.id.tempMax_addManual);
        waterlvl = findViewById(R.id.waterMin_addManual);
        ageYears = findViewById(R.id.ageYear_addManual);
        ageMonths = findViewById(R.id.ageMonth_addManual);
        place = findViewById(R.id.placeName_addManual);
        name = findViewById(R.id.nameName_addManual);
        species = findViewById(R.id.speciesName_addManual);
        errorMessage = findViewById(R.id.error_addManual);

        // Set minimum and maximum value
        FieldChecker fieldChecker = new FieldChecker(moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place , name, species, errorMessage);
        fieldChecker.setFilters();

        // Get image from library and set in preview
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            picPreview.setImageURI(result);
            picPreview.setVisibility(View.VISIBLE);
        });

        addPic.setOnClickListener(view -> getImage.launch("image/*"));

        addPlant.setOnClickListener(view -> {
            if(fieldChecker.addPlant()) sendDatabase();
        });
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

    private void sendDatabase() {
        // Image to bitmap
        BitmapDrawable image = (BitmapDrawable) picPreview.getDrawable();
        Bitmap bitmap = image.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

        // Make the json object for the body of the put request
        JSONObject data = new JSONObject();
        try {
            data.put("minMoist", moistMin.getText().toString());
            data.put("maxMoist", moistMax.getText().toString());
            data.put("minLight", lightMin.getText().toString());
            data.put("maxLight", lightMax.getText().toString());
            data.put("minTemp", tempMin.getText().toString());
            data.put("maxTemp", tempMax.getText().toString());
            data.put("waterlvl", waterlvl.getText().toString());
            data.put("plantDate", calculatePlantDate());
            data.put("place", place.getText().toString());
            data.put("name", name.getText().toString());
            data.put("species", species.getText().toString());
            data.put("image", image_str);
        } catch (JSONException e) { e.printStackTrace(); }

        JSONObject addPlantManual = new JSONObject();
        try {
            addPlantManual.put("type", "UsersInfo");
            addPlantManual.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/library/addManual";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.PUT, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        String Rcomment = response.getString("comment");

                        //check if login is valid
                        if(Rmessage.equals("PlantManualAddSucces")){
                            // Toast
                            Toast toast = Toast.makeText(getApplicationContext(), Rcomment, Toast.LENGTH_LONG);
                            toast.show();

                            // Go to back to library
                            Intent goToAddPlant = new Intent(this, Library.class);
                            goToAddPlant.putExtra("accountInfo", accountInfo);
                            startActivity(goToAddPlant);
                            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                        } else if(Rmessage.equals("AddSpeciesFailed")) {
                            errorMessage.setText(R.string.speciesExists);
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
                return addPlantManual.toString().getBytes(StandardCharsets.UTF_8);
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

    private String calculatePlantDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat plantDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar.add(Calendar.MONTH, -Integer.parseInt(ageMonths.getText().toString()));
        calendar.add(Calendar.YEAR, -Integer.parseInt((ageYears.getText().toString())));
        return plantDate.format(calendar.getTime());
    }
}