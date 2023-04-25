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
    public static Chain.Transaction transferFrom_step1_create(String cntrAddr,
                                                              String fromAddr,
                                                              String destAddr,
                                                              long amount,
                                                              String memo,
                                                              long feeLimit) {
        ApiWrapperWithoutPrivateKey apiWrapperWithoutPrivateKey = ApiWrapperWithoutPrivateKey.ofShasta();

        java.util.List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        inputParameters.add(new Address(fromAddr));
        inputParameters.add(new Address(destAddr));
        inputParameters.add(new Uint256(BigInteger.valueOf(amount)));

        outputParameters.add(new TypeReference<Bool>() {
        });

        Function transferFrom = new Function("transferFrom",
                inputParameters,
                outputParameters);
        TransactionBuilder builder = apiWrapperWithoutPrivateKey.triggerCall(Base58Check.bytesToBase58(ApiWrapper.parseAddress(fromAddr).toByteArray()), Base58Check.bytesToBase58(ApiWrapper.parseAddress(cntrAddr).toByteArray()), transferFrom);
        builder.setFeeLimit(feeLimit);
        builder.setMemo(memo);
        Chain.Transaction transaction = builder.build();
        return transaction;

    }

    public static String transferFrom_step2_sign(String hexTxid,
                                                 String privateKey) {

        String hexSignature = signTransaction(hexTxid, privateKey);
        return hexSignature;
    }

    public static String transferFrom_step3_broadcast(Chain.Transaction transaction, String hexSignature) {
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
