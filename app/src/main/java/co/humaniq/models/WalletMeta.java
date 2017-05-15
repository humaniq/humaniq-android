package co.humaniq.models;

import com.google.gson.annotations.SerializedName;


public class WalletMeta {
    @SerializedName("device_id")
    private String deviceId;

    public WalletMeta(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
