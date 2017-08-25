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
        ApplicationContext context = SpringApplication.run(NodeApplication.class, args);
        try {
            Object obj =  context.getBean("ignite");
            System.out.println("ignite name = " + obj.getClass().getCanonicalName());
        } catch (Exception e){
            System.out.println("Exception occurred");
            e.printStackTrace();
        }
    }

    @Bean
    public Ignite ignite(IgniteConfiguration configuration){
        return Ignition.getOrStart(configuration);
        //SpringTransactionManager сам стартует ноду
    }


}
