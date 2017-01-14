package com.rohitw.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ROHITW on 1/13/2017.
 */
public class JsonUtil {

    private static Logger logger = Logger.getLogger(JsonUtil.class);

    public static MapSqlParameterSource parseJsonStringToSqlParam(String jsonString)
    {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        if (jsonString != null) {
            ObjectMapper jsonMapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<String, Object>();

            try {
                map = jsonMapper.readValue(jsonString, new TypeReference<Object>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Iterator<String> iter = map.keySet().iterator();

            while (iter.hasNext()) {
                String key = iter.next();
                logger.debug("Key=" + key + ", Val=" + map.get(key));
                paramSource.addValue(key, map.get(key));
            }
        }
        return paramSource;
    }

}
