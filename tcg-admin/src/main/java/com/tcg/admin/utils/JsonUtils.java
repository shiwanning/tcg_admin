package com.tcg.admin.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.Lists;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper mapper = null;

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    public static void setObjectMapperStatic(ObjectMapper injectedMapper) {
        JsonUtils.mapper = injectedMapper;
    }

    public static String toJson(Object obj) {
        try {
            if (obj == null || obj instanceof MissingNode) {
                return null;
            }
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.JSON_ERR, e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.JSON_ERR, e);
        }
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        try {
            return mapper.readValue(json, mapper.getTypeFactory()
                                                .constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.JSON_ERR, e);
        }
    }

    public static Map<String, String> jsonStringToMap(String json) {
        try {
            return mapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new AdminServiceBaseException(AdminErrorCode.JSON_ERR, e);
        }
    }

    public static String map2Json(Map<String, Object> map) {
        return toJson(map);
    }

    public static String mapString2Json(Map<String, String> map) {
        return toJson(map);
    }

    public static <T> List<T> getListFromJsonNode(JsonNode arrNode, Class<T> tClass) {
        try {
            List<T> list = Lists.newLinkedList();
            for (final JsonNode objNode : arrNode) {
                T obj = mapper.treeToValue(objNode, tClass);
                list.add(obj);
            }
            return list;
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.JSON_ERR, e);
        }
    }

    public static JsonNode getJsonNodeFromJson(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.JSON_ERR, e);
        }
    }
    
    public static boolean isJson(Object json) {
        try {
            mapper.readTree(json.toString());
            return true;
        } catch (Exception e) {
            LOGGER.error("JsonUtils error:",e);
            return false;
        }
    }

    public static JsonNode createJsonNode(Object obj) {
        try {
            if (obj instanceof String) {
                return getJsonNodeFromJson(String.valueOf(obj));
            } else {
                return mapper.convertValue(obj, JsonNode.class);
            }
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.JSON_ERR, e);
        }
    }

}
