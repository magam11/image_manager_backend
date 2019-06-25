package am.arssystems.image_manager_backend;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class psvm {
    public static void main(String[] args) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        System.out.println(timestamp.getDay());
    }
}
