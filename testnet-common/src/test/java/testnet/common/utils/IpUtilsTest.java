package testnet.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class IpUtilsTest {

    @Test
    void cidrToIPList() {
        System.out.println(IpUtils.cidrToIPList("192.168.0.1/8"));
    }

    @Test
    void rangeToIPList() {
    }
}