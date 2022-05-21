package be.kuleuven.buddy.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;

public class PlantStatistics extends AppCompatActivity {

    AccountInfo accountInfo;
    int plantId;
    ImageView image;
    TextView name, species, age, place, lastWater, waterTank, connected, status, moist, light, temp, userMessage;
    View waterlvl, connectedIcon;
    ProgressBar loading;
    String plantDate;
    SwipeRefreshLayout swipeRefresh;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_plant_statistics);

        if(getIntent().hasExtra("accountInfo")) { accountInfo = getIntent().getExtras().getParcelable("accountInfo"); }

        if(getIntent().hasExtra("plantId")) {
            plantId = getIntent().getExtras().getInt("plantId");
            getPlantData(false);
        }

        image = findViewById(R.id.dyn_plantImage_stat);
        name = findViewById(R.id.dyn_plantName_stat);
        species = findViewById(R.id.dyn_plantSpecies_stat);
        age = findViewById(R.id.dyn_plantAge_stat);
        place = findViewById(R.id.dyn_plantPlace_stat);
        lastWater = findViewById(R.id.dyn_plantWater_stat);
        waterTank = findViewById(R.id.dyn_tank_stat);
        waterlvl = findViewById(R.id.meter);
        connected = findViewById(R.id.connected);
        connectedIcon = findViewById(R.id.connected_icon);
        status = findViewById(R.id.dyn_plantStatus_stat);
        moist = findViewById(R.id.dyn_plantMoisture_stat);
        light = findViewById(R.id.dyn_plantLight_stat);
        temp = findViewById(R.id.dyn_plantTemp_stat);
        userMessage = findViewById(R.id.error_stat);
        loading = findViewById(R.id.loading_stat);

        swipeRefresh = findViewById(R.id.refreshLayout_stat);
        swipeRefresh.setColorSchemeResources(R.color.orange);
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.beige);
        swipeRefresh.setEnabled(false);

        swipeRefresh.setOnRefreshListener(() -> {
            image.setImageBitmap(null);
            loading.setVisibility(View.VISIBLE);
            getPlantData(true);
            swipeRefresh.setRefreshing(false); // explicitly refreshes only once. If "true" it implicitly refreshes forever
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
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

    public void goPlantSettings(View caller) {
        Intent goToPlantSettings = new Intent(this, PlantSettings.class);
        goToPlantSettings.putExtra("accountInfo", accountInfo);
        goToPlantSettings.putExtra("plantId", plantId);
        startActivity(goToPlantSettings);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goOverview(View caller) {
        Intent goToOverview = new Intent(this, Overview.class);
        goToOverview.putExtra("accountInfo", accountInfo);
        goToOverview.putExtra("plantDate", plantDate);
        goToOverview.putExtra("plantId", plantId);
        startActivity(goToOverview);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPlantData(Boolean notification) {
        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/home/plantStatistics/" + plantId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        JSONObject plantData, sensorData, alertPlant, minmax;
                        plantData = response.getJSONObject("plantData");
                        int sensorDataIsNull = response.getInt("sensorIsNull");
                        if(sensorDataIsNull == 0) sensorData = response.getJSONObject("sensorData");
                        else sensorData = null;
                        alertPlant = response.getJSONObject("alertPlant");
                        minmax = response.getJSONObject("minmax");

                        //check if login is valid
                        if(Rmessage.equals("StatsLoaded")) {
                            processData(plantData, sensorData, sensorDataIsNull);
                            if(notification) checkAlerts(alertPlant, minmax, sensorDataIsNull);
                            loading.setVisibility(View.INVISIBLE);
                            swipeRefresh.setEnabled(true);
                        } else{
                            loading.setVisibility(View.INVISIBLE);
                            userMessage.setText(R.string.error);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    //process an error
                    loading.setVisibility(View.INVISIBLE);
                    userMessage.setText(R.string.error);
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void checkAlerts(JSONObject alertPlant, JSONObject minmax, int sensorDataIsNull) throws JSONException {
        String title, smallText, largeText;
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        if(sensorDataIsNull == 0) {
            if(alertPlant.getInt("tankAlert") == 1 && Integer.parseInt(waterTank.getText().toString()) < alertPlant.getInt("minWaterLvl")) {
                title = "Water Tank Alert";
                smallText = "Water tank level is too low";
                largeText = "The water tank level is under your given minimum. Please refill the tank.";
                inboxStyle.addLine(title + " " + smallText);
                notification(title, smallText, largeText, 1);
            }

            if(alertPlant.getInt("lightAlert") == 1) {
                title = "Light Alert";
                int lightPercent = Integer.parseInt(light.getText().toString());
                if(lightPercent < minmax.getInt("minLight")) {
                    smallText = "Too little light";
                    largeText = "There is less light here than your given minimum. Please move the plant to a place with more light";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, 2);
                } else if(lightPercent > minmax.getInt(("maxLight"))) {
                    smallText = "Too much light";
                    largeText = "There is more light here than your given maximum. Please move the plant to a place with less light";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, 2);
                }
            }

            if(alertPlant.getInt("tempAlert") == 1) {
                title = "Temperature Alert";
                int tempPercent = Integer.parseInt(temp.getText().toString());
                if(tempPercent < minmax.getInt("minTemp")) {
                    smallText = "Too cold";
                    largeText = "It is colder here than your given minimum. Please move the plant to a warmer place";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, 3);
                } else if(tempPercent > minmax.getInt(("maxTemp"))) {
                    smallText = "Too warm";
                    largeText = "It is warmer here than your given maximum. Please move the plant to a cooler place";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, 3);
                }
            }

            NotificationCompat.Builder summaryNotification = new NotificationCompat.Builder(this, "My Notification")
                    .setContentTitle(name.getText().toString() + " needs you")
                    .setContentText("Give your plant some attention")
                    .setSmallIcon(R.drawable.logo_small)
                    .setStyle(inboxStyle
                            .setBigContentTitle("New Messages")
                            .setSummaryText(name.getText().toString()))
                    .setContentIntent(notificationIntent())
                    .setGroup("notificationGroup")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setGroupSummary(true)
                    .setAutoCancel(true);

            NotificationManagerCompat.from(this).notify(4, summaryNotification.build());
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent notificationIntent() {
        Intent intent = new Intent(this, PlantStatistics.class);
        intent.putExtra("accountInfo", accountInfo);
        intent.putExtra("plantId", plantId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void notification(String title, String smallText, String largeText, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "My Notification")
            .setContentTitle(name.getText().toString() + ": " + title)
            .setContentText(smallText)
            .setSmallIcon(R.drawable.logo_small)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(largeText))
            .setContentIntent(notificationIntent())
            .setGroup("notificationGroup")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(id, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processData(JSONObject plantData, JSONObject sensorData, int sensorDataIsNull) throws JSONException {

        name.setText(plantData.getString("name"));
        species.setText(plantData.getString("species"));
        plantDate = plantData.getString("plantDate");
        setAge(plantDate);
        place.setText(plantData.getString("place"));
        setLastWater(plantData.getString("lastWatered"));
        if (plantData.getInt("connected") == 1) {
            connected.setText(R.string.blufiConnected);
            connectedIcon.setBackgroundResource(R.drawable.online);
        } else {
            connected.setText(R.string.blufiNotConnected);
            connectedIcon.setBackgroundResource(R.drawable.offline);
        }
        status.setText(plantData.getString("status"));

        if(sensorDataIsNull == 0) {
            setWaterTankLvl(sensorData.getInt("tankLvl"));
            moist.setText(sensorData.getString("moistData"));
            light.setText(sensorData.getString("lightData"));
            temp.setText(sensorData.getString("tempData"));
        } else {
            waterlvl.setVisibility(View.INVISIBLE);
            waterTank.setText("-");
            moist.setText(" -");
            light.setText(" -");
            temp.setText(" -");
        }

        byte[] decodedImage = Base64.decode(plantData.getString("image"), Base64.DEFAULT);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        image.setImageBitmap(imageBitmap);
    }

    private void setLastWater(String plantWater) {

        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        if(!plantWater.equals("null")) {
            try {
                calendar.setTime(Objects.requireNonNull(oldDateFormat.parse(plantWater)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            lastWater.setText(newDateFormat.format(calendar.getTime()));
        } else lastWater.setText("-");
    }

    private void setWaterTankLvl(int waterTankLvl) {

        if(waterTankLvl != 0) {
            ViewGroup.LayoutParams layoutParams = waterlvl.getLayoutParams();
            final float scale = this.getResources().getDisplayMetrics().density;
            layoutParams.height = (int) (0.6 * waterTankLvl * scale + 0.5f); // Convert dp to pixels
            waterlvl.setLayoutParams(layoutParams);
            waterlvl.setVisibility(View.VISIBLE);
        }
        waterTank.setText(String.valueOf(waterTankLvl));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAge(String plantDate) {

        int year = 0;
        int month = 0;
        String ageStr1, ageStr2;
        LocalDate dob = LocalDate.parse(plantDate);
        LocalDate curDate = LocalDate.now();
        if ((dob != null) && (curDate != null)) {
            year = Period.between(dob, curDate).getYears();
            month = Period.between(dob, curDate).getMonths();
        }

        if (year == 1) ageStr1 = year + " year & ";
        else ageStr1 = year + " years & ";
        if (month == 1) ageStr2 = ageStr1.concat(month + " month");
        else ageStr2 = ageStr1.concat(month + " months");

        age.setText(ageStr2);
    }
}