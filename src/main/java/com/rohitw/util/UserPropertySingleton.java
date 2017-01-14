package com.rohitw.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rohitw on 6/21/2015.
 */
public class UserPropertySingleton
{
    private static UserPropertySingleton instance = null;
    private static ConcurrentHashMap<String,Object> propertiesMap = null;
    private static Logger logger = Logger.getLogger(UserPropertySingleton.class);

    protected UserPropertySingleton()
    {}

    public static UserPropertySingleton getInstance()
    {
        if(instance == null)
        {
            logger.trace("ENTRY: initializing User property Singleton");
            instance = new UserPropertySingleton();
            propertiesMap = new ConcurrentHashMap<String, Object>();
        }
        return instance;
    }

    public Object getUserProperty(String key)
    {
        return propertiesMap.get(key);
    }

    public String getUserPropertyString(String key)
    {
        return String.valueOf(propertiesMap.get(key));
    }

    public int getUserPropertyInt(String key)
    {
        String num = String.valueOf(propertiesMap.get(key));
        if(StringUtils.isEmpty(num) || "null".equals(num))
        {
            return 0;
        }
        else
        {
            return Integer.parseInt(num);
        }
    }

    public void setUserProperty(String key, String value)
    {
        propertiesMap.putIfAbsent(key, value);
    }

    public void setUserProperty(String key, Object value)
    {
        propertiesMap.putIfAbsent(key, value);
    }
}
