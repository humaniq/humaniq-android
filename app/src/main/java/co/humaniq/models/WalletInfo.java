package co.humaniq.models;

import com.google.gson.annotations.SerializedName;


public class WalletInfo {
    @SerializedName("public_address")
    String publicAddress;

    @SerializedName("photo")
    String photoUrl;
    String salt;
}
