/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-04-29
 **/
package testnet.common.utils;


import java.util.regex.Pattern;

public class IpUtils {

    protected static Pattern IPV4 = Pattern.compile("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");
    protected static Pattern IPV6 = Pattern.compile("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    public static boolean isIpv4(String ip4) {
        return IPV4.matcher(ip4).matches();
    }

    public static boolean isIpv6(String ip6) {
        return IPV6.matcher(ip6).matches();
    }

    // 判断 IP 是否为内网 IP
    public static boolean isPrivateIP(String ip) {
        Pattern reg = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
        return reg.matcher(ip).find();
    }
}