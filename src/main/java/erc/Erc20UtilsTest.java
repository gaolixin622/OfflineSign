package erc;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

public class Erc20UtilsTest {
    public static void main(String[] args) throws Exception {
        String WEB3_PROVIDER_URL = "https://mainnet.infura.io/v3/c950feb3e5fc4a06bb162dd9899ef944";
        String cntrAddr = "0xdAC17F958D2ee523a2206206994597C13D831ec7";

        String spenderAddr = "0x1123";

        String ownerAddr = "0x59d071b126bB16Be45a7bf20786D4e9f7E93157c";
        String destAddr = "0x1123";

        System.out.println(Erc20Utils.balance(WEB3_PROVIDER_URL, cntrAddr, ownerAddr));
/*
        long amount = 100;
        long gasLimit = 100;


        RawTransaction transferFromRawTransaction = Erc20Utils.transferFrom(WEB3_PROVIDER_URL,
                cntrAddr,
                spenderAddr,
                ownerAddr,
                destAddr,
                amount,
                gasLimit);


        byte[] encodedTransaction = TransactionEncoder.encode(transferFromRawTransaction);


        //将hexTxid以二维码形式，展示
        //另一部设备，扫描hexTxid，进行签名
        //将签名结果用二维码展示
        String hexTxid = Numeric.toHexString(encodedTransaction);
        //这一步签名，在另一部设备完成
        String hexSignDataJson = Erc20Utils.sign(hexTxid, "");

        String transferFromTransactionHash = Erc20Utils.send(WEB3_PROVIDER_URL, transferFromRawTransaction, hexSignDataJson);

        System.out.println(transferFromTransactionHash);*/

    }



    private void  step1_approve(){

    }
}
