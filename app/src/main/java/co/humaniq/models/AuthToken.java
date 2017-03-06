package co.humaniq.models;


import android.util.Log;

public class AuthToken extends DummyModel {
    public static final int RESULT_GOT_TOKEN = 5000;

    private User user;
    private String token;

    private static AuthToken clientToken = null;

    public AuthToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public AuthToken(String token) {
        this.token = token;
    }

    private AuthToken() {
    }

    static public AuthToken updateInstance(AuthToken token) {
        clientToken = token;
        return clientToken;
    }

    static public AuthToken getInstance() {
        if (clientToken == null) {
            clientToken = new AuthToken();  // Error token
            return clientToken;
        }

        return clientToken;
    }

    public String getAuthorization() {
        return "Token "+token;
    }

    public User getUser() {
        return user;
    }
}
