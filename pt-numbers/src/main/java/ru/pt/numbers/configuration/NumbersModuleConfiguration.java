package ru.pt.numbers.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.numbers.repository.NumberGeneratorRepository;
import ru.pt.numbers.service.DatabaseNumberGeneratorService;
import ru.pt.numbers.utils.NumberGeneratorMapper;

@Configuration
@ComponentScan("ru.pt.numbers")
public class NumbersModuleConfiguration {

}
