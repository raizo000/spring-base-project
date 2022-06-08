package com.example.demo.utils;

import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class HttpClientUtils {

  public static String getHeadersAsStringFromRequest(HttpServletRequest request) {
    StringBuilder headersStr = new StringBuilder();
    Enumeration<String> headerNames = request.getHeaderNames();
    if (headerNames != null) {
      headersStr.append("[");
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        headersStr.append(headerName).append(": ");
        headersStr.append(request.getHeader(headerName));
        if (headerNames.hasMoreElements()) {
          headersStr.append(", ");
        }
      }
      headersStr.append("]");
    }
    return headersStr.toString();
  }

  public static MultiValueMap<String, String> buildSearchParams(String search, Pageable pageable) throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("search", URLEncoder.encode(search, "UTF-8"));
    for (Sort.Order order : pageable.getSort()) {
      queryParams.add("sort", order.getProperty() + "," + order.getDirection());
    }
    queryParams.add("page", String.valueOf(pageable.getPageNumber()));
    queryParams.add("size", String.valueOf(pageable.getPageSize()));

    return queryParams;
  }
}
