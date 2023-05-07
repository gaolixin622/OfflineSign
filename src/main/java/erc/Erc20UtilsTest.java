package erc;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Erc20UtilsTest {
    static String WEB3_PROVIDER_URL = "https://mainnet.infura.io/v3/c950feb3e5fc4a06bb162dd9899ef944";

    static String cntrAddr = "0xdAC17F958D2ee523a2206206994597C13D831ec7";

    static String spenderAddr = "0xe948bed25b6b6a3cfb8068a8409f0b8dffbdb0e8";
    static String spenderKey = "";

    static String ownerAddr = "0x1e15b0f14bb3e917ba1a24d68cc9c2ec8cf2f7bb";
    static String ownerKey = "";

    static String destAddr = "0xbd75aa5d4dfcd5e29961e189cc8ed9650f11a8fd";

    public static void main(String[] args) throws Exception {
        testBalance();
        allowanceTest();

    }


    private static void transferFromTest() throws Exception {
        Web3j web3j = Web3j.build(new HttpService(WEB3_PROVIDER_URL));

        String amount ="1";

        BigInteger gasPriceWei = Erc20Utils.estimateGasPrice(web3j);
        double gasPriceDoubleWei = gasPriceWei.doubleValue();
        System.out.println(gasPriceDoubleWei + "wei");

        double gasPriceDoubleEthGwei = gasPriceDoubleWei / 1_000_000_000d;
        System.out.println(gasPriceDoubleEthGwei + "Gwei");

        BigInteger gasLimit = Erc20Utils.getGasLimit(web3j);
        double gasLimitDouble = gasLimit.doubleValue();
        System.out.println(gasLimitDouble);

        gasLimit = BigInteger.valueOf(70000);

        RawTransaction transferFromRawTransaction = Erc20Utils.transferFrom(web3j,
                cntrAddr,
                spenderAddr,
                ownerAddr,
                destAddr,
                amount,
                6,
                gasPriceWei,
                gasLimit);


        byte[] encodedTransaction = TransactionEncoder.encode(transferFromRawTransaction);


        //将hexTxid以二维码形式，展示
        //另一部设备，扫描hexTxid，进行签名
        //将签名结果用二维码展示
        String hexTxid = Numeric.toHexString(encodedTransaction);
        //这一步签名，在另一部设备完成
        String hexSignDataJson = Erc20Utils.sign(hexTxid, spenderKey);

        String transferFromTransactionHash = Erc20Utils.send(web3j, transferFromRawTransaction, hexSignDataJson);

        System.out.println(transferFromTransactionHash);

        web3j.shutdown();
    }

    private static void allowanceTest() throws Exception {
        Web3j web3j = Web3j.build(new HttpService(WEB3_PROVIDER_URL));
        BigDecimal allowance = Erc20Utils.allowance(web3j, cntrAddr, ownerAddr, spenderAddr,6);
        System.out.println(allowance);
        web3j.shutdown();
    }

    private static void testApprove() throws Exception {
        Web3j web3j = Web3j.build(new HttpService(WEB3_PROVIDER_URL));

        String amount ="1";

        BigInteger gasPriceWei = Erc20Utils.estimateGasPrice(web3j);
        double gasPriceDoubleWei = gasPriceWei.doubleValue();
        System.out.println(gasPriceDoubleWei + "wei");

        double gasPriceDoubleEthGwei = gasPriceDoubleWei / 1_000_000_000d;
        System.out.println(gasPriceDoubleEthGwei + "Gwei");

        BigInteger gasLimit = Erc20Utils.getGasLimit(web3j);
        double gasLimitDouble = gasLimit.doubleValue();
        System.out.println(gasLimitDouble);

        gasLimit = BigInteger.valueOf(100);

        RawTransaction transferFromRawTransaction = Erc20Utils.approve(web3j,
                cntrAddr,
                ownerAddr,
                spenderAddr,
                amount,
                6,
                gasPriceWei,
                gasLimit);


        byte[] encodedTransaction = TransactionEncoder.encode(transferFromRawTransaction);


        //将hexTxid以二维码形式，展示
        //另一部设备，扫描hexTxid，进行签名
        //将签名结果用二维码展示
        String hexTxid = Numeric.toHexString(encodedTransaction);
        //这一步签名，在另一部设备完成
        String hexSignDataJson = Erc20Utils.sign(hexTxid, ownerKey);

        String transferFromTransactionHash = Erc20Utils.send(web3j, transferFromRawTransaction, hexSignDataJson);

        System.out.println(transferFromTransactionHash);

        web3j.shutdown();
    }

    private static void testBalance() {
        Web3j web3j = Web3j.build(new HttpService(WEB3_PROVIDER_URL));

        try {
            BigInteger eth = web3j.ethGetBalance(ownerAddr, DefaultBlockParameterName.LATEST).send().getBalance();

            System.out.println(Erc20Utils.toBigDecimal(eth, 18));

            BigDecimal usdt = Erc20Utils.balance(web3j, cntrAddr, ownerAddr, 6);
            System.out.println(usdt);

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            web3j.shutdown();
            System.out.println("shutdown");

        }
    }

    private static void testEthTransfer() throws Exception {
        Web3j web3j = Web3j.build(new HttpService(WEB3_PROVIDER_URL));


        String amount = "0.007" ;

        BigInteger gasPriceWei = Erc20Utils.estimateGasPrice(web3j);
        double gasPriceDoubleWei = gasPriceWei.doubleValue();
        System.out.println(gasPriceDoubleWei + "wei");

        double gasPriceDoubleEthGwei = gasPriceDoubleWei / 1_000_000_000d;
        System.out.println(gasPriceDoubleEthGwei + "Gwei");

        BigInteger gasLimit = Erc20Utils.getGasLimit(web3j);
        double gasLimitDouble = gasLimit.doubleValue();
        System.out.println(gasLimitDouble);

        gasLimit = BigInteger.valueOf(30000);

        RawTransaction transferFromRawTransaction = Erc20Utils.ethTransfer(web3j,
                ownerAddr,
                spenderAddr,
                amount,
                18,
                gasPriceWei,
                gasLimit);


        byte[] encodedTransaction = TransactionEncoder.encode(transferFromRawTransaction);


        //将hexTxid以二维码形式，展示
        //另一部设备，扫描hexTxid，进行签名
        //将签名结果用二维码展示
        String hexTxid = Numeric.toHexString(encodedTransaction);
        //这一步签名，在另一部设备完成
        String hexSignDataJson = Erc20Utils.sign(hexTxid, ownerKey);

        String transferFromTransactionHash = Erc20Utils.send(web3j, transferFromRawTransaction, hexSignDataJson);

        System.out.println(transferFromTransactionHash);

        web3j.shutdown();
    }

}
