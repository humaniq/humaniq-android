package co.humaniq.models;


public class AuthToken extends DummyModel {
    public static final int RESULT_GOT_TOKEN = 5000;

    private User user;
    private String token;

    private static AuthToken clientToken = null;

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
