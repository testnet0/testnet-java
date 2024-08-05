/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2023-11-09
 **/
package testnet.common.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtils {
    private static final Logger logger = Logger.getLogger(LogUtils.class.getName());

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }
}

