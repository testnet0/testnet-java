/**
 * @program: testnet-client
 * @description:
 * @author: TestNet
 * @create: 2023-10-30
 **/
package testnet.common.utils;

import lombok.Getter;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.logging.Logger;


public class CommandUtils {

    private static final Logger log = Logger.getLogger(CommandUtils.class.getName());


    public static CommandResult executeCommand(String command) {
        return executeCommand(command, null, 0, 0);
    }

    public static CommandResult executeCommand(int exitCode, String command) {
        return executeCommand(command, null, 0, exitCode);
    }

    public static CommandResult executeCommand(String command, String workingDirectory) {
        return executeCommand(command, workingDirectory, 0, 0);
    }

    public static CommandResult executeCommand(String command, int timeoutInSeconds) {
        return executeCommand(command, null, timeoutInSeconds, 0);
    }

    public static CommandResult executeCommand(String command, String workingDirectory, int timeoutInSeconds, int exitCode) {
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = DefaultExecutor.builder().get();
        executor.setExitValue(exitCode);
        if (timeoutInSeconds > 0) {
            ExecuteWatchdog watchdog = ExecuteWatchdog.builder().setTimeout(Duration.ofSeconds(timeoutInSeconds)).get();
            executor.setWatchdog(watchdog);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        int exitValue = 0;
        try {
            exitValue = executor.execute(cmdLine);
            return new CommandResult(exitValue, outputStream.toString());
        } catch (Exception e) {
            return new CommandResult(exitValue, outputStream.toString());
        }
    }


    @Getter
    public static class CommandResult {
        private final int exitCode;
        private final String output;

        public CommandResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

    }
}


