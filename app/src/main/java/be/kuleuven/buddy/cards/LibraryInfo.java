package be.kuleuven.buddy.cards;

import android.os.Parcel;
import android.os.Parcelable;

public class LibraryInfo implements Parcelable {

    int libId, libImage;
    String libSpecies;

    public LibraryInfo(int libId, int libImage, String libSpecies) {
        this.libImage = libImage;
        this.libId = libId;
        this.libSpecies = libSpecies;
    }

    protected LibraryInfo(Parcel in) {
        libId = in.readInt();
        libImage = in.readInt();
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

    public int getLibImage() {
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
        parcel.writeInt(libImage);
        parcel.writeString(libSpecies);
    }
}
