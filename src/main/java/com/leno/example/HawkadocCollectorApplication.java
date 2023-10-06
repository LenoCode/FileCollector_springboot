package com.leno.example;

import com.leno.example.modules.collector.services.collectorService.CollectorServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class,    DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class   })
@EnableConfigurationProperties
public class HawkadocCollectorApplication {

	@Autowired
	CollectorServiceInterface collectorService;


	public static void main(String[] args) {
		SpringApplication.run(HawkadocCollectorApplication.class, args);
	}


	@EventListener(ApplicationReadyEvent.class)
	public void afterRunInitialization() throws InterruptedException {
		collectorService.runContinuesHandlerProcess();
	}



}
