/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-04-16
 **/
package testnet.common.utils;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    /**
     * 计算给定字符串的 MD5 哈希值，并以十六进制字符串形式返回。
     *
     * @param input 待计算哈希值的字符串
     * @return MD5 哈希值的十六进制字符串表示
     */
    @SneakyThrows
    public static String calculateMD5(String input) {
        return calculateHash(input, "MD5");
    }

    /**
     * 计算给定字符串的 SHA-256 哈希值，并以十六进制字符串形式返回。
     *
     * @param input 待计算哈希值的字符串
     * @return SHA-256 哈希值的十六进制字符串表示
     */
    @SneakyThrows
    public static String calculateSHA256(String input) {
        return calculateHash(input, "SHA-256");
    }

    /**
     * 计算给定字符串使用指定摘要算法的哈希值，并以十六进制字符串形式返回。
     *
     * @param input     待计算哈希值的字符串
     * @param algorithm 摘要算法名称（如 "MD5" 或 "SHA-256"）
     * @return 使用指定算法计算得到的哈希值的十六进制字符串表示
     */
    @SneakyThrows
    private static String calculateHash(String input, String algorithm) {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hashBytes);
    }

    /**
     * 将字节数组转换为十六进制字符串表示。
     *
     * @param bytes 需要转换的字节数组
     * @return 十六进制字符串表示
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
