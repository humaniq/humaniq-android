package co.humaniq.models;

import com.google.gson.annotations.SerializedName;


public class Wallet extends DummyModel {
    @SerializedName("user")
    private int userId;

    private String hash;
    private float balance;
    private boolean blocked;
    private String currency;
}
