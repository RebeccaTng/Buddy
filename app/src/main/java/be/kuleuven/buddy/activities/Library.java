package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

public class Library extends AppCompatActivity implements LibraryAdapter.LibraryListener {

    RecyclerView libraryRecycler;
    RecyclerView.Adapter libraryAdapter;
    AccountInfo accountInfo;
    TextView userMessage, libNumber;
    ProgressBar loading;
    ArrayList<LibraryInfo> libPlants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_library);

        if(getIntent().hasExtra("accountInfo")) accountInfo = getIntent().getExtras().getParcelable("accountInfo");
        getData();

        userMessage = findViewById(R.id.userMessage_library);
        loading = findViewById(R.id.loading_library);
        libNumber = findViewById(R.id.dyn_libNumber);
        libraryRecycler = findViewById(R.id.Library_recycler);

        loading.setVisibility(View.VISIBLE);
        userMessage.setVisibility(View.INVISIBLE);
        libNumber.setVisibility(View.INVISIBLE);
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
        goToAddLibrary.putExtra("libPlant", libPlants.get(position));
        goToAddLibrary.putExtra("accountInfo", accountInfo);
        startActivity(goToAddLibrary);
    }

    private void getData() {
        // Make the json object for the body of the put request
        JSONObject library = new JSONObject();
        try {
            library.put("type", "UsersInfo");
        } catch (JSONException e) { e.printStackTrace(); }

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/library";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        JSONArray data = response.getJSONArray("data");

                        //check if login is valid
                        if(Rmessage.equals("Libraryloaded")){
                            JSONObject dataObject;
                            Integer speciesId;
                            String species;
                            byte[] decodedImage;
                            Bitmap image;

                            for(int i = 0; i < data.length(); i++) {
                                dataObject = data.getJSONObject(i);
                                speciesId = dataObject.getInt("speciesId");
                                species = dataObject.getString("species");

                                // decode image
                                decodedImage = Base64.decode(dataObject.getString("image"), Base64.DEFAULT);
                                image = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);

                                libPlants.add(new LibraryInfo(speciesId, image, species));
                            }
                            loadLibraryRecycler();

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

    private void loadLibraryRecycler() {
        libraryAdapter = new LibraryAdapter(libPlants, this);
        libraryRecycler.setAdapter(libraryAdapter);

        loading.setVisibility(View.GONE);
        if(libraryAdapter.getItemCount() != 0) {
            libraryRecycler.setVisibility(View.VISIBLE);
            userMessage.setVisibility(View.INVISIBLE);
        } else {
            libraryRecycler.setVisibility(View.INVISIBLE);
            userMessage.setText(R.string.noLibPlants);
            userMessage.setVisibility(View.VISIBLE);
        }
        libNumber.setText(String.valueOf(libraryAdapter.getItemCount()));
        libNumber.setVisibility(View.VISIBLE);
    }
}