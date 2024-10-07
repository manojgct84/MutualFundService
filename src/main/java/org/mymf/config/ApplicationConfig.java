package org.mymf.config;


import org.mymf.service.finsire.MutualFundFinSireService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig
{
    /**
     * Create a singleton instance of OkHttpClient.
     * This will ensure that only one instance of OkHttpClient is used throughout the application.
     *
     * @return OkHttpClient instance.
     */
/*    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .retryOnConnectionFailure(true) // Enable retry on failure
            .build();
    }*/
    @Bean
    public RestTemplate restTemplate ()
    {
        return new RestTemplate();
    }

}
