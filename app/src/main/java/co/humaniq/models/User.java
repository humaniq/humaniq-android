package co.humaniq.models;


public class User extends DummyModel {
    private int id;
    private String photo;
    private Wallet wallet;

    public Wallet getWallet() {
        return wallet;
    }

    public String getPhoto() {
        return photo;
    }

    public int getId() {
        return id;
    }

    public User(int id, String photo, Wallet wallet) {
        this.id = id;
        this.photo = photo;
        this.wallet = wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
