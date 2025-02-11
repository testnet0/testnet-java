package testnet.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainUtils {


    public static String getTopDomain(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String domain = url.replaceFirst("^(http://|https://|ftp://|www\\.)", "");
        String tlds = "(com|net|org|cc|me|tel|mobi|asia|biz|info|name|tv|hk|公司|中国|网络|co\\.uk|us|uk|ca|de|fr|au|jp|in|br|xyz|top|site|online|club|shop|vip|tech|store|blog|com\\.cn|net\\.cn|org\\.cn|gov\\.cn|edu\\.cn|co\\.cn|com\\.co)";
        Pattern pattern = Pattern.compile("([^.]+\\.(?:" + tlds + "))$");
        Matcher matcher = pattern.matcher(domain);
        if (matcher.find()) {
            return matcher.group();
        }
        pattern = Pattern.compile("([^.]+\\.[^.]+\\.(uk|cn|co\\.cn|com\\.cn|net\\.cn|org\\.cn|gov\\.cn|edu\\.cn|\\.tw|\\.al|\\.es|\\.ar))$");
        matcher = pattern.matcher(domain);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }


    public static void main(String[] args) {
        String[] testUrls = {
                "www.baidu.com",
                "http://www.baidu.com",
                "https://www.baidu.com",
                "www.google.co.uk",
                "https://sub.domain.example.com",
                "http://sub.domain.co.cn",
                "https://example.me",
                "http://example.biz",
                "ftp://example.tv",
                "example.org",
                "sub.example.org",
                "http://example.company",
                "example",
                "",
                "http://",
                "not-a-url",
                "aa.测试.中国",
                "测试.中国",
                "aaa.bbb.co.uk",
                "mail.xxx.edu.cn",
                "xxx.edu.cn",
                "geo.test.com.co",
                "www.freelancer.com.ar",
                "www.freelancer.com.al"
        };

        for (String url : testUrls) {
            System.out.println("URL: " + url + " -> Top Domain: " + getTopDomain(url));
        }
    }
}
