package ch.sebi.acmf.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Sebastian on 26.05.2017.
 */
public class ZipUtils {
    public static void zipDirectory(File dest) {
        List<String> fileList = new ArrayList<>();
        findFiles(SettingsManager.DEVICE_CONFIGURATIO_DIRECTORY, SettingsManager.DEVICE_CONFIGURATIO_DIRECTORY, fileList);
        writeZip(dest, SettingsManager.DEVICE_CONFIGURATIO_DIRECTORY, fileList);
    }

    private static void findFiles(File node, File root, List<String> fileList) {
        if(node.isFile()) {
            fileList.add(node.getAbsolutePath().replace(root.getAbsolutePath() + "/", ""));
        }

        if(node.isDirectory()) {
            for(File f : node.listFiles())  {
                findFiles(f,root, fileList);
            }
        }
    }

    private static void writeZip(File dest, File root, List<String> fileList) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest));
            byte data[] = new byte[2048];
            for(String f : fileList) {
                BufferedInputStream bin = new BufferedInputStream(new FileInputStream(root.getPath() + "/" + f));
                ZipEntry entry = new ZipEntry(f);
                out.putNextEntry(entry);
                int count;
                while((count = bin.read(data, 0, data.length)) != -1) {
                    out.write(data, 0, count);
                }
                bin.close();
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void unzip(File zipFile, File dest) {
        if(!dest.exists())
            dest.mkdirs();

        try {
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry = zipIn.getNextEntry();
            while(entry != null) {
                String filePath = dest.getPath() + File.separator + entry.getName();
                if(!entry.isDirectory()) {
                    if(entry.getName().lastIndexOf('.') == -1) {
                        System.err.println("WARNING: File '" + entry.getName() + "' isn't a device configuration file! The extention must be '.dc'.");
                        break;
                    }
                    if (!entry.getName().substring(entry.getName().lastIndexOf('.')).equals(".dc")) {
                        System.err.println("WARNING: File '" + entry.getName() + "' isn't a device configuration file! The extention must be '.dc'.");
                        break;
                    }
                    extractFile(zipIn, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdirs();
                }

                zipIn.closeEntry();;
                entry = zipIn.getNextEntry();
            }
            zipIn.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
