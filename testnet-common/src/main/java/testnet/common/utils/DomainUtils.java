package testnet.common.utils;


import com.google.common.net.InternetDomainName;


public class DomainUtils {



    public static String getTopDomain(String subDomain) {
        InternetDomainName owner =
                InternetDomainName.from(subDomain).topDomainUnderRegistrySuffix();
        return owner.toString();
    }

    public static void main(String[] args) {
        System.out.println(getTopDomain("akadns.net"));
        System.out.println(getTopDomain("www.hellomoto.com.br"));
        System.out.println(getTopDomain("mailx.watsons.com.my"));
        System.out.println(getTopDomain("www.execute-api.cn-north-1.amazonaws.com.cn"));
        System.out.println(getTopDomain("qq.compute.amazonaws.com.cn"));
    }
}
