/**
 * @program: testnet-client
 * @description:
 * @author: TestNet
 * @create: 2023-10-30
 **/
package testnet.common.utils;

import lombok.Getter;
import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Duration;


public class CommandUtils {

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
        DefaultExecutor executor;
        if (workingDirectory != null) {
            executor = DefaultExecutor.builder().setWorkingDirectory(new File(workingDirectory)).get();
        } else {
            executor = DefaultExecutor.builder().get();
        }
        executor.setExitValue(exitCode);
        if (timeoutInSeconds > 0) {
            ExecuteWatchdog watchdog = ExecuteWatchdog.builder().setTimeout(Duration.ofSeconds(timeoutInSeconds)).get();
            executor.setWatchdog(watchdog);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        executor.setStreamHandler(streamHandler);
        int exitValue;
        try {
            exitValue = executor.execute(cmdLine);
            return new CommandResult(exitValue, outputStream.toString());
        } catch (ExecuteException e) {
            return new CommandResult(e.getExitValue(), outputStream + "\n" + errorStream + "\n" + e.getMessage());
        } catch (Exception e) {
            return new CommandResult(-1, outputStream + "\n" + errorStream + "\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CommandResult result = executeCommand("D: && pwd");
        System.out.println(result.output);
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


