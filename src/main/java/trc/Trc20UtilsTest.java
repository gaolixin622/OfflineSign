package trc;

import org.tron.trident.proto.Chain;
import org.tron.trident.utils.Numeric;

import java.math.BigInteger;

public class Trc20UtilsTest {
    public static void main(String[] args) {
        String cntrAddr = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
        String fromAddr = "410249bf665ff59d59a95b60e66a8bee90191b1100";
        String destAddr = "0x410249bf665ff59d59a95b60e66a8bee90191b1100";
        long amount = 100;
        String memo = "";
        long feeLimit = 100;

        BigInteger balance = Trc20Utils.balanceOf(cntrAddr,
                fromAddr);

        System.out.println(balance);


       /* String hexTxid = Numeric.toHexString(ApiWrapperWithoutPrivateKey.calculateTransactionHash(transaction));

        //将hexTxid以二维码形式，展示
        //另一部设备，扫描hexTxid，进行签名
        //将签名结果用二维码展示
        String hexSignature = Trc20Utils.sign(hexTxid, "");

        String txid = Trc20Utils.broadcast(transaction, hexSignature);
        System.out.println(txid);*/

    }
}
