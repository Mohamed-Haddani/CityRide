package com.cityride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan   // active les classes @ConfigurationProperties (JwtProperties, etc.)
public class CityRideApplication {

    public static void main(String[] args) {
        SpringApplication.run(CityRideApplication.class, args);
    }
}
