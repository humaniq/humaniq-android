package co.humaniq;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;
import org.web3j.protocol.parity.ParityFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;
import static org.web3j.tx.Transfer.GAS_LIMIT;


public class Web3 {
    private Web3j web3;
    private Parity parity;
    private Web3jService service;
    private static Web3 instance;

    public static Web3 getInstance() {
        if (instance != null)
            return instance;

        instance = new Web3();
        return instance;
    }

    private Web3() {
        service = new HttpService(Config.SERVER_URL + "/eth_rpc/");
        web3 = Web3jFactory.build(service);
        parity = ParityFactory.build(service);
    }

    public Web3j getWeb3() {
        return web3;
    }

    public Parity getParity() {
        return parity;
    }

    public Web3jService getService() {
        return service;
    }
}
