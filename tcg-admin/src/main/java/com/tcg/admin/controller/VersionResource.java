package com.tcg.admin.controller;


import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/version", produces = MediaType.APPLICATION_JSON_VALUE)
public class VersionResource {

	private JsonNode versionInfo = null;
	
	@Autowired
	private ObjectMapper mapper;
	
    @GetMapping
    public JsonResponseT<JsonNode> getVersion() throws IOException {
    	if(versionInfo == null) {
    		String json = StreamUtils.copyToString( new ClassPathResource("version.json").getInputStream(), Charset.forName("UTF-8"));
            versionInfo = mapper.readTree(json);
    	}
        return new JsonResponseT<>(true, versionInfo);
    }
}
