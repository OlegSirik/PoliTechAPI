package ru.pt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.pt.auth.configuration.AuthModuleConfiguration;
import ru.pt.calculator.configuration.CalculatorModuleConfiguration;
import ru.pt.db.configuration.DbModuleConfiguration;
import ru.pt.files.configuration.FilesModuleConfiguration;
import ru.pt.numbers.configuration.NumbersModuleConfiguration;
import ru.pt.process.configuration.ProcessModuleConfiguration;

@SpringBootApplication
// spring по идее подтянет, но, лучше явно указать
@Import({NumbersModuleConfiguration.class,
        ProcessModuleConfiguration.class,
        DbModuleConfiguration.class,
        CalculatorModuleConfiguration.class,
        AuthModuleConfiguration.class,
        FilesModuleConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


  