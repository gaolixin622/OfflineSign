package erc;

import com.alibaba.fastjson.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.transaction.type.TransactionType;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Erc20Utils {
    public static RawTransaction transferFrom_step1_create(String WEB3_PROVIDER_URL,
                                                           String cntrAddr,
                                                           String fromAddr,
                                                           String destAddr,
                                                           long amount,
                                                           long gasPrice,
                                                           long gasLimit) throws Exception {
        // 连接以太坊
        Web3j web3j = Web3j.build(new HttpService(WEB3_PROVIDER_URL));

        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        inputParameters.add(new Uint256(new BigInteger(fromAddr)));
        inputParameters.add(new Uint256(new BigInteger(destAddr)));
        inputParameters.add(new Uint256(BigInteger.valueOf(amount)));

        outputParameters.add(new TypeReference<Address>() {
        });
        outputParameters.add(new TypeReference<Address>() {
        });
        outputParameters.add(new TypeReference<Uint256>() {
        });

        // 构造ERC20代币的transferFrom函数调用
        Function transferFromFunction = new Function(
                "transferFrom",
                inputParameters,
                outputParameters
        );
        String encodedTransferFromFunction = FunctionEncoder.encode(transferFromFunction);

        // 获取新的nonce值
        BigInteger newNonce = getTransactionNonce(web3j, fromAddr);

        // 构造原始交易
        RawTransaction transferFromRawTransaction = RawTransaction.createTransaction(
                newNonce,
                BigInteger.valueOf(gasPrice),
                BigInteger.valueOf(gasLimit),
                cntrAddr,
                encodedTransferFromFunction
        );


        return transferFromRawTransaction;


    }


    public static String transferFrom_step3_send(String WEB3_PROVIDER_URL, RawTransaction transferFromRawTransaction, String hexSignDataJson) throws Exception {
        // 连接以太坊
        Web3j web3j = Web3j.build(new HttpService(WEB3_PROVIDER_URL));

        HexSignData hexSignData = JSONObject.parseObject(hexSignDataJson, HexSignData.class);
        Sign.SignatureData signatureData = new Sign.SignatureData(Numeric.hexStringToByteArray(hexSignData.getV()),
                Numeric.hexStringToByteArray(hexSignData.getR()),
                Numeric.hexStringToByteArray(hexSignData.getS()));


        byte[] signedTransferFromMessage = encode(transferFromRawTransaction, signatureData);

        // 发送交易到以太坊网络
        String transferFromTransactionHash = web3j.ethSendRawTransaction(Numeric.toHexString(signedTransferFromMessage)).send().getTransactionHash();
        System.out.println("Transfer from transaction hash: " + transferFromTransactionHash);

        return transferFromTransactionHash;
    }


    public static String transferFrom_step2_sign(String hexTxid, String WALLET_PRIVATE_KEY) {
        Credentials credentials = Credentials.create(WALLET_PRIVATE_KEY);

        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(hexTxid), credentials.getEcKeyPair());

        HexSignData hexSignData = new HexSignData();
        hexSignData.setV(Numeric.toHexString(signatureData.getV()));
        hexSignData.setR(Numeric.toHexString(signatureData.getR()));
        hexSignData.setS(Numeric.toHexString(signatureData.getS()));

        return JSONObject.toJSONString(hexSignData);

    }

    private static BigInteger getTransactionNonce(Web3j web3j, String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, null).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }


    private static byte[] encode(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> values = TransactionEncoder.asRlpValues(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        byte[] encoded = RlpEncoder.encode(rlpList);
        if (!rawTransaction.getType().equals(TransactionType.LEGACY)) {
            return ByteBuffer.allocate(encoded.length + 1)
                    .put(rawTransaction.getType().getRlpType())
                    .put(encoded)
                    .array();
        }
        return encoded;
    }


    static class HexSignData {
        private String v;
        private String r;
        private String s;

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }
}
