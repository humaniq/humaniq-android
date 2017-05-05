package co.humaniq.models;

import org.web3j.abi.datatypes.generated.Uint256;


public class HistoryItem extends DummyModel {
    private int viewType = ViewType.DATA;

    private Uint256 coins;
    private String currency;
    private boolean bonus;

    private String dateTime;
    private String from;
    private String to;

    public HistoryItem(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int getViewType() {
//        if (viewType != ViewType.DATA)
        return viewType;
//
//        User user = AuthToken.getInstance().getUser();
//
//        if (isBonus())
//            return ViewType.HISTORY_BONUS;
//        else if (fromUser != user.getId())
//            return ViewType.HISTORY_RECEIVED;
//        else
//            return ViewType.HISTORY_TRANSFERRED;
    }

    public HistoryItem(int viewType, Uint256 coins, String currency, boolean bonus,
                       String dateTime, String from, String to)
    {
        this.viewType = viewType;
        this.coins = coins;
        this.currency = currency;
        this.bonus = bonus;
        this.dateTime = dateTime;
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Uint256 getCoins() {
        return coins;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDate() {
//        final int spaceIndex = dateTime.indexOf(' ');
//        return dateTime.substring(0, spaceIndex);
        return "";
    }

    public boolean isBonus() {
        return bonus;
    }
}
