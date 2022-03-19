package be.kuleuven.buddy.cards;

public class HomeInfo {

    int plantImage;
    String plantName, plantSpecies, plantWater, plantPlace, plantStatus;

    public HomeInfo(int plantImage, String plantName, String plantSpecies, String plantWater, String plantSpace, String plantStatus) {
        this.plantImage = plantImage;
        this.plantName = plantName;
        this.plantSpecies = plantSpecies;
        this.plantWater = plantWater;
        this.plantPlace = plantSpace;
        this.plantStatus = plantStatus;
    }

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
}
