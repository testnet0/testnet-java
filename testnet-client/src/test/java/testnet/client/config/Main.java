package testnet.client.config;

import testnet.common.utils.CommandUtils;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        CommandUtils.CommandResult result = CommandUtils.executeCommand("whoami | ipconfig");
        System.out.println(result.getOutput());
    }
}