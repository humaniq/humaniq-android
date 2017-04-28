package co.humaniq.models;

import com.google.gson.annotations.SerializedName;


public class WalletInfo {
    @SerializedName("public_address")
    private String publicAddress;

    @SerializedName("photo")
    private String photoUrl;
    private String salt;

    public String getPublicAddress() {
        return publicAddress;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getSalt() {
        return salt;
    }
}
