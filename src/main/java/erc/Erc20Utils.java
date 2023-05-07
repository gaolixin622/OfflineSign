package erc;

import com.alibaba.fastjson.JSONObject;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.transaction.type.TransactionType;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Erc20Utils {

    public static BigInteger estimateGasPrice(Web3j web3j) throws IOException {
        EthGasPrice gas = web3j.ethGasPrice().send();
        return gas.getGasPrice();
    }


    public static BigInteger getGasLimit(Web3j web3j) throws IOException {
        EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
        return block.getGasLimit().divide(new BigInteger(String.valueOf(block.getTransactions().size())));
    }


    public static BigDecimal balance(Web3j web3j, String contractAddress, String address, int power)
            throws Exception {
        Function function = balanceOf(address);
        String responseValue = callSmartContractFunction(web3j, address, function, contractAddress);

        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());

        Uint256 balance = (Uint256) response.get(0);


        return toBigDecimal(balance.getValue(), power);
    }


    public static BigDecimal toBigDecimal(BigInteger bigInteger, int dividePower) {
        BigDecimal b1 = new BigDecimal(bigInteger.toString());
        return b1.divide(BigDecimal.valueOf(Math.pow(10, dividePower)), dividePower, RoundingMode.HALF_UP);
    }


    public static BigInteger toBigInteger(String amount, int multiplyPower) {
        BigDecimal b1 = new BigDecimal(amount);
        BigDecimal b2 = b1.multiply(BigDecimal.valueOf(Math.pow(10, multiplyPower)));
        return b2.toBigInteger();
    }

    public static RawTransaction approve(Web3j web3j,
                                         String cntrAddr,
                                         String ownerAddr,
                                         String spenderAddr,
                                         String amount,
                                         int power,
                                         BigInteger gasPrice,
                                         BigInteger gasLimit) throws Exception {

        BigInteger amountInt =toBigInteger(amount, power);

        Function function = approve(spenderAddr, amountInt);

        BigInteger newNonce = getTransactionNonce(web3j, ownerAddr);

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction =
                RawTransaction.createTransaction(
                        newNonce, gasPrice, gasLimit, cntrAddr, encodedFunction);


        return rawTransaction;

    }

    public static BigDecimal allowance(Web3j web3j,
                                       String cntrAddr,
                                       String owner,
                                       String spender,
                                       int power) throws Exception {
        Function function = allowance(owner, spender);
        String responseValue = callSmartContractFunction(web3j, spender, function, cntrAddr);

        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        Uint256 allowance = (Uint256) response.get(0);

        return toBigDecimal(allowance.getValue(), power);
    }


    public static RawTransaction transfer(Web3j web3j,
                                          String cntrAddr,
                                          String fromAddr,
                                          String destAddr,
                                          String amount,
                                          int power,
                                          BigInteger gasPrice,
                                          BigInteger gasLimit) throws Exception {
        BigInteger amountInt =toBigInteger(amount, power);

        // 构造ERC20代币的transfer函数调用
        Function function = transfer(destAddr, amountInt);

        String encodedFunction = FunctionEncoder.encode(function);

        // 获取新的nonce值
        BigInteger newNonce = getTransactionNonce(web3j, fromAddr);

        // 构造原始交易
        RawTransaction transaction =
                RawTransaction.createTransaction(
                        newNonce, gasPrice, gasLimit, cntrAddr, encodedFunction);

        return transaction;
    }

    public static RawTransaction ethTransfer(Web3j web3j,
                                             String fromAddr,
                                             String destAddr,
                                             String amount,
                                             int power,
                                             BigInteger gasPrice,
                                             BigInteger gasLimit) throws Exception {
        BigInteger amountInt =toBigInteger(amount, power);

        // 获取新的nonce值
        BigInteger newNonce = getTransactionNonce(web3j, fromAddr);
        System.out.println("nonce  " + newNonce);

        // 构造原始交易
        RawTransaction transaction =
                RawTransaction.createEtherTransaction(
                        newNonce, gasPrice, gasLimit, destAddr, amountInt);

        return transaction;
    }

    public static RawTransaction transferFrom(Web3j web3j,
                                              String cntrAddr,
                                              String spenderAddr,
                                              String fromAddr,
                                              String destAddr,
                                              String amount,
                                              int power,
                                              BigInteger gasPrice,
                                              BigInteger gasLimit) throws Exception {
        BigInteger amountInt =toBigInteger(amount, power);

        // 构造ERC20代币的transfer函数调用
        Function function = transferFrom(fromAddr, destAddr, amountInt);

        String encodedFunction = FunctionEncoder.encode(function);

        // 获取新的nonce值
        BigInteger newNonce = getTransactionNonce(web3j, spenderAddr);

        // 构造原始交易
        RawTransaction transaction =
                RawTransaction.createTransaction(
                        newNonce, gasPrice, gasLimit, cntrAddr, encodedFunction);

        return transaction;
    }


    public static String sign(String hexTxid, String WALLET_PRIVATE_KEY) {
        Credentials credentials = Credentials.create(WALLET_PRIVATE_KEY);

        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(hexTxid), credentials.getEcKeyPair());

        HexSignData hexSignData = new HexSignData();
        hexSignData.setV(Numeric.toHexString(signatureData.getV()));
        hexSignData.setR(Numeric.toHexString(signatureData.getR()));
        hexSignData.setS(Numeric.toHexString(signatureData.getS()));

        return JSONObject.toJSONString(hexSignData);

    }


    public static String send(Web3j web3j, RawTransaction transferFromRawTransaction, String hexSignDataJson) throws Exception {
        // 连接以太坊

        HexSignData hexSignData = JSONObject.parseObject(hexSignDataJson, HexSignData.class);
        Sign.SignatureData signatureData = new Sign.SignatureData(Numeric.hexStringToByteArray(hexSignData.getV()),
                Numeric.hexStringToByteArray(hexSignData.getR()),
                Numeric.hexStringToByteArray(hexSignData.getS()));


        byte[] transactionMsg = encode(transferFromRawTransaction, signatureData);

        // 发送交易到以太坊网络
        String transactionHash = web3j.ethSendRawTransaction(Numeric.toHexString(transactionMsg)).send().getTransactionHash();
        return transactionHash;
    }

    private static Function balanceOf(String owner) {
        return new Function(
                "balanceOf",
                Collections.singletonList(new Address(owner)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
    }

    private static String callSmartContractFunction(Web3j web3j, String from, Function function, String contractAddress)
            throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);

        org.web3j.protocol.core.methods.response.EthCall response =
                web3j.ethCall(
                                Transaction.createEthCallTransaction(
                                        from, contractAddress, encodedFunction),
                                DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return response.getValue();
    }

    private static Function approve(String spender, BigInteger value) {
        return new Function(
                "approve",
                Arrays.asList(new Address(spender), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {
                }));
    }


    private static Function allowance(String owner, String spender) {
        return new Function(
                "allowance",
                Arrays.asList(new Address(owner), new Address(spender)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
    }

    private static Function transfer(String to, BigInteger value) {
        return new Function(
                "transfer",
                Arrays.asList(new Address(to), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {
                }));
    }


    private static Function transferFrom(String from, String to, BigInteger value) {
        return new Function(
                "transferFrom",
                Arrays.asList(new Address(from), new Address(to), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {
                }));
    }


    private static BigInteger getTransactionNonce(Web3j web3j, String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
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
