package com.tcg.admin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.admin.common.constants.SystemConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by James on 2016/10/20.
 */
public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private static final int TIMEOUT = 15000;// 15s

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
            .setSocketTimeout(TIMEOUT).build();

    private static final HttpClientBuilder HTTPCLIENT_BUILDER = HttpClientBuilder.create().setDefaultRequestConfig(REQUEST_CONFIG);

    private HttpUtils() {
    	throw new IllegalStateException("Utility class");
    }
    
    public static StringBuilder post(String domain, String resource, Map<String, String> parameter) {
        return getPost(domain, resource, parameter, SystemConstant.TYPE_POST);
    }

    public static StringBuilder post(String url, Map<String, String> parameter) {
        return getPost(null, url, parameter, SystemConstant.TYPE_POST);
    }

    public static StringBuilder postJson(String url, Map<String, String> parameter) {
        return getPost(null, url, parameter, SystemConstant.TYPE_JSON);
    }


    /**
     *
     * @param domain
     * @param resource
     * @param parameter
     * @param type
     * @return
     */
    public static StringBuilder getPost(String domain, String resource, Map<String, String> parameter, String type) {
        HttpPost post = new HttpPost(resource);
        HttpHost host = null;
        if(StringTools.isNotEmpty(domain)) {
            host = new HttpHost(domain);
            LOGGER.debug("Http post url----->{}{}", host, post.getURI());
        }else{
            LOGGER.debug("Http post url----->{}", post.getURI());
        }


        CloseableHttpClient httpClient = HTTPCLIENT_BUILDER.build();
        CloseableHttpResponse response = null;
        StringBuilder resultString = new StringBuilder();
        try {
        	if("JSON".equals(type)) {
        		post.setHeader(HTTP.CONTENT_TYPE, SystemConstant.JSON_UTF_8);
        		String body = new ObjectMapper().writeValueAsString(parameter);
                post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
                
                LOGGER.debug("entity {}", body);
                response = httpClient.execute(post);
        	} else {
        		List<NameValuePair> nameValuePairs = new LinkedList<>();
                for (Map.Entry<String, String> entry : parameter.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                String query = URLEncodedUtils.format(nameValuePairs, StandardCharsets.UTF_8);
                post.setHeader(HTTP.CONTENT_TYPE, SystemConstant.X_WWW_FORM_UTF_8);
                post.setEntity(new StringEntity(query));
                LOGGER.debug("entity {}", query);

                if(StringTools.isNotEmpty(domain)) {
                    response = httpClient.execute(host, post);
                }else{
                    response = httpClient.execute(post);
                }
        	}
            resultString = inputStreamToString(response,domain);
        } catch (Exception e) {
            LOGGER.error("Send HTTP request error!", e);
        } finally {
            try {
                if(response!=null) {
                    response.close();
                }
                if(httpClient!=null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                LOGGER.error("httpClient close error!", e);
            }
        }
        return resultString;
    }

    /**
     * get method
     * @param url
     * @return
     */
    public static String get(String url) {
        HttpGet get = new HttpGet(url);
        return getStringBuilder(url,get);
    }

    public static String doGet(String url, NameValuePair... nvps) {
        URI builder;
        try {
            builder = new URIBuilder(url).addParameters(Arrays.asList(nvps)).build();
        } catch (URISyntaxException e) {
            throw new AdminServiceBaseException(AdminErrorCode.GENERIC_SYSTEM_ERROR, "Connect failed: " + url, e);
        }
        HttpGet httpget = new HttpGet(builder);
        return getStringBuilder(url,httpget);
    }

    /**
     * including headers information
     * @param url
     * @param headers
     * @return
     */
    public static String get(String url, Map<String, String> headers) {
        HttpGet get = new HttpGet(url);
        return getStringBuilder(url,getHeader(get,headers));
    }

    /**
     * get Header information For Get method
     * @param get
     * @param headers
     * @return
     */
    private static HttpGet getHeader(HttpGet get, Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                get.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return get;
    }


    /**
     * get StringBuilder
     * @param url
     * @param get
     * @return
     */
    private static String getStringBuilder(String url,HttpGet get){
        LOGGER.debug("Http GET url----->{}", url);
        String resultString;
        CloseableHttpClient httpClient = HTTPCLIENT_BUILDER.build();
        CloseableHttpResponse response = null;
        
        try {
            response = httpClient.execute(get);
            resultString = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == 500) {
                LOGGER.error("Error 500 Return, Message: {}", resultString);
                Map<String, Object> returnMessage = new ObjectMapper().readValue(resultString, Map.class);
                throw new AdminServiceBaseException(returnMessage.get("errorCode").toString());
            }

        } catch (Exception e) {
            LOGGER.error("Send HTTP GET request error!", e);
            throw new AdminServiceBaseException(AdminErrorCode.GENERIC_SYSTEM_ERROR, e.getMessage());
        } finally {
            try {
                if(response!=null) {
                    response.close();
                }
                httpClient.close();
            } catch (Exception e) {
                LOGGER.error("httpClient close error!", e);
            }
        }
        return resultString;
    }

    /**
     * get Content from inputStream to String
     * @param response
     * @param logInfo
     * @return
     * @throws IOException
     */
    private static StringBuilder inputStreamToString(CloseableHttpResponse response, String logInfo) throws IOException {
        StringBuilder resultString = new StringBuilder();

        if (response.getStatusLine().getStatusCode() == 500) {
            throw new AdminServiceBaseException(AdminErrorCode.GENERIC_SYSTEM_ERROR);
        }

        InputStream inputStream = response.getEntity().getContent();
        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line;
        while ((line = rd.readLine()) != null) {
            resultString.append(line);
        }
        LOGGER.debug("\nURL: {} \nResult: {}", logInfo, resultString);
        return resultString;
    }

    public static NameValuePair[] getNameValuePairs(Map<String, String> params) {
        NameValuePair[] nvpArray = new NameValuePair[params.size()];
        int idx = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nvpArray[idx++] = new BasicNameValuePair(entry.getKey(), entry.getValue());
        }
        return nvpArray;
    }
}
