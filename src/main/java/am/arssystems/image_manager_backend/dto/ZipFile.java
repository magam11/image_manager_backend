package am.arssystems.image_manager_backend.dto;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile {

    public static void main(String[] args) throws IOException {
//        List<String> srcFiles = Arrays.asList("C:\\Users\\Maga\\Desktop\\149150580.jpg", "C:\\Users\\Maga\\Desktop\\149150580 - Copy.jpg");
//        FileOutputStream fos = new FileOutputStream("C:\\Users\\Maga\\Desktop\\multiCompressed.zip");
//        ZipOutputStream zipOut = new ZipOutputStream(fos);
//        for (String srcFile : srcFiles) {
//            File fileToZip = new File(srcFile);
//            FileInputStream fis = new FileInputStream(fileToZip);
//            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
//            zipOut.putNextEntry(zipEntry);
//
//            byte[] bytes = new byte[1024];
//            int length;
//            while((length = fis.read(bytes)) >= 0) {
//                zipOut.write(bytes, 0, length);
//            }
//            fis.close();
//        }
//        zipOut.close();
//        fos.close();
    }
}