/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-04-29
 **/
package testnet.common.utils;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class IpUtils {
    public static boolean isIpv6(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    // 判断 IP 是否为内网 IP
    public static boolean isPrivateIP(String ip) {
        Pattern reg = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
        return reg.matcher(ip).find();
    }

    /**
     * 将CIDR格式的IP段转换为IP列表
     *
     * @param cidr CIDR格式的IP段，例如 "192.168.1.0/24"
     * @return IP地址列表
     */
    public static List<String> cidrToIPList(String cidr) {
        List<String> ipList = new ArrayList<>();
        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid CIDR format: " + cidr);
        }

        String ip = parts[0];
        int prefix;
        try {
            prefix = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid prefix in CIDR: " + cidr);
        }

        if (prefix < 0 || prefix > 32) {
            throw new IllegalArgumentException("Invalid prefix length: " + prefix);
        }

        long ipLong = ipToLong(ip);
        long mask = (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL; // 确保掩码仅作用于低32位
        long network = ipLong & mask;
        // 计算广播地址的正确方式
        long broadcast = network + (1L << (32 - prefix)) - 1;

        for (long i = network; i <= broadcast; i++) {
            ipList.add(longToIP(i));
        }

        return ipList;
    }

    /**
     * 将起始IP到结束IP的段转换为IP列表
     *
     * @param startIP 起始IP，例如 "192.168.1.1"
     * @param endIP   结束IP，例如 "192.168.1.10"
     * @return IP地址列表
     */
    public static List<String> rangeToIPList(String startIP, String endIP) {
        List<String> ipList = new ArrayList<>();
        long start = ipToLong(startIP);
        long end = ipToLong(endIP);

        if (start > end) {
            throw new IllegalArgumentException("Start IP must be less than or equal to End IP");
        }

        for (long i = start; i <= end; i++) {
            ipList.add(longToIP(i));
        }

        return ipList;
    }

    // 辅助函数：将IPv4地址转换为长整型
    private static long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("Invalid IP address: " + ip);
        }
        long result = 0;
        for (int i = 0; i < 4; i++) {
            int octet = Integer.parseInt(octets[i]);
            result = (result << 8) | octet; // 左移8位后按位或
        }
        return result & 0xFFFFFFFFL; // 确保结果为无符号32位整数
    }

    // 辅助函数：将长整型转换为IPv4地址
    private static String longToIP(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    /**
     * 判断字符串是否是合法的IP地址（IPv4或IPv6）
     */
    public static boolean isValidIPAddress(String host) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}::([0-9a-fA-F]{1,4}:){0,5}[0-9a-fA-F]{1,4}$";
        return Pattern.matches(ipv4Pattern, host) || Pattern.matches(ipv6Pattern, host);
    }
}