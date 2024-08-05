/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2023-11-01
 **/
package testnet.common.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class FileUtils {

    private static final Logger log = Logger.getLogger(FileUtils.class.getName());

    public static String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }

    public static String buildPath(String currentPath, String... parts) {
        Path path = Paths.get(currentPath);
        for (String part : parts) {
            path = path.resolve(part);
        }
        return path.toString();
    }

    public static String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        return "";
    }

    // 判断文件是否存在
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    // 判断文件夹是否存在
    public static boolean directoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.exists() && directory.isDirectory();
    }

    // 创建文件夹
    public static boolean createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.mkdirs();
    }

    // 删除文件
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.delete();
    }


    // 删除文件夹及其内容
    public static boolean deleteDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }

        // 递归删除文件夹中的所有文件和子文件夹
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
        }

        // 删除空文件夹
        return directory.delete();
    }

    // 列出文件夹中的文件和子文件夹
    public static File[] listFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            return directory.listFiles();
        }
        return null;
    }

    // 复制文件
    public static boolean copyFile(String sourcePath, String destinationPath) {
        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);

        if (sourceFile.exists() && sourceFile.isFile()) {
            // 使用流复制文件
            try {
                Files.copy(sourceFile.toPath(), destinationFile.toPath());
                return true;
            } catch (IOException e) {
                log.warning(e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    // 移动文件
    public static boolean moveFile(String sourcePath, String destinationPath) {
        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            // 使用移动操作来移动文件
            return sourceFile.renameTo(destinationFile);
        } else {
            return false;
        }
    }

    // 创建文件
    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        try {
            return file.createNewFile();
        } catch (IOException e) {
            log.warning(e.getMessage());
            return false;
        }
    }

    public static boolean createFileAndWrite(String filePath, String content) {
        File file = new File(filePath);
        try {
            // 创建文件
            if (!file.exists() && !file.createNewFile()) {
                System.err.println("Failed to create file: " + filePath);
                return false;
            }

            // 写入内容
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            }

            return true;
        } catch (IOException e) {
            log.warning("Error writing to file: " + e.getMessage());
            return false;
        }
    }

}
