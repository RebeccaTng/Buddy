package be.kuleuven.buddy.cards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class LibraryInfo {

    int libId;
    String libSpecies;
    Bitmap libImage;

    public LibraryInfo(int libId, String libImage, String libSpecies) {

        // decode image
        byte[] decodedImage = Base64.decode(libImage, Base64.DEFAULT);
        this.libImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        this.libId = libId;
        this.libSpecies = libSpecies;
    }

    public int getLibId() {
        return libId;
    }

    public Bitmap getLibImage() {
        return libImage;
    }

    public String getLibSpecies() {
        return libSpecies;
    }
}
