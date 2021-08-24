package com.qooco.boost.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static Logger logger = LogManager.getLogger(FileUtils.class);
    private static final String DOT = ".";
    private static final String DEFAULT_NAME = "pata";
    private static final String INVALID_CHARACTER = "[\\\\/:*?\"<>|,']";
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static String removeInvalidCharacter(String fileName) {
        String result = fileName.replaceAll(INVALID_CHARACTER, "").replaceAll(" ", "");
        if (result.length() < 3) {
            return DEFAULT_NAME;
        }
        return result.trim();
    }

    public static boolean saveFile(String root, String relativePath, MultipartFile file) {
        try {
            String filePath = new StringBuffer(root).append(relativePath).toString();
            File dest = new File(filePath);
            file.transferTo(dest);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public static void deleteFile(String fileName) {
        org.codehaus.plexus.util.FileUtils.fileDelete(fileName);
    }

    public static void deleteAllFile(String path) {
        try {
            org.codehaus.plexus.util.FileUtils.cleanDirectory(path);
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
        }
    }

    public static String createFileName(String originalName) {
        String extension = FilenameUtils.getExtension(originalName);
        String baseName = RandomStringUtils.randomAlphanumeric(8);
        try {
            return File.createTempFile(baseName, DOT + extension).getName();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return "";
    }

    public static String createFileName(String baseName, String subPrefix, String extension) {
        return new StringBuffer(baseName).append(subPrefix).append(DOT).append(extension).toString();
    }


    public static String createRootFolder(String rootFolder) {
        File folder = new File(rootFolder);
        if (createDirectory(folder)) return folder.getPath();
        return "";
    }

    public static String createFolder(String parent, String folderName) {
        File folder = new File(parent, folderName);
        if (createDirectory(folder)) return folder.getPath();
        return "";
    }

    public static boolean checkExistedFolder(String path) {
        File folder = new File(path);
        return folder.exists();
    }

    private static boolean createDirectory(File folder) {
        if (folder.exists()) return true;

        if (folder.mkdirs()) {
            return setPermission(folder, true, true, true);
        }
        return false;
    }

    private static boolean setPermission(File folder, boolean executable, boolean readable, boolean writable) {
        if (!folder.setExecutable(executable, true)) return false;
        if (!folder.setReadable(readable, true)) return false;
        return folder.setWritable(writable, true);
    }


}
