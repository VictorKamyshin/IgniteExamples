package com.example;

import com.example.mocks.CacheService;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value="com.example.configuration")
public class IgniteConfigurationTest {
    @Bean
    public Ignite ignite(IgniteConfiguration configuration){
        return Ignition.getOrStart(configuration);
    }

    @Bean
    public CacheService cacheServiceMock(){
        return new CacheService();
    }
}
