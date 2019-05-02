package am.arssystems.image_manager_backend.twilio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Component
public class TwilioUtil {

    @Value("${twilio.account.sid}")
    public String ACCOUNT_SID;

    @Value("${twilio.authentication.token}")
    public String AUTH_TOKEN;
    @Value("${twilio.phone.number}")
    public String TWILIO_PHONE_NUMBER;

    public void sendSMS(String ToPhoneNumber,String text) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                Message message = Message.creator(new PhoneNumber(ToPhoneNumber),
                        new PhoneNumber(TWILIO_PHONE_NUMBER),
                        text).create();

                System.out.println(message.getSid());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }


}


