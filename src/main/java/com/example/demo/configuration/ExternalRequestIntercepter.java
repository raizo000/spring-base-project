package com.example.demo.configuration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

public class ExternalRequestIntercepter implements ClientHttpRequestInterceptor {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    String requestId = UUID.randomUUID().toString();
    long startTime = System.currentTimeMillis();
    logRequestInfo(requestId, request, body);
    ClientHttpResponse response = execution.execute(request, body);
    if (response != null) {
      logResponseInfo(requestId, response, System.currentTimeMillis() - startTime);
    }
    return response;
  }

  /**
   * Log the request info
   *
   * @param request
   * @return
   */
  private void logRequestInfo(String requestId, final HttpRequest request, final byte[] body) {
    StringBuilder msg = new StringBuilder();

    msg.append("\n====== Request Begin========\n");

    msg.append("RequestID : ").append(requestId).append("\n");

    if (request.getMethod() != null) {
      msg.append("Method : ").append(request.getMethod()).append("\n");
    }

    // Assign empty string for uri to avoid null pointer error when checking
    // contains for path in the code below.
    String uri = "";
    if (request.getURI() != null) {
      uri = request.getURI().toString();
      msg.append("URI : ").append(uri).append("\n");
    }

    HttpHeaders headers = request.getHeaders();
    if (headers != null) {
      msg.append("Headers : ").append(headers.toSingleValueMap()).append("\n");
    }

    if (uri != null && (uri.contains("upload")) || uri.contains("image")
        || uri.contains("location/list")) {
      // Ignore these request body
    } else {
      if (body.length != 0) {
        try {
          msg.append("Body : ").append(new String(body, "UTF-8")).append("\n");
        } catch (UnsupportedEncodingException e) {
          log.error("Has UnsupportedEncodingException while handle logRequestInfo.", e);
        }
      }
    }
    msg.append("====== Request End==========");
    log.info(msg.toString());
  }

  /**
   * Do log the response info
   *
   * @param response
   * @return
   * @throws IOException
   */

  private void logResponseInfo(String requestId, ClientHttpResponse response,
      long processTimeInMillies) throws IOException {
    StringBuilder msg = new StringBuilder();
    try {
      msg.append("\n====== Response Begin========\n");
      msg.append("RequestID : ").append((requestId)).append("\n");
      msg.append("Status : ").append(response.getRawStatusCode()).append("\n");
      msg.append("ProcessedTime : ").append((processTimeInMillies)).append("\n");
      HttpHeaders headers = response.getHeaders();
      if (headers != null) {
        msg.append("Headers :").append(headers.toSingleValueMap()).append("\n");
      }
      response.getBody().reset();
      msg.append("Body : ")
          .append(StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()))
          .append("\n");
    } catch (Exception ex) {
      log.error("logResponseInfo error", ex);
    }
    msg.append("====== Response End==========\n");
    log.info(msg.toString());
  }

}
