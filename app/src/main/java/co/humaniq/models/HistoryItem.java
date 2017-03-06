package co.humaniq.models;

import com.google.gson.annotations.SerializedName;


public class HistoryItem extends DummyModel {
    private int viewType = ViewType.DATA;

    private float coins;
    private String currency;

    @SerializedName("date_time")
    private String dateTime;

    @SerializedName("from_user")
    private int fromUser;

    @SerializedName("to_user")
    private int toUser;

    public HistoryItem(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int getViewType() {
        if (viewType == ViewType.DATA) {
            if (fromUser != AuthToken.getInstance().getUser().getId())
                return ViewType.HISTORY_RECEIVED;
            else
                return ViewType.HISTORY_TRANSFERRED;
        }
        return viewType;
    }

    public HistoryItem(float coins, String currency, String dateTime, int fromUser, int toUser) {
        this.coins = coins;
        this.currency = currency;
        this.dateTime = dateTime;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.viewType = BaseModel.ViewType.DATA;
    }

    public float getCoins() {
        return coins;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getFromUser() {
        return fromUser;
    }

    public int getToUser() {
        return toUser;
    }
}
