package com.tcg.admin.controller.core;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

/**
 * Title: com.yx.cw.controller.core.BehaviorRequestWrapper.java<br>
 * Description: Manually retrieve request Inputstream to log it ! replace by sammy
 * 
 * @author: Kim
 * @version: 1.0
 */
public class BehaviorRequestWrapper extends HttpServletRequestWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(BehaviorRequestWrapper.class);
	
    private final byte[] body;
    private boolean fileUpload = false;
    
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    public BehaviorRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        String contentType = request.getContentType();
        // restful webservice contains json media type , retrieve body content
        if (contentType!= null && (contentType.contains(APPLICATION_JSON) || contentType.contains(APPLICATION_FORM_URL_ENCODED))) {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                body = IOUtils.toByteArray(inputStream);
                return;
            }
        } else if (contentType!= null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
        	this.fileUpload = true;
        }
        body = null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (body == null && !fileUpload) {
            return null;
        } else if (fileUpload) {
        	return super.getInputStream();
        }

        final ByteArrayInputStream stream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return stream.read();
            }

			@Override
			public boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				// TODO Auto-generated method stub
				
			}
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (body == null)
            return super.getReader();

        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body), StandardCharsets.UTF_8));
    }

    public String getBody() {

        StringBuilder sb = new StringBuilder();
        if (body != null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                LOGGER.error("getBody line error", e);
            }
        }
        return sb.toString();
    }
}
