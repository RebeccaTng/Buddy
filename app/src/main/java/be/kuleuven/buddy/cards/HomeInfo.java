package be.kuleuven.buddy.cards;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeInfo implements Parcelable {

    int plantId, plantImage;
    String plantName, plantSpecies, plantWater, plantPlace, plantStatus;

    public HomeInfo(int plantId, int plantImage, String plantName, String plantSpecies, String plantWater, String plantSpace, String plantStatus) {
        this.plantId = plantId;
        this.plantImage = plantImage;
        this.plantName = plantName;
        this.plantSpecies = plantSpecies;
        this.plantWater = plantWater;
        this.plantPlace = plantSpace;
        this.plantStatus = plantStatus;
    }

    protected HomeInfo(Parcel in) {
        plantImage = in.readInt();
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

    public int getPlantImage() {
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
        dest.writeInt(plantImage);
        dest.writeString(plantName);
        dest.writeString(plantSpecies);
        dest.writeString(plantWater);
        dest.writeString(plantPlace);
        dest.writeString(plantStatus);
    }
}
