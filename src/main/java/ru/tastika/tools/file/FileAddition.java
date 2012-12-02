package ru.tastika.tools.file;


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * User: hobal
 * Date: 09.01.2005
 * Time: 16:09:42
 * <p/>
 * Методы - дополнения для работы с файлами
 */
public class FileAddition {


    private static final Pattern fileNameFromCssUrlPattern = Pattern.compile("(?i:^url\\([\\.\\-_!@#\\$%\\^\\+=,&\\(\\)\\s\\w\\d\\\\\\/]*\\/([\\.\\-_!@#\\$%\\^\\+=,&\\(\\)\\s\\w\\d]+\\.\\w{3,4})\\s*\\)$)");
    private static final Pattern fullFileNameFromCssUrlPattern = Pattern.compile("(?i:^url\\(([\\.\\-_!@#\\$%\\^\\+=,&\\(\\)\\s\\w\\d\\\\\\/]*\\/[\\.\\-_!@#\\$%\\^\\+=,&\\(\\)\\s\\w\\d]+\\.\\w{3,4})\\s*\\)$)");
    private static final Pattern notFileSybmols = Pattern.compile("[\\\\\\/\\:\\*\\?\\<\\>\\|\\\"\\'\\.\\,\\;]");
    private static String tempDirName = "temp";

    private static final long KILOBYTE = 1024L;
    private static final long MEGABYTE = 1024L * 1024L;
    private static final long GIGABYTE = 1024L * 1024L * 1024L;
    private static final long TERABYTE = 1024L * 1024L * 1024L * 1024L;


    public static boolean createFile(File file, String fileContent) {
        boolean success = false;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(fileContent);
            out.close();
            success = true;
        } catch (IOException e) {
        }
        return success;
    }

    public static boolean createFile(String fileName, String fileContent) {
        boolean success = false;
        File file = new File(fileName);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            if (fileContent != null) {
                out.write(fileContent);
                out.close();
            }
            success = true;
        } catch (IOException e) {
        }
        return success;
    }

    public static String getFileExtension(File file) {
        String extension = "";
        String fileName = file.getName();
        int dotPosition = fileName.lastIndexOf('.');
        if (dotPosition != -1) {
            extension = fileName.substring(dotPosition + 1);
        }
        return extension;
    }


    public static String getFileExtension(String fileName) {
        String extension = "";
        if (fileName != null) {
            int dotPosition = fileName.lastIndexOf('.');
            if (dotPosition != -1) {
                extension = fileName.substring(dotPosition + 1);
            }
        }
        return extension.toLowerCase();
    }


    /**
     * Возвращет имя файла без расширения
     *
     * @param fileName - ��� �����
     * @return ��� ����� ��� ����������
     */
    public static String getFileBaseName(String fileName) {
        String baseName = (fileName != null ? fileName : "");
        int dotPosition = baseName.lastIndexOf('.');
        if (dotPosition != -1) {
            baseName = baseName.substring(0, dotPosition);
        }
        return baseName;
    }


    /**
     * �������� �� ��������� ���� ������ ��� �����
     *
     * @param filePath - ���� � ��������� �����
     * @return ������ - �������� �����
     */
    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
//        String baseName = filePath;
//        int separatorPosition = filePath.lastIndexOf('/');
//        if (separatorPosition != -1) {
//            baseName = filePath.substring(separatorPosition + 1, filePath.length());
//        }
//        return baseName;
    }

    public static File getFile(String filePath) {
        File file = new File(filePath);
        return file;
    }


    /**
     * �������� ���� ��� �������� ����� � ��� ��������� '/'
     *
     * @param filePath - ���� � ��������� �����
     * @return ������ - ���� ��� �������� �����
     */
    public static String getParentDirectory(String filePath) {
        String parent = "";
        int separatorPosition = filePath.lastIndexOf(File.separatorChar);
        if (separatorPosition != -1) {
            parent = filePath.substring(0, separatorPosition);
        }
        return parent;
    }


    public static void writeObjectToXML(String xmlFileName, Object object) throws FileNotFoundException {
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(xmlFileName)));
        e.writeObject(object);
        e.close();
    }


    public static void addDirectoryToZip(String zipFileName, String directoryName, boolean addParentDirectory) throws IOException {
        File file = new File(zipFileName);
        File directory = new File(directoryName);
        if (directory.isDirectory()) {
            File tempZipFile = new File(file.getParentFile(), "~~" + file.getName());
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempZipFile)));
            if (file.isFile()) {
                ZipFile zipFile = new ZipFile(zipFileName);
                for (Enumeration enumeration = zipFile.entries(); enumeration.hasMoreElements(); ) {
                    ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    zipOutputStream.putNextEntry(zipEntry);
                    int b = inputStream.read();
                    while (b != -1) {
                        zipOutputStream.write(b);
                        b = inputStream.read();
                    }
                    inputStream.close();
                    zipOutputStream.closeEntry();

                }
                zipFile.close();
            }
            if (addParentDirectory) {
                addFilesToZipOutputStream(directory, zipOutputStream, directory.getName());
            } else {
                addFilesToZipOutputStream(directory, zipOutputStream, "");
            }
            zipOutputStream.close();
            file.delete();
            tempZipFile.renameTo(file);
        }
    }


    private static void addFilesToZipOutputStream(File parentDirectory, ZipOutputStream zipOutputStream, String entryPrefix) throws IOException {
        File[] files = parentDirectory.listFiles();
        for (File file : files) {
            String entryName;
            if (entryPrefix == null || entryPrefix.length() == 0) {
                entryName = file.getName();
            } else {
                entryName = entryPrefix + File.separatorChar + file.getName();
            }
            if (file.isDirectory()) {
                addFilesToZipOutputStream(file, zipOutputStream, entryName);
            } else if (file.isFile()) {
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                zipOutputStream.putNextEntry(zipEntry);
                int b = inputStream.read();
                while (b != -1) {
                    zipOutputStream.write(b);
                    b = inputStream.read();
                }
                inputStream.close();
                zipOutputStream.closeEntry();
            }
        }
    }


    public static void addDirectoryFilesToZip(String zipFileName, String directoryName, boolean insertDir) throws IOException {
        ZipFile zipFile = new ZipFile(zipFileName);
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName + "temp")));
        ZipEntry zipEntry = zipFile.getEntry("templates.xml");
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        zipOutputStream.putNextEntry(zipEntry);
        int b = inputStream.read();
        while (b != -1) {
            zipOutputStream.write(b);
            b = inputStream.read();
        }
        inputStream.close();
        zipFile.close();
        zipOutputStream.closeEntry();
        File directory = new File(directoryName);
        String dirName = directoryName + File.separatorChar;
        File[] files = directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String ext = getFileExtension(pathname);
                return ext.equals("gif") || ext.equals("jpg") || ext.equals("png");
            }
        });
        for (File file : files) {
            String entry = file.getName();
            if (insertDir) {
                entry = dirName + entry;
            }
            zipOutputStream.putNextEntry(new ZipEntry(entry));
            inputStream = new BufferedInputStream(new FileInputStream(file));
            b = inputStream.read();
            while (b != -1) {
                zipOutputStream.write(b);
                b = inputStream.read();
            }
            inputStream.close();
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();
        File oldFile = new File(zipFileName);
        if (!oldFile.delete() || !new File(zipFileName + "temp").renameTo(oldFile)) {
            throw new IOException("���������� ������� ��� ������������� ����");
        }
    }


    public static void copy(File src, File dest) throws IOException {
        File parentFile = src.getParentFile();
        if (!parentFile.isDirectory()) {
            parentFile.mkdirs();
        }
        parentFile = dest.getParentFile();
        if (!parentFile.isDirectory()) {
            parentFile.mkdirs();
        }
        try {
            // Create channel on the source
            FileChannel srcChannel = new FileInputStream(src).getChannel();

            // Create channel on the destination
            FileChannel dstChannel = new FileOutputStream(dest).getChannel();

            // Copy file contents from source to destination
            long transferredBytes = 0L;
            long totalBytes = srcChannel.size();
            while (transferredBytes < totalBytes) {
                long availableBytes = (long) (Runtime.getRuntime().freeMemory() * 0.9);
                availableBytes = Math.min(availableBytes, totalBytes - transferredBytes);
                transferredBytes += dstChannel.transferFrom(srcChannel, transferredBytes, availableBytes);
            }

            // Close the channels
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeObjectToZipXML(String xmlFileName, Object object) throws FileNotFoundException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(getFileBaseName(xmlFileName) + ".zip")));
        try {
            File file = new File(xmlFileName);
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            XMLEncoder e = new XMLEncoder(zipOutputStream);
            e.writeObject(object);
            e.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Object readObjectFromXML(String xmlFileName) throws Exception {
        XMLDecoderExceptionListener exceptionListener = new XMLDecoderExceptionListener();
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(xmlFileName)), xmlFileName, exceptionListener);
        Object result = d.readObject();
        d.close();
        if (exceptionListener.isExceptionThrown()) {
            throw exceptionListener.getException();
        }
        return result;
    }


    public static Object readSerializedObject(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
        Object o = inputStream.readObject();
        inputStream.close();
        return o;
    }


    public static void writeSerializedObject(String fileName, Serializable serializableObject) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
        outputStream.writeObject(serializableObject);
        outputStream.close();
    }


    public static Object readObjectFromZipXML(String xmlFileName) throws Exception {
        ZipFile zipFile = new ZipFile(getFileBaseName(xmlFileName) + ".zip");
        File file = new File(xmlFileName);
        XMLDecoderExceptionListener exceptionListener = new XMLDecoderExceptionListener();
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(zipFile.getInputStream(zipFile.getEntry(file.getName()))), zipFile, exceptionListener);
        Object result = d.readObject();
        d.close();
        zipFile.close();
        if (exceptionListener.isExceptionThrown()) {
            throw exceptionListener.getException();
        }
        return result;
    }


    public static String getNextFreeFileName(String fileName) {
        String nextFileName = fileName;
        String baseFileName = fileName;
        String extension = getFileExtension(fileName);
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos != -1) {
            nextFileName = fileName.substring(0, dotPos);
        }
        char[] nextFileNameChar = nextFileName.toCharArray();
        int number = 0;
        int digit;
        int charArrayLength = nextFileNameChar.length;
        for (int i = 0; i < charArrayLength; i++) {
            digit = Character.digit(nextFileNameChar[charArrayLength - i - 1], 10);
            if (digit != -1) {
                number += digit * Math.pow(10, i);
            } else {
                baseFileName = fileName.substring(0, charArrayLength - i);
                break;
            }
        }
        if (String.valueOf(number).equals(baseFileName)) {
            baseFileName = "";
        }
        String numberString = String.valueOf(number + 1);
        StringBuffer stringBuffer = new StringBuffer();
        int numberLength = 3;
        int reminderLength = numberLength - numberString.length();
        for (int i = 0; i < reminderLength; i++) {
            stringBuffer.append('0');
        }
        if (extension.length() == 0) {
            return baseFileName + stringBuffer.toString() + numberString;
        } else {
            return baseFileName + stringBuffer.toString() + numberString + '.' + extension;
        }
    }


    public static String getNextFreeFileName(String fileName, int minNumberLength) {
        String nextFileName = fileName;
        String baseFileName = fileName;
        String extension = getFileExtension(fileName);
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos != -1) {
            nextFileName = fileName.substring(0, dotPos);
        }
        char[] nextFileNameChar = nextFileName.toCharArray();
        int number = 0;
        int digit;
        int charArrayLength = nextFileNameChar.length;
        for (int i = 0; i < charArrayLength; i++) {
            digit = Character.digit(nextFileNameChar[charArrayLength - i - 1], 10);
            if (digit != -1) {
                number += digit * Math.pow(10, i);
            } else {
                //baseFileName = fileName.substring(0, charArrayLength - i);
                break;
            }
        }
/*
        if (String.valueOf(number).equals(baseFileName)) {
            baseFileName = "";
        }
*/
        String numberString = String.valueOf(number + 1);
        StringBuilder stringBuffer = new StringBuilder();
        int numberLength = minNumberLength;
        int reminderLength = numberLength - numberString.length();
        for (int i = 0; i < reminderLength; i++) {
            stringBuffer.append('0');
        }
        if (extension.length() == 0) {
            if (number == 0) {
                return baseFileName + stringBuffer.toString() + numberString;
            } else {
                return baseFileName.replaceAll(String.valueOf(number) + "$", stringBuffer.toString() + numberString);
            }
        } else {
            if (number == 0) {
                return baseFileName + stringBuffer.toString() + numberString + '.' + extension;
            } else {
                return baseFileName.replaceAll(String.valueOf(number) + "$", stringBuffer.toString() + numberString) + '.' + extension;
            }
        }
    }


    public static int getNumberFromFileName(String fileName) {
        String nextFileName = fileName;
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos != -1) {
            nextFileName = fileName.substring(0, dotPos);
        }
        char[] nextFileNameChar = nextFileName.toCharArray();
        int number = 0;
        int digit;
        int charArrayLength = nextFileNameChar.length;
        for (int i = 0; i < charArrayLength; i++) {
            digit = Character.digit(nextFileNameChar[charArrayLength - i - 1], 10);
            if (digit != -1) {
                number += digit * Math.pow(10, i);
            } else {
                break;
            }
        }
        return number;
    }


    public static boolean unzipFiles(File selectedFile, File unpackDirectory, boolean deleleContentUnpackDir) throws ZipFilleException {
        boolean res = true;
        if (!unpackDirectory.exists()) {
            unpackDirectory.mkdir();
        }
        if (selectedFile.isFile() && unpackDirectory.isDirectory()) {
            try {
                ZipFile zipFile = new ZipFile(selectedFile);
                if (deleleContentUnpackDir) {
                    res = emptyDirectory(unpackDirectory);
                }
                if (res) {
                    for (Enumeration enumeration = zipFile.entries(); enumeration.hasMoreElements(); ) {
                        ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                        File unzippedFile = new File(unpackDirectory, zipEntry.getName());
                        String fullPath = unzippedFile.getAbsolutePath();
                        String directoryString = fullPath.substring(0, fullPath.lastIndexOf(unzippedFile.getName()) - 1);
                        File directory = new File(directoryString);
                        directory.mkdirs();
                        res = unzippedFile.createNewFile();
                        if (!res) {
                            break;
                        }
                        InputStream inputStream = zipFile.getInputStream(zipEntry);
                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(unzippedFile));
                        int b = inputStream.read();
                        while (b != -1) {
                            outputStream.write(b);
                            b = inputStream.read();
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                }
            } catch (ZipException ze) {
                throw new ZipFilleException("������ ������������");
            } catch (ClassCastException cce) {
                throw new ZipFilleException("�������� ������ zip �����");
            } catch (IOException e) {
                throw new ZipFilleException("�� ������ ���� " + selectedFile.getName());
            }
        } else {
            res = false;
        }
        return res;
    }


    public static boolean unzipFilesWithProgress(File selectedFile, File unpackDirectory, boolean deleleContentUnpackDir, ProgressBarObject object) throws ZipFilleException {
        boolean res = true;
        if (!unpackDirectory.exists()) {
            unpackDirectory.mkdir();
        }
        if (selectedFile.isFile() && unpackDirectory.isDirectory()) {
            try {
                ZipFile zipFile = new ZipFile(selectedFile);
                if (deleleContentUnpackDir) {
                    res = emptyDirectory(unpackDirectory);
                }
                if (res) {
                    for (Enumeration enumeration = zipFile.entries(); enumeration.hasMoreElements(); ) {
                        ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                        String zipEntryName = zipEntry.getName();
                        object.setProgressMessage(zipEntryName);
                        File unzippedFile = new File(unpackDirectory, zipEntryName);
                        String fullPath = unzippedFile.getAbsolutePath();
                        String directoryString = fullPath.substring(0, fullPath.lastIndexOf(unzippedFile.getName()) - 1);
                        File directory = new File(directoryString);
                        directory.mkdirs();
                        if (!unzippedFile.isFile()) {
                            res = unzippedFile.createNewFile();
                            if (!res) {
                                break;
                            }
                            InputStream inputStream = zipFile.getInputStream(zipEntry);
                            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(unzippedFile));
                            int b = inputStream.read();
                            while (b != -1) {
                                outputStream.write(b);
                                b = inputStream.read();
                            }
                            inputStream.close();
                            outputStream.close();
                        }
                        object.incProgress();
                    }
                }
            } catch (ZipException ze) {
                throw new ZipFilleException("������ ������������");
            } catch (ClassCastException cce) {
                throw new ZipFilleException("�������� ������ zip �����");
            } catch (IOException e) {
                throw new ZipFilleException("�� ������ ���� " + selectedFile.getName());
            }
        } else {
            res = false;
        }
        return res;
    }


    public static boolean unzipFiles(File selectedFile, File unpackDirectory) throws ZipFilleException {
        return unzipFiles(selectedFile, unpackDirectory, true);
    }


    public static boolean emptyDirectory(File unpackDirectory) {
        boolean result = true;
        if (unpackDirectory.isDirectory()) {
            File[] files = unpackDirectory.listFiles();
            for (File file : files) {
                result = delete(file);
            }
        } else {
            result = false;
        }
        return result;
    }


    public static boolean emptyDirectory(File unpackDirectory, boolean onlyFiles) {
        boolean result = true;
        if (unpackDirectory.isDirectory()) {
            File[] files = unpackDirectory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (!onlyFiles || files[i].isFile()) {
                    result = delete(files[i]);
                }
            }
        } else {
            result = false;
        }
        return result;
    }


    public static boolean delete(File file) {
        boolean res = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length && res; i++) {
                res = delete(files[i]);
            }
        }
        res &= file.delete();
        return res;
    }


    public static TreeSet copyFiles(TreeSet<String> filesToCopyPaths, String copyToFolderPath) {
        TreeSet<String> resultFilesPaths = new TreeSet<String>();
        for (String filePath : filesToCopyPaths) {
            File fileToCopy = new File(filePath);
            if (fileToCopy.isFile()) {
                try {
                    String pathname = copyToFolderPath + fileToCopy.getName();
                    copy(fileToCopy, new File(pathname));

                    resultFilesPaths.add(pathname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultFilesPaths;
    }


    public static String getFileNameFromCssUrl(String testString) {
        String foundMatch = "";
        Matcher m = fileNameFromCssUrlPattern.matcher(testString);
        if (m.find()) {
            foundMatch = m.group(1);
        }
        return foundMatch;
    }


    public static String getFullFileNameFromCssUrl(String testString) {
        String foundMatch = "";
        Matcher m = fullFileNameFromCssUrlPattern.matcher(testString);
        if (m.find()) {
            foundMatch = m.group(1);
        }
        return foundMatch;
    }


    /**
     * ��������� ���� �� ��������� � path ����������, ������� �� � ������ ���������� � ���������� File, ���� ��������� ���������� ����������
     *
     * @param path - ���� � ����������
     * @return ������ File - ����������, ��������������� path, ���� ���������� ��������� (���� �� ����, ������� �������), null - ���� ���������� ��� � ������� �� �������
     */
    public static File getOrCreateDirectory(String path) {
        File dir = new File(path);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            return null;
        } else {
            return dir;
        }
    }


    /**
     * ���������, ���������� �� ��������� � dir ����������, ������� �� � ������ ���������� � ���������� File, ���� ��������� ���������� ����������
     *
     * @param dir - ����������
     * @return ������ File - ����������, ��������������� dir, ���� ���������� ��������� (���� �� ����, ������� �������), null - ���� ���������� ��� � ������� �� �������
     */
    public static File getOrCreateDirectory(File dir) {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            return null;
        } else {
            return dir;
        }
    }


    /**
     * ��������� ����������� ����� srcFile � ���� destFile. ��� ���� ����������� ������� ����� destFile �� �����������.
     * ���� ���� ���������� � ����� srcFile �� �������, ����� ����������� ������������� �� �����, ���� �� �� ����������,
     * ����� ����� destFile ����� ������ ��������� ���, � srcFile ����� ���������� � ����
     *
     * @param srcFile  - ���� ��� �����������
     * @param destFile - ����, � ������� ���������� �����������
     * @return ����, � ������� ���� ��������� �����������, ��� null, ���� ����������� �� �������
     */
    public static File copyFileIfNeed(File srcFile, File destFile) {
        if (srcFile.isFile()) {
            boolean needCopy = false;
            if (destFile.isFile()) {
                if (destFile.length() != srcFile.length()) {
                    needCopy = true;
                    String newImageName = getNextFreeFileName(destFile.getName());
                    destFile = new File(destFile.getParentFile(), newImageName);
                    while (destFile.isFile()) {
                        newImageName = FileAddition.getNextFreeFileName(destFile.getName());
                        destFile = new File(destFile.getParentFile(), newImageName);
                    }
                }
            } else {
                getOrCreateDirectory(destFile.getParentFile());
                needCopy = true;
            }
            if (needCopy) {
                try {
                    FileAddition.copy(srcFile, destFile);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    destFile = null;
                }
            }
        } else {
            destFile = null;
        }
        return destFile;
    }


    /**
     * ��������� ����������� �����, �������������� �� ���� srcFilePath � ���� destFilePath. ����������� ������� ����� srcFilePath.
     * ��� ����������� ����������� ������� ����� destFile.
     * ���� ���� ���������� � ����� srcFile �� �������, ����� ����������� ������������� �� �����, ���� �� �� ����������,
     * ����� ����� destFile ����� ������ ��������� ���, � srcFile ����� ���������� � ����.
     * ����� ���������� ��� �����, ���� ���� ��� ���������� ��� ������ ������ � ������ ������ (""), ���� ���� �� ���������� ���
     * ���������� � ��� �� ������
     *
     * @param srcFilePath  ���� �� �����, ������� ��������� �����������
     * @param destFilePath ���� �� �����, � ������� ���������� �����������
     * @return ���������� ��� ����� ��� ������ ������, ���� ���� �� ��� ���������� ��� ���������� � ��� �� ������
     */
    public static String copyFileIfNeed(String srcFilePath, String destFilePath) {
        String fileName = "";
        File srcFile = new File(srcFilePath);
        if (srcFile.isFile()) {
            boolean needCopy = false;
            File destFile = new File(destFilePath);
            if (destFile.isFile()) {
                if (destFile.length() != srcFile.length()) {
                    needCopy = true;
                    fileName = getNextFreeFileName(destFile.getName());
                    destFile = new File(destFile.getParentFile(), fileName);
                    while (destFile.isFile()) {
                        fileName = FileAddition.getNextFreeFileName(destFile.getName());
                        destFile = new File(destFile.getParentFile(), fileName);
                    }
                }
            } else {
                getOrCreateDirectory(destFile.getParentFile());
                needCopy = true;
            }
            if (needCopy) {
                try {
                    FileAddition.copy(srcFile, destFile);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return fileName;
    }


    public static String replaceNotFileSymbols(String fileName, String replacement) {
        return notFileSybmols.matcher(fileName).replaceAll(replacement);
    }


    public static String includeStartingSlash(String fileName) {
        fileName = fileName.trim().replaceAll("\\\\", "/");
        if (fileName.length() > 0 && fileName.charAt(0) == '/') {
            return fileName;
        }
        return '/' + fileName;
    }


    public static String removeStartingSlash(String fileName) {
        fileName = fileName.trim().replaceAll("\\\\", "/");
        if (fileName.length() > 0 && fileName.charAt(0) == '/') {
            return fileName.substring(1);
        }
        return fileName;
    }


    public static String includeTrailingSlash(String fileName) {
        fileName = fileName.trim().replaceAll("\\\\", "/");
        if (!fileName.endsWith("/")) {
            fileName = fileName + "/";
        }
        return fileName;
    }


    public static void testDirectories(File localFile) {
        File parentDirectory = localFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.isDirectory()) {
            parentDirectory.mkdirs();
        }
    }

    public static String getFileStringContentWithWhiteSpaces(File file, boolean newLines) {
        StringBuilder content = new StringBuilder("");
        if (file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    if (newLines) {
                        content.append('\n');
                    }
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                //e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                //e.printStackTrace();
            }
        }
        return content.toString();
    }


    public static String getFileStringContent(File file, boolean newLines) {
        StringBuilder content = new StringBuilder("");
        if (file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    if (newLines) {
                        content.append('\n');
                    }
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                //e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                //e.printStackTrace();
            }
        }
        return content.toString();
    }


    public static String getFileStringContent(File file) {
        return getFileStringContent(file, false);
    }


    public static String getStreamContent(InputStream stream) {
        StringBuilder content = new StringBuilder("");
        if (stream != null) {
            try {
                InputStreamReader reader = new InputStreamReader(stream);
                int character;
                while ((character = reader.read()) != -1) {
                    content.append(String.valueOf((char) character));
                }
                stream.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return content.toString();
    }


    public static String addTraingSystemDependentSlash(String path) {
        String slash = System.getProperty("file.separator");
        if (slash == null || slash.length() == 0) {
            slash = "/";
        }
        if (!path.endsWith(slash)) {
            path += slash;
        }
        return path;
    }


    public static String getRelativePath(File parentDir, File childFile, boolean precedingSlash) {
        String path = childFile.getAbsolutePath();
        if (path.contains(parentDir.getAbsolutePath())) {
            path = path.substring(parentDir.getAbsolutePath().length());
            if (precedingSlash) {
                return FileAddition.includeStartingSlash(path);
            }
            return FileAddition.removeStartingSlash(path);
        }
        return path;
    }


    public static File getTempDirectory() {
        File tempDir = new File(tempDirName);
        if (!tempDir.isDirectory()) {
            tempDir.mkdir();
        }
        return tempDir;
    }


    public static boolean renameFile(File oldFile, File newFile, boolean overwrite) {
        return oldFile.isFile() && ((overwrite && newFile.exists() && newFile.delete()) | oldFile.renameTo(newFile));
    }


    public static File getDirectory(String directoryName) {
        File dir = new File(directoryName);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }


    public static String getFileSizeString(long byteFilesize) {
        double filesize = byteFilesize;
        String unit = "b";
        /* terabytes */
        if (filesize >= TERABYTE) {
            filesize /= TERABYTE;
            unit = "Tb";
        }
        /* gigabytes */
        else if (filesize >= GIGABYTE) {
            filesize /= GIGABYTE;
            unit = "Gb";
        }
        /* megabytes */
        else if (filesize >= MEGABYTE) {
            filesize /= MEGABYTE;
            unit = "Mb";
        }
        /* kilobytes */
        else if (filesize >= KILOBYTE) {
            filesize /= KILOBYTE;
            unit = "Kb";
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(filesize) + unit;
    }


    public static long getDirectoriesSize(File[] files) {
        long size = 0L;
        if (files != null) {
            for (File file : files) {
                size += getFileSize(file);
            }
        }
        return size;
    }


    public static long getDirectoriesSize(List files) {
        long size = 0L;
        if (files != null) {
            for (Object obj : files) {
                File file = (File) obj;
                size += getFileSize(file);
            }
        }
        return size;
    }


    private static long getFileSize(File file) {
        return file.isDirectory() ? getDirectoriesSize(file.listFiles()) : file.length();
    }

}
