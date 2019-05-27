package am.arssystems.image_manager_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ImageManagerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageManagerBackendApplication.class, args);
    }

//    @Bean
//    public EmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
//
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        final TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
//        factory.addAdditionalTomcatConnectors(this.createConnection());
//        return factory;
//    }
//
//    private Connector createConnection() {
//        final String protocol = "org.apache.coyote.http11.Http11NioProtocol";
//        final Connector connector = new Connector(protocol);
//
//        connector.setScheme("http");
//        connector.setPort(9090);
//        connector.setRedirectPort(8443);
//        return connector;
//    }

}
