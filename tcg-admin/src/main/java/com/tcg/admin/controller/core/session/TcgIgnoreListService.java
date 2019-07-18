package com.tcg.admin.controller.core.session;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.tcg.admin.model.xml.IgnoreURI;
import com.tcg.admin.model.xml.RequestIntervalType;

@Component
public class TcgIgnoreListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcgIgnoreListService.class);
    private Map<String, IgnoreURI> ignoreMap;
    
    private List<String> ignoreFuzzyKeys;

    @PostConstruct
    public void init() {

        final String xmlFileName = "behavior_log_ignore.xml";
        ignoreMap = new HashMap<>();
        
        ignoreFuzzyKeys = Lists.newLinkedList();

        JAXBContext jaxbContext;
        Unmarshaller jaxbUnmarshaller;
        try {
            jaxbContext = JAXBContext.newInstance(RequestIntervalType.class);

            jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            URL resource = this.getClass().getClassLoader().getResource(xmlFileName);

            RequestIntervalType requsetInterval = (RequestIntervalType) jaxbUnmarshaller.unmarshal(resource);
            
            for(IgnoreURI uri : requsetInterval.getIgnoreList().getURIs().getURI()) {
                ignoreMap.put(uri.getValue(), uri);

                if(uri.getValue().contains("*")) {
                    ignoreFuzzyKeys.add(uri.getValue().replaceAll("/\\*", ""));
                }
            }
        } catch (JAXBException e) {
            LOGGER.error("Load com.tcg.admin.model.xml: {} failed.", xmlFileName, e);
        }
    }

    /**
     * Check RequestURI exists in properties.
     * 
     * @param reqURIKey
     * @return
     */
    public boolean checkRequestURIExist(String reqURIKey) {

        if(StringUtils.isEmpty(reqURIKey)) {
            return false;
        }
        
        IgnoreURI ignoreUri = ignoreMap.get(reqURIKey);

        if(ignoreUri == null) {
            for(String fuzzyUri : ignoreFuzzyKeys) {
                if(reqURIKey.contains(fuzzyUri)) {
                    ignoreUri = ignoreMap.get(fuzzyUri + "/*");
                    break;
                }
            }
        }
        
        return ignoreUri != null;
    }
}
