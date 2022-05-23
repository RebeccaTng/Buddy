package be.kuleuven.buddy.account;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountInfo implements Parcelable {

    private String username, email, token;

    public AccountInfo(String username, String email, String token) {
        this.username = username;
        this.email = email;
        this.token = token;
    }

    protected AccountInfo(Parcel in) {
        username = in.readString();
        email = in.readString();
        token = in.readString();
    }

    public static final Creator<AccountInfo> CREATOR = new Creator<AccountInfo>() {
        @Override
        public AccountInfo createFromParcel(Parcel in) {
            return new AccountInfo(in);
        }

        @Override
        public AccountInfo[] newArray(int size) {
            return new AccountInfo[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(token);
    }
}
