package be.kuleuven.buddy.cards;

public class LibraryInfo {

    int libId, libImage;
    String libSpecies;

    public LibraryInfo(int libId, int libImage, String libSpecies) {
        this.libImage = libImage;
        this.libId = libId;
        this.libSpecies = libSpecies;
    }

    public int getLibId() {
        return libId;
    }

    public int getLibImage() {
        return libImage;
    }

    public String getLibSpecies() {
        return libSpecies;
    }
}
