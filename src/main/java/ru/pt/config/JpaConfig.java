package ru.pt.config;



import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {
    @Bean
    public static BeanFactoryPostProcessor entityManagerFactoryInterfaceFix() {
        return beanFactory -> {
            if (beanFactory.containsBeanDefinition("entityManagerFactory")) {
                BeanDefinition bd = beanFactory.getBeanDefinition("entityManagerFactory");
                bd.getPropertyValues().add("entityManagerFactoryInterface", EntityManagerFactory.class);
            }
        };
    }
} 
