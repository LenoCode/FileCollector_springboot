package com.leno.example.config.httpClientConfig;


import com.leno.example.restClients.executionManager.BaseExecutionManagerRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientRestConfiguration {

    @Value("${hawkadoc.services.executionmanager.host}")
    private String executionHost;
    @Value("${hawkadoc.services.executionmanager.port}")
    private Integer executionPort;


    @Bean
    public BaseExecutionManagerRestClient baseSchedulerRestClient(){
        String url = setUrlPathForAuth(executionHost, executionPort);
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                //.defaultStatusHandler(HttpStatusCode::isError,clientResponse -> Mono.just())
                //.defaultHeader("Authorization","test:test")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient)).build();
        return factory.createClient(BaseExecutionManagerRestClient.class);
    }


    private String setUrlPathForAuth(String host, Integer port){
        StringBuilder url = new StringBuilder();
        url.append("http://").append(host).append(":").append(port);
        return url.toString();
    }
}
