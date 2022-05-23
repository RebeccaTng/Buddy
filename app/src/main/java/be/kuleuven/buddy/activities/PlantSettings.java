package be.kuleuven.buddy.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.other.FieldChecker;
import be.kuleuven.buddy.other.InfoFragment;

public class PlantSettings extends AppCompatActivity {

    private AccountInfo accountInfo;
    private int plantId, moistMinDB, moistMaxDB, lightMinDB, lightMaxDB, tempMinDB, tempMaxDB;
    private EditText name, moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place;
    private ImageView image;
    private TextView species, errorMessage;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch tankSwitch, lightSwitch, tempSwitch;
    private FieldChecker fieldChecker;
    private ProgressBar loading;
    private Button useStandard, delete, save;
    private Boolean prevPersonalized;
    private RequestQueue requestQueue;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_plant_settings);

        if(getIntent().hasExtra("accountInfo")) { accountInfo = getIntent().getExtras().getParcelable("accountInfo"); }
        if(getIntent().hasExtra("plantId")) {
            plantId = getIntent().getExtras().getInt("plantId");
            getPlantData();
        }

        name = findViewById(R.id.dyn_plantName_settings);
        image = findViewById(R.id.dyn_plantImage_settings);
        species = findViewById(R.id.dyn_plantSpecies_settings);
        moistMin = findViewById(R.id.moistMin_settings);
        moistMax = findViewById(R.id.moistMax_settings);
        lightMin = findViewById(R.id.lightMin_settings);
        lightMax = findViewById(R.id.lightMax_settings);
        tempMin = findViewById(R.id.tempMin_settings);
        tempMax = findViewById(R.id.tempMax_settings);
        waterlvl = findViewById(R.id.waterMin_settings);
        ageYears = findViewById(R.id.ageYear_settings);
        ageMonths = findViewById(R.id.ageMonth_settings);
        place = findViewById(R.id.placeName_settings);
        errorMessage = findViewById(R.id.error_settings);
        tankSwitch = findViewById(R.id.tankSwitch);
        lightSwitch = findViewById(R.id.lightSwitch);
        tempSwitch = findViewById(R.id.tempSwitch);
        loading = findViewById(R.id.loading_settings);
        ImageView editIcon = findViewById(R.id.editIcon);
        useStandard = findViewById(R.id.standardBtn_settings);
        delete = findViewById(R.id.deleteBtn_settings);
        save = findViewById(R.id.saveBtn_settings);
        ImageView infoBtn = findViewById(R.id.infoIconSettings);
        requestQueue = Volley.newRequestQueue(this);

        fieldChecker = new FieldChecker(moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place, name, null, errorMessage, false);

        infoBtn.setOnClickListener(view -> {
            String title = getResources().getString(R.string.howToValues);
            String body = getResources().getString(R.string.valuesTips);
            InfoFragment info = InfoFragment.newInstance(title, body);
            info.show(getSupportFragmentManager(), "infoFragment");
        });

        editIcon.setOnClickListener(view -> {
            name.requestFocus();
            name.setSelection(name.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
        });

        setBtnListeners();
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

    public void goPlantStatistics(View caller) {
        Intent goToPlantStatistics = new Intent(this, PlantStatistics.class);
        goToPlantStatistics.putExtra("plantId", plantId);
        goToPlantStatistics.putExtra("accountInfo", accountInfo);
        startActivity(goToPlantStatistics);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void setBtnListeners() {
        useStandard.setOnClickListener(view -> standardSettings());

        delete.setOnClickListener(view -> {
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlert));
            deleteBuilder.setMessage("Are you sure you want to delete \"" + name.getText().toString() + "\" from your home?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> deleteFromDatabase())
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        });

        save.setOnClickListener(view ->{
            if(fieldChecker.checkFields()) {
                useStandard.setEnabled(false);
                delete.setEnabled(false);
                save.setEnabled(false);
                sendData(checkStandardOrPersonalized());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPlantData() {
        String url = "https://a21iot03.studev.groept.be/public/api/home/plantSettings/getPlant/" + plantId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if(Rmessage.equals("SettingsLoaded")) {
                            JSONObject plantData, speciesData, sensorPersonalized;
                            plantData = response.getJSONObject("plantData");
                            speciesData = response.getJSONObject("speciesData");
                            int personalized = plantData.getInt("personalized");
                            if(personalized == 1) sensorPersonalized = response.getJSONObject("sensorPersonalized");
                            else sensorPersonalized = null;
                            processData(plantData, speciesData, sensorPersonalized, personalized);

                        } else {
                            loading.setVisibility(View.INVISIBLE);
                            errorMessage.setText(R.string.error);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    loading.setVisibility(View.INVISIBLE);
                    errorMessage.setText(R.string.error);
                });

        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processData(JSONObject plantData, JSONObject speciesData, JSONObject sensorPersonalized, int personalized) throws JSONException {
        name.setText(plantData.getString("name"));
        setAge(plantData.getString("plantDate"));
        place.setText(plantData.getString("place"));
        waterlvl.setText(plantData.getString("minWaterLvl"));
        tankSwitch.setChecked(plantData.getInt("tankAlert") == 1);
        lightSwitch.setChecked(plantData.getInt("lightAlert") == 1);
        tempSwitch.setChecked(plantData.getInt("tempAlert") == 1);

        moistMinDB = speciesData.getInt("minMoist");
        moistMaxDB = speciesData.getInt("maxMoist");
        lightMinDB = speciesData.getInt("minLight");
        lightMaxDB = speciesData.getInt("maxLight");
        tempMinDB = speciesData.getInt("minTemp");
        tempMaxDB = speciesData.getInt("maxTemp");

        if(personalized == 1) {
            moistMin.setText(sensorPersonalized.getString("minMoist"));
            moistMax.setText(sensorPersonalized.getString("maxMoist"));
            lightMin.setText(sensorPersonalized.getString("minLight"));
            lightMax.setText(sensorPersonalized.getString("maxLight"));
            tempMin.setText(sensorPersonalized.getString("minTemp"));
            tempMax.setText(sensorPersonalized.getString("maxTemp"));
            prevPersonalized = true;
        } else {
            standardSettings();
            prevPersonalized = false;
        }

        species.setText(speciesData.getString("species"));
        byte[] decodedImage = Base64.decode(speciesData.getString("image"), Base64.DEFAULT);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        image.setImageBitmap(imageBitmap);

        loading.setVisibility(View.INVISIBLE);
        useStandard.setEnabled(true);
        delete.setEnabled(true);
        save.setEnabled(true);
        fieldChecker.setFilters();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAge(String plantDate) {
        int year = 0;
        int month = 0;
        LocalDate dob = LocalDate.parse(plantDate);
        LocalDate curDate = LocalDate.now();
        if ((dob != null) && (curDate != null)) {
            year = Period.between(dob, curDate).getYears();
            month = Period.between(dob, curDate).getMonths();
        }
        ageYears.setText(String.valueOf(year));
        ageMonths.setText(String.valueOf(month));
    }

    private void standardSettings() {
        moistMin.setText(String.valueOf(moistMinDB));
        moistMax.setText(String.valueOf(moistMaxDB));
        lightMin.setText(String.valueOf(lightMinDB));
        lightMax.setText(String.valueOf(lightMaxDB));
        tempMin.setText(String.valueOf(tempMinDB));
        tempMax.setText(String.valueOf(tempMaxDB));
    }

    private void deleteFromDatabase() {
        String url = "https://a21iot03.studev.groept.be/public/api/home/plantSettings/deletePlant/" + plantId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if(Rmessage.equals("PlantDeleted")) {
                            String comment = "\"" + name.getText().toString() + "\" was successfully deleted";
                            Toast toast = Toast.makeText(getApplicationContext(), comment, Toast.LENGTH_LONG);
                            toast.show();

                            Intent goToHome = new Intent(this, Home.class);
                            goToHome.putExtra("accountInfo", accountInfo);
                            startActivity(goToHome);
                            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                            finish();

                        } else {
                            errorMessage.setText(R.string.error);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    errorMessage.setText(R.string.error);
                    errorMessage.setVisibility(View.VISIBLE);
                });

        requestQueue.add(jsonObjectRequest);
    }

    private Boolean checkStandardOrPersonalized() {
        return !moistMin.getText().toString().equals(String.valueOf(moistMinDB)) || !moistMax.getText().toString().equals(String.valueOf(moistMaxDB))
                || !lightMin.getText().toString().equals(String.valueOf(lightMinDB)) || !lightMax.getText().toString().equals(String.valueOf(lightMaxDB))
                || !tempMin.getText().toString().equals(String.valueOf(tempMinDB)) || !tempMax.getText().toString().equals(String.valueOf(tempMaxDB));
    }

    private JSONObject sendDataBody(Boolean personalized) {
        JSONObject data = new JSONObject();
        try {
            data.put("plantId", plantId);
            data.put("name", name.getText().toString());
            data.put("plantDate", fieldChecker.calculatePlantDate());
            data.put("place", place.getText().toString());
            data.put("waterlvl", waterlvl.getText().toString());
            data.put("tankAlert", tankSwitch.isChecked()? 1 : 0);
            data.put("lightAlert", lightSwitch.isChecked()? 1 : 0);
            data.put("tempAlert", tempSwitch.isChecked()? 1 : 0);
            data.put("prevPersonalized", prevPersonalized);
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

        JSONObject editSettings = new JSONObject();
        try {
            editSettings.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        return editSettings;
    }

    private void enableBtns() {
        useStandard.setEnabled(true);
        delete.setEnabled(true);
        save.setEnabled(true);
    }

    private void sendData(Boolean personalized) {
        String url = "https://a21iot03.studev.groept.be/public/api/home/plantSettings/updatePlant";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.PUT, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if(Rmessage.equals("UpdatePlantSucces")) {
                            String Rcomment = response.getString("comment");
                            Toast toast = Toast.makeText(getApplicationContext(), Rcomment, Toast.LENGTH_LONG);
                            toast.show();

                            Intent goToPlantStatistics = new Intent(this, PlantStatistics.class);
                            goToPlantStatistics.putExtra("plantId", plantId);
                            goToPlantStatistics.putExtra("accountInfo", accountInfo);
                            startActivity(goToPlantStatistics);
                            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                        } else if(Rmessage.equals("AddPlantFailed")) {
                            enableBtns();
                            errorMessage.setText(R.string.plantExists);
                            errorMessage.setVisibility(View.VISIBLE);

                        } else {
                            enableBtns();
                            errorMessage.setText(R.string.error);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    enableBtns();
                    errorMessage.setText(R.string.error);
                    errorMessage.setVisibility(View.VISIBLE);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() { return sendDataBody(personalized).toString().getBytes(StandardCharsets.UTF_8); }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
}