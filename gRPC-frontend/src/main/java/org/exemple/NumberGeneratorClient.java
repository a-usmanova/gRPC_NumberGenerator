package org.exemple;

import org.exemple.service.NumberGeneratorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class NumberGeneratorClient {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(NumberGeneratorClient.class, args);
        NumberGeneratorService numberGeneratorService = context.getBean(NumberGeneratorService.class);

        numberGeneratorService.getNumbers();
    }

}
