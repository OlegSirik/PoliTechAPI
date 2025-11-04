package ru.pt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.pt.numbers.configuration.NumbersModuleConfiguration;

@SpringBootApplication
// spring по идее подтянет, но, лучше явно указать
@Import(NumbersModuleConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


  