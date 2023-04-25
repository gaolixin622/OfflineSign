package erc;

import com.alibaba.fastjson.JSONObject;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

public class Erc20UtilsTest {
    public static void main(String[] args) throws Exception {
        String WEB3_PROVIDER_URL = "";
        String cntrAddr = "0x1123";
        String fromAddr = "0x1123";
        String destAddr = "0x1123";
        long amount = 100;
        long gasPrice = 100;
        long gasLimit = 100;


        RawTransaction transferFromRawTransaction = Erc20Utils.transferFrom_step1_create(WEB3_PROVIDER_URL,
                cntrAddr,
                fromAddr,
                destAddr,
                amount,
                gasPrice,
                gasLimit);


        byte[] encodedTransaction = TransactionEncoder.encode(transferFromRawTransaction);


        //将hexTxid以二维码形式，展示
        //另一部设备，扫描hexTxid，进行签名
        //将签名结果用二维码展示
        String hexTxid = Numeric.toHexString(encodedTransaction);
        //这一步签名，在另一部设备完成
        String hexSignDataJson = Erc20Utils.transferFrom_step2_sign(hexTxid, "");

        String transferFromTransactionHash = Erc20Utils.transferFrom_step3_send(WEB3_PROVIDER_URL, transferFromRawTransaction, hexSignDataJson);

        System.out.println(transferFromTransactionHash);

    }
}