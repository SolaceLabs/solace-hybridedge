package com.solace.hybrid_edge_starter;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.solace.camel.component.jms.SolaceJmsFactoryProperties;

@SpringBootApplication
@EnableConfigurationProperties(SolaceJmsFactoryProperties.class)
public class Application {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(Application.class, args);
	}
	
}

