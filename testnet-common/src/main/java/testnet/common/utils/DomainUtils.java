package testnet.common.utils;


import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory;


public class DomainUtils {

    private static final PublicSuffixList suffixList;

    static {
        PublicSuffixListFactory factory = new PublicSuffixListFactory();
        suffixList = factory.build();
    }

    public static String getTopDomain(String subDomain) {
        return suffixList.getRegistrableDomain(subDomain);
    }
}
