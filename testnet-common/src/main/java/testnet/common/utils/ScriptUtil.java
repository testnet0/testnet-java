package testnet.common.utils;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class ScriptUtil {

    private static final String osName = System.getProperty("os.name").toLowerCase();

    /**
     * make script file
     */
    @SneakyThrows
    public static void markScriptFile(String scriptFileName, String content) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(scriptFileName);
            fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Error writing to file: {}", e.getMessage());
            throw new IOException("Failed to write to file: " + scriptFileName, e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("Error closing file: {}", e.getMessage());
                }
            }
        }
    }

    public static String generateFileName() {
        if (osName.contains("win")) {
            return UUID.randomUUID().toString().replace("-", "") + ".bat";
        } else {
            return UUID.randomUUID().toString().replace("-", "") + ".sh";
        }
    }

    public static String getCommandPrefix() {
        if (osName.contains("win")) {
            return "cmd /c";
        } else {
            return "bash";
        }
    }

    public static CommandUtils.CommandResult execToFile(String command) {
        String scriptFileName = generateFileName();
        FileUtils.createFileAndWrite(scriptFileName, command);
        if (!FileUtils.fileExists(scriptFileName)) {
            log.error("文件创建失败");
            return null;
        }
        return CommandUtils.executeCommand(ScriptUtil.getCommandPrefix() + " " + scriptFileName);
    }
}