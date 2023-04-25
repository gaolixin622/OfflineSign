package trc;

import org.tron.trident.proto.Chain;
import org.tron.trident.utils.Numeric;

public class Trc20UtilsTest {
    public static void main(String[] args) {
        String cntrAddr = "0x123";
        String fromAddr = "0x123";
        String destAddr = "0x123";
        long amount = 100;
        String memo = "";
        long feeLimit = 100;

        Chain.Transaction transaction = Trc20Utils.transferFrom_step1_create(cntrAddr,
                fromAddr,
                destAddr,
                amount,
                memo,
                feeLimit);


        String hexTxid = Numeric.toHexString(ApiWrapperWithoutPrivateKey.calculateTransactionHash(transaction));

        //将hexTxid以二维码形式，展示
        //另一部设备，扫描hexTxid，进行签名
        //将签名结果用二维码展示
        String hexSignature = Trc20Utils.transferFrom_step2_sign(hexTxid, "");

        String txid = Trc20Utils.transferFrom_step3_broadcast(transaction, hexSignature);
        System.out.println(txid);

    }
}
