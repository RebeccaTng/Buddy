package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Home extends AppCompatActivity implements HomeAdapter.HomeListener {

    RecyclerView homeRecycler;
    RecyclerView.Adapter homeAdapter;
    TextView username, numOfPlants, userMessage;
    AccountInfo accountInfo;
    ProgressBar loading;
    ArrayList<HomeInfo> homePlants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_home);

        username = findViewById(R.id.dyn_username_home);
        if(getIntent().hasExtra("accountInfo")) {
            accountInfo = getIntent().getExtras().getParcelable("accountInfo");
            username.setText(accountInfo.getUsername());
        }
        getData();

        loading = findViewById(R.id.loading_home);
        userMessage = findViewById(R.id.userMessage_home);
        numOfPlants = findViewById(R.id.dyn_numOfPlants);
        homeRecycler = findViewById(R.id.home_recycler);

        loading.setVisibility(View.VISIBLE);
        userMessage.setVisibility(View.INVISIBLE);
        numOfPlants.setVisibility(View.INVISIBLE);
        homeRecycler.setVisibility(View.INVISIBLE);

        homeRecycler.setHasFixedSize(true);
        homeRecycler.setLayoutManager(new LinearLayoutManager(this)); // Vertical layout by default
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
        goToPlantStatistics.putExtra("plant", homePlants.get(position));
        goToPlantStatistics.putExtra("accountInfo", accountInfo);
        startActivity(goToPlantStatistics);
    }

    private void getData() {
        // Make the json object for the body of the put request
        JSONObject library = new JSONObject();
        try {
            library.put("type", "UsersInfo");
        } catch (JSONException e) { e.printStackTrace(); }

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/home";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        JSONArray data = response.getJSONArray("data");

                        //check if login is valid
                        if(Rmessage.equals("HomeLoaded")){
                            JSONObject dataObject;
                            Integer plantId;
                            String name, species, last_watered, place, status, image;

                            for(int i = 0; i < data.length(); i++) {
                                dataObject = data.getJSONObject(i);
                                plantId = dataObject.getInt("plantId");
                                image = dataObject.getString("image");
                                name = dataObject.getString("name");
                                species = dataObject.getString("species");
                                last_watered = dataObject.getString("lastWatered");
                                place = dataObject.getString("place");
                                status = dataObject.getString("status");

                                homePlants.add(new HomeInfo(plantId, image, name, species, last_watered, place, status));
                            }
                            loadHomeRecycler();

                        } else{
                            userMessage.setText(R.string.error);
                            userMessage.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    //process an error
                    userMessage.setText(R.string.error);
                    userMessage.setVisibility(View.VISIBLE);
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                // Request body goes here
                return library.toString().getBytes(StandardCharsets.UTF_8);
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

    private void loadHomeRecycler() {
        homeAdapter = new HomeAdapter(homePlants, this);
        homeRecycler.setAdapter(homeAdapter);

        loading.setVisibility(View.GONE);
        if(homeAdapter.getItemCount() != 0) {
            homeRecycler.setVisibility(View.VISIBLE);
            userMessage.setVisibility(View.INVISIBLE);
        } else {
            homeRecycler.setVisibility(View.INVISIBLE);
            userMessage.setText(R.string.noPlants);
            userMessage.setVisibility(View.VISIBLE);
        }
        numOfPlants.setText(String.valueOf(homeAdapter.getItemCount()));
        numOfPlants.setVisibility(View.VISIBLE);
    }
}