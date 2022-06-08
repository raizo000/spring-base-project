package com.example.demo.configuration;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.demo.dto.RequestWrapper;
import com.example.demo.dto.ResponseWrapper;
import com.example.demo.utils.HttpClientUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalRequestInterceptor implements Filter {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    String requestId = UUID.randomUUID().toString();
    long startTime = System.currentTimeMillis();

    RequestWrapper requestWrapper = new RequestWrapper(requestId, (HttpServletRequest) request);
    ResponseWrapper responseWrapper = new ResponseWrapper(requestId, (HttpServletResponse) response);
    try {
      chain.doFilter(requestWrapper, responseWrapper);
    } catch (Exception e) {
      log.error("InternalRequestInterceptor", e);
    }
    logRequestInfo(requestWrapper);
    logResponseInfo(responseWrapper, System.currentTimeMillis() - startTime);
  }

  /**
   * Log the request info
   *
   * @param request
   */
  private void logRequestInfo(final HttpServletRequest request) {
    StringBuilder msg = new StringBuilder();

    msg.append("\n====== Request Begin========\n");
    msg.append("IP         : ").append(request.getRemoteAddr()).append("\n");

    if (request instanceof RequestWrapper) {
      msg.append("RequestID  : ").append(((RequestWrapper) request).getId()).append("\n");
    }
    HttpSession session = request.getSession(false);
    if (session != null) {
      msg.append("SessionId  : ").append(session.getId()).append("\n");
    }
    if (request.getMethod() != null) {
      msg.append("Method     : ").append(request.getMethod()).append("\n");
    }
    if (request.getRequestURI() != null) {
      msg.append("URI        : ").append(request.getRequestURI()).append("\n");
    }
    if (request.getQueryString() != null) {
      msg.append("Query      : ").append(request.getQueryString()).append("\n");
    }
    String headersStr = HttpClientUtils.getHeadersAsStringFromRequest(request);

    if (!StringUtils.isBlank(headersStr)) {
      msg.append("Headers    : ").append(headersStr).append("\n");
    }
    if (request instanceof RequestWrapper && !isMultipart(request)
        && !isBinaryContent(request)) {
      String requestBody = getRequestBody(request);
      if (StringUtils.isNotBlank(requestBody)) {
        msg.append("Body       : ").append(requestBody).append("\n");
      }
    }
    msg.append("====== Request End==========");
    log.info(msg.toString());
  }

  /**
   * Do log the response info
   *
   * @param request
   * @return
   */
  private boolean isBinaryContent(final HttpServletRequest request) {
    if (request.getContentType() == null) {
      return false;
    }
    return request.getContentType().startsWith("image") || request.getContentType().startsWith("video")
        || request.getContentType().startsWith("audio");
  }

  /**
   * check the content type is multipart
   *
   * @param request
   * @return
   */
  private boolean isMultipart(final HttpServletRequest request) {
    return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
  }

  /**
   * Do log the response info
   *
   * @param response
   */
  private void logResponseInfo(final ResponseWrapper response, long processTimeInMillies) {
    StringBuilder msg = new StringBuilder();

    msg.append("\n====== Response Begin========\n");
    msg.append("RequestID  : ").append((response.getId())).append("\n");
    msg.append("Status     : ").append(response.getStatus()).append("\n");
    msg.append("ProcessedTime     : ").append(processTimeInMillies).append("\n");

    String headersStr = getHeadersAsStringFromResponse(response);
    if (!StringUtils.isBlank(headersStr)) {
      msg.append("Headers    :").append(headersStr).append("\n");
    }
    String responseBody = getResponseBody(response);
    if (StringUtils.isNotBlank(responseBody)) {
      msg.append("Body       : ").append(responseBody).append("\n");
    }
    msg.append("====== Response End==========\n");
    log.info(msg.toString());
  }

  /**
   * Get the request body from ServletRequest
   *
   * @param request
   * @return
   */
  private String getRequestBody(HttpServletRequest request) {
    String body = null;
    RequestWrapper requestWrapper = (RequestWrapper) request;
    try {
      String charEncoding = requestWrapper.getCharacterEncoding() != null ? requestWrapper.getCharacterEncoding()
          : "UTF-8";
      body = new String(requestWrapper.toByteArray(), charEncoding);
    } catch (UnsupportedEncodingException e) {
      log.warn("Failed to parse request payload", e);
    }
    return body;
  }

  /**
   * Get the response body from ServletResponse
   *
   * @param response
   * @return
   */
  private String getResponseBody(final ResponseWrapper response) {
    String body = null;
    try {
      body = new String(new String(response.toByteArray(), response.getCharacterEncoding()));
    } catch (UnsupportedEncodingException e) {
      log.warn("Failed to parse response payload", e);
    }
    return body;
  }

  /**
   * Get the response headers as string
   *
   * @param response
   * @return
   */
  private String getHeadersAsStringFromResponse(ResponseWrapper response) {
    StringBuilder headersStr = new StringBuilder();
    Collection<String> headerNames = response.getHeaderNames();
    if (headerNames != null) {
      headersStr.append("[");
      for (Iterator<String> i = headerNames.iterator(); i.hasNext();) {
        String headerName = i.next();
        headersStr.append(headerName).append(": ");
        headersStr.append(response.getHeader(headerName));
        if (i.hasNext()) {
          headersStr.append(", ");
        }
      }
      headersStr.append("]");
    }
    return headersStr.toString();
  }
}
