package be.kuleuven.buddy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.cards.HomeAdapter;
import be.kuleuven.buddy.cards.HomeInfo;

import be.kuleuven.buddy.bluetooth.ui.BlufiMain;
import be.kuleuven.buddy.other.TokenCheck;

public class Home extends AppCompatActivity implements HomeAdapter.HomeListener {

    private RecyclerView homeRecycler;
    private TextView numOfPlants, userMessage;
    private AccountInfo accountInfo;
    private ProgressBar loading;
    private final ArrayList<HomeInfo> homePlants = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_home);

        swipeRefresh = findViewById(R.id.refreshLayout_home);
        TextView username = findViewById(R.id.dyn_username_home);

        if(getIntent().hasExtra("accountInfo")) {
            accountInfo = getIntent().getExtras().getParcelable("accountInfo");
            TokenCheck tokenCheck = new TokenCheck(accountInfo.getEmail(), this);
            tokenCheck.checkExpired();
            username.setText(accountInfo.getUsername());
        }

        getData();

        loading = findViewById(R.id.loading_home);
        userMessage = findViewById(R.id.userMessage_home);
        numOfPlants = findViewById(R.id.dyn_numOfPlants);
        homeRecycler = findViewById(R.id.home_recycler);

        swipeRefresh.setColorSchemeResources(R.color.orange);
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.beige);
        swipeRefresh.setEnabled(false);

        loading.setVisibility(View.VISIBLE);
        homeRecycler.setVisibility(View.INVISIBLE);

        homeRecycler.setHasFixedSize(true);
        homeRecycler.setLayoutManager(new LinearLayoutManager(this)); // Vertical layout by default
        homeRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                swipeRefresh.setEnabled(newState == RecyclerView.SCROLL_STATE_IDLE);
            }
        });

        swipeRefresh.setOnRefreshListener(() -> {
            homePlants.clear();
            loading.setVisibility(View.VISIBLE);
            homeRecycler.setVisibility(View.INVISIBLE);
            getData();
            swipeRefresh.setRefreshing(false); // Explicitly refreshes only once. If "true" it implicitly refreshes forever
        });
    }

    public void goBluetooth(View caller) {
        Intent goToBluetooth = new Intent(this, BlufiMain.class);
        startActivity(goToBluetooth);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goAccount(View caller) {
        Intent goToAccount = new Intent(this, Account.class);
        goToAccount.putExtra("accountInfo", accountInfo);
        startActivity(goToAccount);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goLibrary(View caller) {
        Intent goToLibrary = new Intent(this, Library.class);
        goToLibrary.putExtra("accountInfo", accountInfo);
        startActivity(goToLibrary);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onCardClick(int position) {
        Intent goToPlantStatistics  = new Intent(this, PlantStatistics.class);
        goToPlantStatistics.putExtra("plantId", homePlants.get(position).getPlantId());
        goToPlantStatistics.putExtra("accountInfo", accountInfo);
        startActivity(goToPlantStatistics);
    }

    private void getData() {
        JSONObject library = new JSONObject();
        try { library.put("type", "UsersInfo");
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/home";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if(Rmessage.equals("HomeLoaded")){
                            JSONArray data, alertPlant, minmax, sensorData;

                            data = response.getJSONArray("data");
                            alertPlant = response.getJSONArray("alertPlant");
                            minmax = response.getJSONArray("minmax");
                            sensorData = response.getJSONArray("sensorData");

                            loadArray(data);
                            // Notifications
                            checkAlerts(alertPlant, minmax, sensorData);
                            loadHomeRecycler();

                        } else {
                            loading.setVisibility(View.GONE);
                            userMessage.setTextColor(ContextCompat.getColor(this, R.color.red));
                            userMessage.setText(R.string.error);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    loading.setVisibility(View.GONE);
                    userMessage.setTextColor(ContextCompat.getColor(this, R.color.red));
                    userMessage.setText(R.string.error);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() { return library.toString().getBytes(StandardCharsets.UTF_8); }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accountInfo.getToken());
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void loadArray(JSONArray data) throws JSONException {
        int plantId, connected;
        String image, species, last_watered, name, status, place;
        JSONObject dataObject;

        // Load Array with plant information
        for(int i = 0; i < data.length(); i++) {
            dataObject = data.getJSONObject(i);
            plantId = dataObject.getInt("plantId");
            image = dataObject.getString("image");
            name = dataObject.getString("name");
            species = dataObject.getString("species");
            last_watered = dataObject.getString("lastWatered");
            place = dataObject.getString("place");
            status = dataObject.getString("status");
            connected = dataObject.getInt("connected");

            homePlants.add(new HomeInfo(plantId, image, name, species, last_watered, place, status, connected));
        }
    }

    private void checkAlerts(JSONArray alertPlant, JSONArray minmax, JSONArray sensorData) throws JSONException {
        JSONObject alertObject, minmaxObject, sensorObject;
        String name, title,  smallText, largeText;
        int plantId;
        NotificationCompat.InboxStyle inboxStyle;

        for(int i = 0; i < alertPlant.length(); i++) {
            alertObject = alertPlant.getJSONObject(i);
            minmaxObject = minmax.getJSONObject(i);
            sensorObject = sensorData.getJSONObject(i);
            plantId = alertObject.getInt("plantId");
            name = alertObject.getString("name");
            inboxStyle = new NotificationCompat.InboxStyle();
            boolean tankAlert = alertObject.getInt("tankAlert") == 1;
            boolean lightAlert = alertObject.getInt("lightAlert") == 1;
            boolean tempAlert = alertObject.getInt("tempAlert") == 1;

            if(tankAlert && sensorObject.getInt("tankLvl") < alertObject.getInt("minWaterLvl")) {
                title = name + ": Water Tank Alert";
                smallText = "Water tank level is too low";
                largeText = "The water tank level is under your given minimum. Please refill the tank.";
                inboxStyle.addLine(title + " " + smallText);
                notification(title, smallText, largeText, plantId, (i*4) + 5);
            }

            if(lightAlert) {
                title = name + ": Light Alert";
                int lightPercent = sensorObject.getInt("lightData");
                if(lightPercent < minmaxObject.getInt("minLight")) {
                    smallText = "Too little light";
                    largeText = "There is less light here than your given minimum. Please move the plant to a place with more light";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, plantId, (i*4) + 6);
                } else if(lightPercent > minmaxObject.getInt(("maxLight"))) {
                    smallText = "Too much light";
                    largeText = "There is more light here than your given maximum. Please move the plant to a place with less light";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, plantId, (i*4) + 6);
                }
            }

            if(tempAlert) {
                title = name + ": Temperature Alert";
                int tempPercent = sensorObject.getInt("tempData");
                if(tempPercent < minmaxObject.getInt("minTemp")) {
                    smallText = "Too cold";
                    largeText = "It is colder here than your given minimum. Please move the plant to a warmer place";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, plantId, (i*4) + 7);
                } else if(tempPercent > minmaxObject.getInt(("maxTemp"))) {
                    smallText = "Too warm";
                    largeText = "It is warmer here than your given maximum. Please move the plant to a cooler place";
                    inboxStyle.addLine(title + " " + smallText);
                    notification(title, smallText, largeText, plantId, (i*4) + 7);
                }
            }

            if(tankAlert || lightAlert || tempAlert) summaryNotification(plantId, name, inboxStyle, (i*4) + 8);
        }
    }

    private void summaryNotification(int plantId, String name, NotificationCompat.InboxStyle inboxStyle, int id) {
        NotificationCompat.Builder summaryNotification = new NotificationCompat.Builder(this, "home")
                .setContentTitle(name + " needs you")
                .setContentText("Give your plant some attention")
                .setSmallIcon(R.drawable.logo_small)
                .setStyle(inboxStyle
                        .setBigContentTitle("New Messages")
                        .setSummaryText(name))
                .setContentIntent(notificationIntent(plantId))
                .setGroup("notificationGroup" + plantId)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroupSummary(true)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(id, summaryNotification.build());
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent notificationIntent(int plantId) {
        Intent intent = new Intent(this, PlantStatistics.class);
        intent.putExtra("accountInfo", accountInfo);
        intent.putExtra("plantId", plantId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void notification(String title, String smallText, String largeText, int plantId, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "home")
                .setContentTitle(title)
                .setContentText(smallText)
                .setSmallIcon(R.drawable.logo_small)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(largeText))
                .setContentIntent(notificationIntent(plantId))
                .setGroup("notificationGroup" + plantId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(notificationId, builder.build());
    }

    private void loadHomeRecycler() {
        RecyclerView.Adapter homeAdapter = new HomeAdapter(homePlants, this);
        homeRecycler.setAdapter(homeAdapter);

        loading.setVisibility(View.GONE);
        if(homeAdapter.getItemCount() != 0) {
            homeRecycler.setVisibility(View.VISIBLE);
        } else {
            homeRecycler.setVisibility(View.INVISIBLE);
            userMessage.setTextColor(ContextCompat.getColor(this, R.color.dark_green));
            userMessage.setText(R.string.noPlants);
        }
        numOfPlants.setText(String.valueOf(homeAdapter.getItemCount()));
        swipeRefresh.setEnabled(true);
    }
}