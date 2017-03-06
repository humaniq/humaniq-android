package co.humaniq.models;


public class User extends DummyModel {
    private String photo;
    private Wallet wallet;

    public Wallet getWallet() {
        return wallet;
    }

    public String getPhoto() {
        return photo;
    }
}
