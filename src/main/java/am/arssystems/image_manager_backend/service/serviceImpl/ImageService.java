package am.arssystems.image_manager_backend.service.serviceImpl;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {
    public double getImageFileSize(File image) throws IOException {
        InputStream inputStream = new FileInputStream(image);
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        double picSize = 0;
        int redlen = 0;
        while ((redlen = inputStream.read(buffer, 0, bufferSize)) > -1) {
            picSize += redlen;
        }
        if(inputStream!=null){
            inputStream.close();
        }
        return picSize;

    }
}
