package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void zipFile(File fileToZip, File zipFile) throws IOException {
        if (!fileToZip.exists()) {
            throw new FileNotFoundException("File not found: " + fileToZip.getAbsolutePath());
        }
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(fileToZip)) {

            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }

        System.out.println("File " + fileToZip.getName() + " archived as " + zipFile.getName());
    }
}
