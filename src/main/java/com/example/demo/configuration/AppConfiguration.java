package com.example.demo.configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  private static final int MAX_TOTAL_CONNECTIONS = 50;

  @Bean
  public PoolingHttpClientConnectionManager poolingConnectionManager() {
    SSLContextBuilder builder = new SSLContextBuilder();
    try {
      builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    } catch (NoSuchAlgorithmException | KeyStoreException e) {
      LOGGER.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(),
          e);
    }

    SSLConnectionSocketFactory sslsf = null;
    try {
      sslsf = new SSLConnectionSocketFactory(builder.build());
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      LOGGER.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(),
          e);
    }

    Registry<ConnectionSocketFactory> socketFactoryRegistry =
        RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf)
            .register("http", new PlainConnectionSocketFactory()).build();

    PoolingHttpClientConnectionManager poolingConnectionManager =
        new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
    return poolingConnectionManager;
  }

  @Bean
  public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
    return new ConnectionKeepAliveStrategy() {
      @Override
      public TimeValue getKeepAliveDuration(org.apache.hc.core5.http.HttpResponse response,
          HttpContext context) {
        BasicHeaderElementIterator it =
            new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
          HeaderElement he = it.next();
          String param = he.getName();
          String value = he.getValue();

          if (value != null && param.equalsIgnoreCase("timeout")) {
            return TimeValue.ofSeconds(Long.valueOf(value));
          }
        }
        return TimeValue.ofSeconds(20);
      }
    };
  }

  @Bean
  public CloseableHttpClient httpClient() {

    RequestConfig requestConfig =
        RequestConfig.custom().setConnectionRequestTimeout(Timeout.ofSeconds(60))
            .setConnectTimeout(Timeout.ofSeconds(60)).build();

    return HttpClients.custom().setDefaultRequestConfig(requestConfig)
        .setConnectionManager(poolingConnectionManager())
        .setKeepAliveStrategy(connectionKeepAliveStrategy()).build();
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
            connectionManager.close();;
            connectionManager.closeIdle(TimeValue.ofSeconds(30));
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

  @Bean
  public RestTemplate restTemplate() {
    ClientHttpRequestFactory factory =
        new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.setInterceptors(Collections.singletonList(new ExternalIRequestntercepter()));
    return restTemplate;
  }

  @Bean
  public FilterRegistrationBean<InternalRequestInterceptor> loggingFilter() {
    FilterRegistrationBean<InternalRequestInterceptor> registrationBean =
        new FilterRegistrationBean<>();
    registrationBean.setFilter(new InternalRequestInterceptor());
    registrationBean.addUrlPatterns("/test");
    registrationBean.setOrder(2);
    return registrationBean;
  }
}
