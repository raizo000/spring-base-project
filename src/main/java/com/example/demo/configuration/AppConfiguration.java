package com.example.demo.configuration;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

  private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  private static final int CONNECT_TIMEOUT = 30 * 1000;
  private static final int REQUEST_TIMEOUT = 30 * 1000;
  private static final int SOCKET_TIMEOUT = 60 * 1000;
  private static final int MAX_TOTAL_CONNECTIONS = 100;
  private static final int MAX_ROUTE_PER_HOST = 50;
  private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
  private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

  @Bean
  public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
        new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setHttpClient(httpClient());
    return clientHttpRequestFactory;
  }

  @Bean
  public CloseableHttpClient httpClient() {
    RequestConfig requestConfig =
        RequestConfig.custom().setConnectionRequestTimeout(Timeout.ofMilliseconds(REQUEST_TIMEOUT))
            .setResponseTimeout(Timeout.ofMilliseconds(SOCKET_TIMEOUT)).build();

    return HttpClients.custom().setDefaultRequestConfig(requestConfig)
        .setConnectionManager(poolingConnectionManager()).build();
  }

  @Bean
  public PoolingHttpClientConnectionManager poolingConnectionManager() {
    return PoolingHttpClientConnectionManagerBuilder.create()
        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
            .setSslContext(SSLContexts.createSystemDefault()).setTlsVersions(TLS.V_1_3).build())
        .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofMinutes(1)).build())
        .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
        .setConnPoolPolicy(PoolReusePolicy.LIFO).setMaxConnTotal(MAX_TOTAL_CONNECTIONS)
        .setDefaultConnectionConfig(ConnectionConfig.custom().setSocketTimeout(Timeout.ofMinutes(1))
            .setConnectTimeout(Timeout.ofMinutes(1)).setTimeToLive(TimeValue.ofMinutes(10)).build())
        .build();
  }

  @Bean
  public Runnable idleConnectionMonitor(
      final PoolingHttpClientConnectionManager connectionManager) {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 10000)
      public void run() {
        try {
          if (connectionManager != null) {
            LOGGER.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
            connectionManager.closeExpired();;
            connectionManager.closeIdle(TimeValue.ofSeconds(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS));
          } else {
            LOGGER.trace(
                "run IdleConnectionMonitor - Http Client Connection manager is not initialised");
          }
        } catch (Exception e) {
          LOGGER.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}",
              e.getMessage(), e);
        }
      }
    };
  }

  // @Bean
  // public RestTemplate restTemplate() {
  // RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
  // restTemplate.setInterceptors(Collections.singletonList(new ExternalRequestIntercepter()));
  // return restTemplate;
  // }

  @Bean
  public RestTemplate restTemplate() {
    ClientHttpRequestFactory factory =
        new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.setInterceptors(Collections.singletonList(new ExternalRequestIntercepter()));
    return restTemplate;
  }


  @Bean
  public RestClient restClient() {

    return RestClient.builder().requestInterceptor(new ExternalRequestIntercepter())
        .requestFactory(clientHttpRequestFactory()).build();
  }

  @Bean
  public FilterRegistrationBean<InternalRequestInterceptor> loggingFilter() {
    FilterRegistrationBean<InternalRequestInterceptor> registrationBean =
        new FilterRegistrationBean<>();
    registrationBean.setFilter(new InternalRequestInterceptor());
    registrationBean.addUrlPatterns("/**");
    registrationBean.setOrder(2);
    return registrationBean;
  }
}
