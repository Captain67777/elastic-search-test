package com.lilium.elasticsearch.configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration // indicating to Spring that beans are defined here
@EnableElasticsearchRepositories(basePackages = "com.lilium.elasticsearch.repository") // will contain beans
@ComponentScan(basePackages = {"com.lilium.elasticsearch"}) // instructing Spring to look for beans in mentioned package, eg for ElasticsearchRepository
public class Config extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.url}")
    public String elasticsearchUrl;

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl)
//                .usingSsl("3f232a128ad3d28e19234f9c336b1973f8de1bbcf28050edd403758dd6b3f090") //add the generated sha-256 fingerprint
//                .withBasicAuth("elastic", "V1*bYNw+gDC6bLhuE2wr") //add your username and password
                .build();

        return RestClients.create(config).rest();
    }

}