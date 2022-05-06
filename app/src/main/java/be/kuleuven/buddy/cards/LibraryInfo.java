package be.kuleuven.buddy.cards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class LibraryInfo implements Parcelable {

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

    protected LibraryInfo(Parcel in) {
        libId = in.readInt();
        libImage = in.readParcelable(Bitmap.class.getClassLoader());
        libSpecies = in.readString();
    }

    public static final Creator<LibraryInfo> CREATOR = new Creator<LibraryInfo>() {
        @Override
        public LibraryInfo createFromParcel(Parcel in) {
            return new LibraryInfo(in);
        }

        @Override
        public LibraryInfo[] newArray(int size) {
            return new LibraryInfo[size];
        }
    };

    public int getLibId() {
        return libId;
    }

    public Bitmap getLibImage() {
        return libImage;
    }

    public String getLibSpecies() {
        return libSpecies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(libId);
        parcel.writeValue(libImage);
        parcel.writeString(libSpecies);
    }
}
