package de.hskl.ki;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.hskl.ki.models.proxy.PrefixedFormData;
import de.hskl.ki.models.proxy.ProxyFormRequest;
import de.hskl.ki.services.jackson.ProxyFormRequestDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KiApplication.class, args);
    }

}
