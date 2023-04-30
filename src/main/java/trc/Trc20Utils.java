package trc;

import com.google.protobuf.ByteString;
import org.tron.trident.abi.TypeReference;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Bool;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.Type;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.core.transaction.TransactionBuilder;
import org.tron.trident.proto.Chain;
import org.tron.trident.utils.Base58Check;
import org.tron.trident.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trc20Utils {
    public static Chain.Transaction approveCreate(String cntrAddr,
                                                  String ownerAddr,
                                                  String spender,
                                                  long amount,
                                                  String memo,
                                                  long feeLimit) {
        ApiWrapperWithoutPrivateKey apiWrapperWithoutPrivateKey = ApiWrapperWithoutPrivateKey.ofShasta();

        Function approve = new Function("approve",
                Arrays.asList(new Address(spender),
                        new Uint256(BigInteger.valueOf(amount))),
                Arrays.asList(new TypeReference<Bool>() {
                }));

        TransactionBuilder builder = apiWrapperWithoutPrivateKey.triggerCall(Base58Check.bytesToBase58(ApiWrapper.parseAddress(ownerAddr).toByteArray()),
                Base58Check.bytesToBase58(ApiWrapper.parseAddress(cntrAddr).toByteArray()), approve);
        builder.setFeeLimit(feeLimit);
        builder.setMemo(memo);

        Chain.Transaction transaction = builder.build();
        return transaction;

    }


    public static Chain.Transaction transferCreate(String cntrAddr,
                                                   String fromAddr,
                                                   String destAddr,
                                                   long amount,
                                                   String memo,
                                                   long feeLimit) {
        ApiWrapperWithoutPrivateKey apiWrapperWithoutPrivateKey = ApiWrapperWithoutPrivateKey.ofShasta();

        Function transfer = new Function("transfer",
                Arrays.asList(new Address(destAddr),
                        new Uint256(BigInteger.valueOf(amount))),
                Arrays.asList(new TypeReference<Bool>() {
                }));

        TransactionBuilder builder = apiWrapperWithoutPrivateKey.triggerCall(Base58Check.bytesToBase58(ApiWrapper.parseAddress(fromAddr).toByteArray()),
                Base58Check.bytesToBase58(ApiWrapper.parseAddress(cntrAddr).toByteArray()), transfer);
        builder.setFeeLimit(feeLimit);
        builder.setMemo(memo);

        Chain.Transaction transaction = builder.build();
        return transaction;

    }

    public static Chain.Transaction transferFromCreate(String cntrAddr,
                                                       String spenderAdder,
                                                       String fromAddr,
                                                       String destAddr,
                                                       long amount,
                                                       String memo,
                                                       long feeLimit) {
        ApiWrapperWithoutPrivateKey apiWrapperWithoutPrivateKey = ApiWrapperWithoutPrivateKey.ofShasta();


        Function transferFrom = new Function("transferFrom",
                Arrays.asList(new Address(fromAddr), new Address(destAddr),
                        new Uint256(BigInteger.valueOf(amount))),
                Arrays.asList(new TypeReference<Bool>() {
                }));

        TransactionBuilder builder = apiWrapperWithoutPrivateKey.triggerCall(Base58Check.bytesToBase58(ApiWrapper.parseAddress(spenderAdder).toByteArray()),
                Base58Check.bytesToBase58(ApiWrapper.parseAddress(cntrAddr).toByteArray()), transferFrom);
        builder.setFeeLimit(feeLimit);
        builder.setMemo(memo);

        Chain.Transaction transaction = builder.build();
        return transaction;

    }


    public static String sign(String hexTxid,
                              String privateKey) {

        String hexSignature = signTransaction(hexTxid, privateKey);
        return hexSignature;
    }

    public static String broadcast(Chain.Transaction transaction, String hexSignature) {
        byte[] signature = Numeric.hexStringToByteArray(hexSignature);
        Chain.Transaction signedTxn = transaction.toBuilder().addSignature(ByteString.copyFrom(signature)).build();
        return broadcastTransaction(signedTxn);
    }


    //这一步 实际会在一部离线设备上操作
    private static String signTransaction(String hexTxid, String privateKey) {
        byte[] signature = KeyPair.signTransaction(Numeric.hexStringToByteArray(hexTxid), new KeyPair(privateKey));
        return Numeric.toHexString(signature);
    }


    private static String broadcastTransaction(Chain.Transaction signedTxn) {
        ApiWrapperWithoutPrivateKey apiWrapperWithoutPrivateKey = ApiWrapperWithoutPrivateKey.ofShasta();
        String txid = apiWrapperWithoutPrivateKey.broadcastTransaction(signedTxn);
        apiWrapperWithoutPrivateKey.close();

        return txid;
    }
}
