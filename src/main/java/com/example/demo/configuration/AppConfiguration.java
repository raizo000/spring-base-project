package com.example.demo.configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
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
        RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIMEOUT)
            .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();

    return HttpClients.custom().setDefaultRequestConfig(requestConfig)
        .setConnectionManager(poolingConnectionManager())
        .setKeepAliveStrategy(connectionKeepAliveStrategy())
        .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
  }

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
    poolingConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE_PER_HOST);
    return poolingConnectionManager;
  }

  @Bean
  public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
    return new ConnectionKeepAliveStrategy() {
      @Override
      public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        HeaderElementIterator it =
            new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
          HeaderElement he = it.nextElement();
          String param = he.getName();
          String value = he.getValue();
          if (value != null && param.equalsIgnoreCase("timeout")) {
            return Long.parseLong(value) * 1000;
          }
        }
        return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
      }
    };
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
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS,
                TimeUnit.SECONDS);
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
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
    restTemplate.setInterceptors(Collections.singletonList(new ExternalIRequestntercepter()));
    return restTemplate;
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
