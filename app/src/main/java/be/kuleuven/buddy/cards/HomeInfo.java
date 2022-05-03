package be.kuleuven.buddy.cards;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class HomeInfo implements Parcelable {

    int plantId, personlized;
    Bitmap plantImage;
    String plantName, plantSpecies, plantWater, plantPlace, plantStatus, plantDate;

    public HomeInfo(int plantId, Bitmap plantImage, String plantName, String plantSpecies, String plantWater, String plantPlace, String plantStatus, String plantDate, int personalized) {

        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String lastWatered, date;
        Calendar calendar1, calendar2;
        calendar1 = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        if(!plantWater.equals("null")) {
            try {
                calendar1.setTime(Objects.requireNonNull(oldDateFormat.parse(plantWater)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            lastWatered = newDateFormat.format(calendar1.getTime());
        } else lastWatered = "-";

        try {
            calendar2.setTime(Objects.requireNonNull(oldDateFormat.parse(plantDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = newDateFormat.format(calendar1.getTime());

        this.plantId = plantId;
        this.plantImage = plantImage;
        this.plantName = plantName;
        this.plantSpecies = plantSpecies;
        this.plantWater = lastWatered;
        this.plantPlace = plantPlace;
        this.plantStatus = plantStatus;
        this.plantDate = date;
        this.personlized = personalized;
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

    public String getPlantDate() { return plantDate; }

    public int getPersonlized() { return personlized; }

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
        dest.writeString(plantDate);
        dest.writeInt(personlized);
    }
}
