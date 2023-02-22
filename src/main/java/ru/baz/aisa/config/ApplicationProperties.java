package ru.baz.aisa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties(prefix = "wash-my-car")
@Setter
@Getter
@Component
public class ApplicationProperties {
    private String token;
    private int slotPerDay;
    private String dateFormat;
    private String dateFormatSlot;
    private Duration slotDuration = Duration.ofMinutes(15);
}
