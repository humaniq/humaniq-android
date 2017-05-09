package co.humaniq.models;

import org.web3j.abi.datatypes.generated.Uint256;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HistoryItem extends DummyModel {
    private int viewType = ViewType.DATA;

    private String address;
    private Long blockNumber;
    private String blockHash;
    private Long transactionIndex;
    private String transactionHash;
    private Long logIndex;
    private Long timestamp;

    private String from;
    private String to;
    private Long value;

    public HistoryItem(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int getViewType() {
        return viewType;
    }

    public HistoryItem(int viewType, String address, Long blockNumber, String blockHash,
                       Long transactionIndex, String transactionHash, Long logIndex,
                       Long timestamp, String from, String to, Long value)
    {
        this.viewType = viewType;
        this.address = address;
        this.blockNumber = blockNumber;
        this.blockHash = blockHash;
        this.transactionIndex = transactionIndex;
        this.transactionHash = transactionHash;
        this.logIndex = logIndex;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public String getAddress() {
        return address;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public Long getTransactionIndex() {
        return transactionIndex;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public Long getLogIndex() {
        return logIndex;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Long getValue() {
        return value;
    }

    public String getFormattedDate() {
        Timestamp timestamp = new Timestamp(getTimestamp());
        DateFormat simpleDateFormat = SimpleDateFormat.getDateInstance();
        return simpleDateFormat.format(timestamp);
    }
}
