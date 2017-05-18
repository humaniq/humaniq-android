package co.humaniq.models;

import android.content.Context;
import co.humaniq.App;
import co.humaniq.Preferences;
import com.google.gson.annotations.SerializedName;


public class WalletMeta {
    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("public_address")
    private String publicAddress;

    @SerializedName("key_file_path")
    private String keyFilePath;

    public WalletMeta(String deviceId, String publicAddress, String keyFilePath) {
        this.deviceId = deviceId;
        this.publicAddress = publicAddress;
        this.keyFilePath = keyFilePath;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public String getKeyFilePath() {
        return keyFilePath;
    }

    public void save(Context context) {
        Preferences preferences = App.getPreferences(context);
        preferences.setAccountKeyFile(getKeyFilePath());
        preferences.setAccountAddress(getPublicAddress());
    }
}
