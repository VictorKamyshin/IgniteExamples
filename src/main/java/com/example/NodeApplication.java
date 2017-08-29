package com.example;

import com.example.aspects.TransactionAspect;
import com.sun.glass.ui.Application;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.transactions.spring.SpringTransactionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(value={"com.example.configuration"})
public class NodeApplication {
    public static void main(String[] args){
        SpringApplication.run(NodeApplication.class, args);
    }

    @Bean
    public Ignite ignite(IgniteConfiguration configuration){
        //SpringTransactionManager сам стартует ноду
        return Ignition.getOrStart(configuration);
    }

}
