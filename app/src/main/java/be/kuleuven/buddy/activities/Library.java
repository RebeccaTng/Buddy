package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import be.kuleuven.buddy.cards.LibraryAdapter;
import be.kuleuven.buddy.cards.LibraryInfo;
import be.kuleuven.buddy.other.TokenCheck;

public class Library extends AppCompatActivity implements LibraryAdapter.LibraryListener {

    private RecyclerView libraryRecycler;
    private AccountInfo accountInfo;
    private TextView userMessage, libNumber;
    private ProgressBar loading;
    private final ArrayList<LibraryInfo> libPlants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_library);

        if(getIntent().hasExtra("accountInfo")) accountInfo = getIntent().getExtras().getParcelable("accountInfo");
        TokenCheck tokenCheck = new TokenCheck(accountInfo.getEmail(), this);
        tokenCheck.checkExpired();
        getData();

        loading = findViewById(R.id.loading_library);
        userMessage = findViewById(R.id.userMessage_library);
        libNumber = findViewById(R.id.dyn_libNumber);
        libraryRecycler = findViewById(R.id.Library_recycler);

        loading.setVisibility(View.VISIBLE);
        libraryRecycler.setVisibility(View.INVISIBLE);

        libraryRecycler.setHasFixedSize(true);
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Vertical layout by default
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

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goAddManual(View caller) {
        Intent goToAddManual = new Intent(this, AddManual.class);
        goToAddManual.putExtra("accountInfo", accountInfo);
        startActivity(goToAddManual);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onCardClick(int position) {
        Intent goToAddLibrary = new Intent(this, AddLibrary.class);
        goToAddLibrary.putExtra("libId", libPlants.get(position).getLibId());
        goToAddLibrary.putExtra("accountInfo", accountInfo);
        startActivity(goToAddLibrary);
    }

    private void getData() {
        JSONObject library = new JSONObject();
        try { library.put("type", "UsersInfo");
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/library";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    try {
                        String Rmessage = response.getString("message");

                        if(Rmessage.equals("LibraryLoaded")) {
                            JSONArray data = response.getJSONArray("data");
                            loadArray(data);
                            loadLibraryRecycler();

                        } else{
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
        JSONObject dataObject;
        int speciesId;
        String species, image;

        for(int i = 0; i < data.length(); i++) {
            dataObject = data.getJSONObject(i);
            speciesId = dataObject.getInt("speciesId");
            image = dataObject.getString("image");
            species = dataObject.getString("species");

            libPlants.add(new LibraryInfo(speciesId, image, species));
        }
    }

    private void loadLibraryRecycler() {
        RecyclerView.Adapter libraryAdapter = new LibraryAdapter(libPlants, this);
        libraryRecycler.setAdapter(libraryAdapter);

        loading.setVisibility(View.GONE);
        if(libraryAdapter.getItemCount() != 0) {
            libraryRecycler.setVisibility(View.VISIBLE);
        } else {
            libraryRecycler.setVisibility(View.INVISIBLE);
            userMessage.setTextColor(ContextCompat.getColor(this, R.color.dark_green));
            userMessage.setText(R.string.noLibPlants);
        }
        libNumber.setText(String.valueOf(libraryAdapter.getItemCount()));
    }
}