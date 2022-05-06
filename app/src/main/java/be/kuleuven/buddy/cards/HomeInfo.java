package be.kuleuven.buddy.cards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class HomeInfo implements Parcelable {

    int plantId;
    Bitmap plantImage;
    String plantName, plantSpecies, plantWater, plantPlace, plantStatus;

    public HomeInfo(int plantId, String plantImage, String plantName, String plantSpecies, String plantWater, String plantPlace, String plantStatus) {

        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String lastWatered;
        Calendar calendar = Calendar.getInstance();

        if(!plantWater.equals("null")) {
            try {
                calendar.setTime(Objects.requireNonNull(oldDateFormat.parse(plantWater)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            lastWatered = newDateFormat.format(calendar.getTime());
        } else lastWatered = "-";

        // decode image
        byte[] decodedImage = Base64.decode(plantImage, Base64.DEFAULT);
        this.plantImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        this.plantId = plantId;
        this.plantName = plantName;
        this.plantSpecies = plantSpecies;
        this.plantWater = lastWatered;
        this.plantPlace = plantPlace;
        this.plantStatus = plantStatus;
    }

    protected HomeInfo(Parcel in) {
        plantImage = in.readParcelable(Bitmap.class.getClassLoader());
        plantName = in.readString();
        plantSpecies = in.readString();
        plantWater = in.readString();
        plantPlace = in.readString();
        plantStatus = in.readString();
    }

    public static final Creator<HomeInfo> CREATOR = new Creator<HomeInfo>() {
        @Override
        public HomeInfo createFromParcel(Parcel in) {
            return new HomeInfo(in);
        }

        @Override
        public HomeInfo[] newArray(int size) {
            return new HomeInfo[size];
        }
    };

    public int getPlantId() { return plantId; }

    public Bitmap getPlantImage() {
        return plantImage;
    }

    public String getPlantName() {
        return plantName;
    }

    public String getPlantSpecies() {
        return plantSpecies;
    }

    public String getPlantWater() {
        return plantWater;
    }

    public String getPlantPlace() {
        return plantPlace;
    }

    public String getPlantStatus() {
        return plantStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(plantImage);
        dest.writeString(plantName);
        dest.writeString(plantSpecies);
        dest.writeString(plantWater);
        dest.writeString(plantPlace);
        dest.writeString(plantStatus);
    }
}
