package co.humaniq.models;


import android.content.Context;
import co.humaniq.App;
import co.humaniq.Client;
import co.humaniq.Preferences;

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

    static public AuthToken updateInstance(Context context, AuthToken token) {
        return AuthToken.updateInstance(context, token, true);
    }

    static public AuthToken updateInstance(Context context, AuthToken token, boolean inc) {
        clientToken = token;
        clientToken.saveToken(context);

        if (inc) {
            Preferences preferences = App.getPreferences(context);
            preferences.setLoginCount(preferences.getLoginCount() + 1);
        }

        Client.revokeAuthClient();
        return clientToken;
    }

    static public AuthToken getInstance() {
        if (clientToken == null) {
            clientToken = new AuthToken();  // Error token
            return clientToken;
        }

        return clientToken;
    }

    static public void revoke() {
        clientToken = null;
        Client.revokeAuthClient();
    }

    public String getAuthorization() {
        return "Token "+token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void saveToken(Context context) {
    }
}
