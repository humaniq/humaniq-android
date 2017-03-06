package co.humaniq.models;

import com.google.gson.annotations.SerializedName;


public class Wallet extends DummyModel {
    @SerializedName("user")
    private int userId;

    private String hash;
    private float balance;
    private boolean blocked;
    private String currency;

    public Wallet(int userId, String hash, float balance, boolean blocked, String currency) {
        this.userId = userId;
        this.hash = hash;
        this.balance = balance;
        this.blocked = blocked;
        this.currency = currency;
    }

    public int getUserId() {
        return userId;
    }

    public String getHash() {
        return hash;
    }

    public float getBalance() {
        return balance;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public String getCurrency() {
        return currency;
    }
}
