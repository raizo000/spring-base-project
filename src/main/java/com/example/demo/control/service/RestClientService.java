package com.example.demo.control.service;

import java.util.Iterator;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class RestClientService {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Do get
     * 
     * @param url
     * @param responseType
     * @return
     * @throws HttpClientErrorException
     */
    public <R> ResponseEntity<R> doGet(String url, Class<R> responseType)
            throws HttpClientErrorException {
        return doGet(url, null, null, null, responseType);
    }

    /**
     * Do get.
     *
     * @param <R> the generic type
     * @param url the url
     * @param headerParams the header param
     * @param responseType the response type
     * @return the object
     */
    public <R> ResponseEntity<R> doGet(String url, Map<String, String> headerParams,
            Class<R> responseType) throws HttpClientErrorException {
        return doGet(url, headerParams, null, null, responseType);
    }

    /**
     * Make post request to restful services.
     *
     * @param url the url
     * @param headerParams the header param
     * @param responseType the response type
     * @param uriVariables the uri params
     * @return the object
     */
    public <R> ResponseEntity<R> doGet(String url, Map<String, String> headerParams,
            Map<String, String> uriVariables, Class<R> responseType)
            throws HttpClientErrorException {
        return doGet(url, headerParams, uriVariables, null, responseType);
    }

    /**
     * Do a GET request
     * 
     * @param url
     * @param headerParams
     * @param uriVariables
     * @param queryParameters
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <R> ResponseEntity<R> doGet(String url, Map<String, String> headerParams,
            Map<String, String> uriVariables, MultiValueMap<String, String> queryParameters,
            Class<R> responseType) throws RestClientException {

        // Create http Headers with basic info: content-type, cache-control and SvcAuth.
        HttpHeaders headers = createHttpHeaders(headerParams);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String resolvedUrl = resolveRequestUri(url, uriVariables, queryParameters);

        // Exchange the request to get response
        return restTemplate.exchange(resolvedUrl, HttpMethod.GET, requestEntity, responseType);
    }

    /**
     * Do get
     * 
     * @param url
     * @param responseType as ParameterizedTypeReference
     * @return
     * @throws HttpClientErrorException
     */
    public <R> ResponseEntity<R> doGet(String url, ParameterizedTypeReference<R> responseType)
            throws HttpClientErrorException {
        return doGet(url, null, null, null, responseType, false, 0);
    }

    /**
     * Do get, check from cache first, if the cached data is still valid, then return the cached
     * data
     * 
     * @param url
     * @param responseType
     * @param cacheTimeoutInSeconds
     * @return
     * @throws HttpClientErrorException
     */
    public <R> ResponseEntity<R> doCachedGet(String url, ParameterizedTypeReference<R> responseType,
            int cacheTimeoutInSeconds) throws HttpClientErrorException {
        return doGet(url, null, null, null, responseType, true, cacheTimeoutInSeconds);
    }

    /**
     * 
     * @param url
     * @param headerParams
     * @param uriVariables
     * @param queryParameters
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <R> ResponseEntity<R> doGet(String url, Map<String, String> headerParams,
            Map<String, String> uriVariables, MultiValueMap<String, String> queryParameters,
            ParameterizedTypeReference<R> responseType, boolean enableCache, int timeoutInSeconds)
            throws RestClientException {

        // Create http Headers with basic info: content-type, cache-control and SvcAuth.
        HttpHeaders headers = createHttpHeaders(headerParams);

        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
        String resolvedUrl = resolveRequestUri(url, uriVariables, queryParameters);

        // Exchange the request to get response
        return restTemplate.exchange(resolvedUrl, HttpMethod.GET, requestEntity, responseType);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param responseType
     * @return
     * @throws HttpClientErrorException
     */
    public <T, R> ResponseEntity<R> doPost(String url, T requestBody, Class<R> responseType)
            throws HttpClientErrorException {
        return doPost(url, requestBody, null, null, null, responseType, false, 0);
    }


    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param responseType
     * @return
     * @throws HttpClientErrorException
     */
    public <T, R> ResponseEntity<R> doCachedPost(String url, T requestBody, Class<R> responseType,
            int timeoutInSeconds) throws HttpClientErrorException {
        return doPost(url, requestBody, null, null, null, responseType, true, timeoutInSeconds);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestEntity
     * @param responseType
     * @return
     * @throws HttpClientErrorException
     */
    public <T, R> ResponseEntity<R> doPost(String url, HttpEntity<?> requestEntity,
            Class<R> responseType) throws HttpClientErrorException {
        // Exchange the request to get response
        ResponseEntity<R> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
        return responseEntity;
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPost(String url, T requestBody,
            Map<String, String> headerParams, Class<R> responseType) throws RestClientException {
        return doPost(url, requestBody, headerParams, null, null, responseType, false, 0);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doCachedPost(String url, T requestBody,
            Map<String, String> headerParams, Class<R> responseType, int timeoutInSeconds)
            throws RestClientException {
        return doPost(url, requestBody, headerParams, null, null, responseType, true,
                timeoutInSeconds);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param queryParameters
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPost(String url, T requestBody,
            Map<String, String> headerParams, MultiValueMap<String, String> queryParameters,
            Class<R> responseType) throws RestClientException {
        return doPost(url, requestBody, headerParams, null, queryParameters, responseType, false,
                0);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param queryParameters
     * @param responseType
     * @param enableCache
     * @param timeoutInSeconds
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPost(String url, T requestBody,
            Map<String, String> headerParams, MultiValueMap<String, String> queryParameters,
            Class<R> responseType, boolean enableCache, int timeoutInSeconds)
            throws RestClientException {
        return doPost(url, requestBody, headerParams, null, queryParameters, responseType,
                enableCache, timeoutInSeconds);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param queryParameters
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doCachedPost(String url, T requestBody,
            Map<String, String> headerParams, MultiValueMap<String, String> queryParameters,
            Class<R> responseType, int timeoutInSeconds) throws RestClientException {
        return doPost(url, requestBody, headerParams, null, queryParameters, responseType, true,
                timeoutInSeconds);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param uriVariables
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPost(String url, T requestBody,
            Map<String, String> headerParams, Map<String, String> uriVariables,
            Class<R> responseType) throws RestClientException {
        return doPost(url, requestBody, headerParams, uriVariables, null, responseType, false, 0);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param uriVariables
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPost(String url, T requestBody,
            Map<String, String> headerParams, Map<String, String> uriVariables,
            Class<R> responseType, boolean enableCache, int timeoutInSeconds)
            throws RestClientException {
        return doPost(url, requestBody, headerParams, uriVariables, null, responseType, enableCache,
                timeoutInSeconds);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param uriVariables
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doCachedPost(String url, T requestBody,
            Map<String, String> headerParams, Map<String, String> uriVariables,
            Class<R> responseType, int timeoutInSeconds) throws RestClientException {
        return doPost(url, requestBody, headerParams, uriVariables, null, responseType, true,
                timeoutInSeconds);
    }

    /**
     * Do a post request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param uriVariables
     * @param queryParameters
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPost(String url, T requestBody,
            Map<String, String> headerParams, Map<String, String> uriVariables,
            MultiValueMap<String, String> queryParameters, Class<R> responseType,
            boolean enableCache, int timeoutInSeconds) throws RestClientException {

        // Create http Headers with basic info: content-type, cache-control and SvcAuth.
        HttpHeaders headers = createHttpHeaders(headerParams);

        HttpEntity<T> requestEntity = new HttpEntity<T>(requestBody, headers);
        String resolvedUrl = resolveRequestUri(url, uriVariables, queryParameters);

        // Exchange the request to get response
        ResponseEntity<R> responseEntity =
                restTemplate.exchange(resolvedUrl, HttpMethod.POST, requestEntity, responseType);
        return responseEntity;
    }

    /**
     * Do a PUT request
     * 
     * @param url
     * @param requestBody
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPut(String url, T requestBody, Class<R> responseType)
            throws RestClientException {
        return doPut(url, requestBody, null, null, null, responseType);
    }

    /**
     * Do a PUT request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPut(String url, T requestBody,
            Map<String, String> headerParams, Class<R> responseType) throws RestClientException {
        return doPut(url, requestBody, headerParams, null, null, responseType);
    }

    /**
     * Do a PUT request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param queryParameters
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPut(String url, T requestBody,
            Map<String, String> headerParams, MultiValueMap<String, String> queryParameters,
            Class<R> responseType) throws RestClientException {
        return doPut(url, requestBody, headerParams, null, queryParameters, responseType);
    }

    /**
     * Do a PUT request
     * 
     * @param url
     * @param requestBody
     * @param headerParams
     * @param uriVariables
     * @param responseType
     * @return
     * @throws RestClientException
     */
    public <T, R> ResponseEntity<R> doPut(String url, T requestBody,
            Map<String, String> headerParams, Map<String, String> uriVariables,
            Class<R> responseType) throws RestClientException {
        return doPut(url, requestBody, headerParams, uriVariables, null, responseType);
    }

    /**
     * Make post request to restful services.
     *
     * @param url the url
     * @param headerParams the header param
     * @param responseType the response type
     * @param queryParameters the uri params
     * @return the object
     */
    public <T, R> ResponseEntity<R> doPut(String url, T requestBody,
            Map<String, String> headerParams, Map<String, String> uriVariables,
            MultiValueMap<String, String> queryParameters, Class<R> responseType)
            throws HttpClientErrorException {

        // Create http Headers with basic info: content-type, cache-control and SvcAuth.
        HttpHeaders headers = createHttpHeaders(headerParams);

        HttpEntity<T> requestEntity = new HttpEntity<T>(requestBody, headers);
        String resolvedUrl = resolveRequestUri(url, uriVariables, queryParameters);

        // Exchange the request to get response
        return restTemplate.exchange(resolvedUrl, HttpMethod.PUT, requestEntity, responseType);
    }

    /**
     * Include the additional header to request headers
     * 
     * @param headerParams
     * @return
     */
    public HttpHeaders createHttpHeaders(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();

        // Add more header parameters, if any.
        if (headerParams != null) {
            Iterator<String> keyIterator = headerParams.keySet().iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String value = headerParams.get(key);

                // Make sure no duplicate value for header key
                if (headers.containsKey(key)) {
                    headers.remove(key);
                }
                headers.add(key, value);
            }
        }
        return headers;
    }

    /**
     * Build new URI with variable & query parameters
     * 
     * @param url
     * @param uriVariables
     * @param queryParameters
     * @return
     */
    private String resolveRequestUri(String url, Map<String, String> uriVariables,
            MultiValueMap<String, String> queryParameters) {

        String resolvedUrl = null;

        // Query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        UriComponents uriComponents = null;

        // Add query parameter
        if (queryParameters != null && queryParameters.size() > 0) {
            builder = builder.queryParams(queryParameters);
            uriComponents = builder.build();
        }

        // Resolve uri variables
        if (uriVariables != null && uriVariables.size() > 0) {
            uriComponents = builder.buildAndExpand(uriVariables);
        }

        if (uriComponents != null) {
            resolvedUrl = uriComponents.toUriString();
        }

        // Try to make sure the resolved has value
        resolvedUrl = resolvedUrl != null ? resolvedUrl : url;
        return resolvedUrl;
    }
}
