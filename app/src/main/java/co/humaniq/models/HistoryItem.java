package co.humaniq.models;

import com.google.gson.annotations.SerializedName;


public class HistoryItem extends DummyModel {
    private int viewType = ViewType.DATA;

    private float coins;
    private String currency;
    private boolean bonus;

    @SerializedName("date_time")
    private String dateTime;

    @SerializedName("from_user")
    private int fromUser;

    @SerializedName("to_user")
    private int toUser;
    private int date;

    public HistoryItem(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int getViewType() {
        if (viewType != ViewType.DATA)
            return viewType;

        User user = AuthToken.getInstance().getUser();

        if (isBonus())
            return ViewType.HISTORY_BONUS;
        else if (fromUser != user.getId())
            return ViewType.HISTORY_RECEIVED;
        else
            return ViewType.HISTORY_TRANSFERRED;
    }

    public HistoryItem(int viewType, float coins, String currency, boolean bonus, String dateTime,
                       int fromUser, int toUser, int date)
    {
        this.viewType = viewType;
        this.coins = coins;
        this.currency = currency;
        this.bonus = bonus;
        this.dateTime = dateTime;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.date = date;
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

    public String getDate() {
        final int spaceIndex = dateTime.indexOf(' ');
        return dateTime.substring(0, spaceIndex);
    }

    public boolean isBonus() {
        return bonus;
    }
}
