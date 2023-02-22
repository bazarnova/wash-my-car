package ru.baz.aisa.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModules(
                new JavaTimeModule(),
                new ProblemModule(),
                new ConstraintViolationProblemModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(applicationProperties.getDateFormat());
    }

    @Bean
    public DateTimeFormatter slotDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(applicationProperties.getDateFormatSlot());
    }

}
