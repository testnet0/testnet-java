/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-11
 **/
package org.jeecg;

import java.net.MalformedURLException;
import java.net.URL;

public class Test {
    public static void main(String[] args) {
        URL url = null;
        try {
            url = new URL("http://10.58.148.30:9999/123/123");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String absolutePath = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
        String[] relativePath = url.getPath().split("/");
        System.out.println(absolutePath);
        System.out.println(relativePath);
    }
}
