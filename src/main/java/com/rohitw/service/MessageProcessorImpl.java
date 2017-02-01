package com.rohitw.service;

import com.rohitw.init.AppConfigConstants;
import com.rohitw.util.CacheManager;
import com.rohitw.util.JdbcDataSourceUtil;
import com.rohitw.model.RVo;
import com.rohitw.util.JsonUtil;
import com.rohitw.util.UserPropertySingleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import static com.rohitw.init.AppConfigConstants.*;

/**
 * Created by ROHITW on 12/31/2016.
 */
public class MessageProcessorImpl extends MessageProcessor
{

    private static final String CACHE_ID_CONFIG_DS = "_configDB";
    private static final String CACHE_ID_APP_DS_PREFIX = "_AppDB_";

    private static final String METADATA_CACHE_ID_DS_KEY_TIME = "_time_";
    private static final String METADATA_CACHE_ID_DS_KEY_COUNT = "_cnt_";

    private Logger logger = Logger.getLogger(MessageProcessorImpl.class);
    private CacheManager cacheManager = CacheManager.INSTANCE;
    private UserPropertySingleton propertySingleton = UserPropertySingleton.getInstance();

    @Override
    public Map<String,Object> processMessageImpl(String inputMsg, String account, String instr) {

        Map<String,Object> response = null;

        if(instr != null && instr.equals(AppConfigConstants.IDENTIFIER_DUMMY)) {
            logger.info("processing dummy message");
            response = dummyResponse(inputMsg);
        }
        else
        {
            response = new HashMap<>();
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            paramSource.addValue(AppConfigConstants.IDENTIFIER_ALERT_ID, inputMsg);
            String configQuery = propertySingleton.getUserPropertyString(PROPERTY_NAME_ALERT_QUERY);
            if(StringUtils.isNotEmpty(account))
            {
                paramSource.addValue(AppConfigConstants.IDENTIFIER_ACCOUNT, account);
                configQuery = JdbcDataSourceUtil.CONFIG_QUERY_ACCT;
            }

            DataSource configDbDS = (DataSource) cacheManager.getItemFromCache(CACHE_ID_CONFIG_DS);
            if(configDbDS == null)
            {
                logger.info("No DataSource found! creating config DS...");
                configDbDS = JdbcDataSourceUtil.getDataSource();
                cacheManager.addItemToCache(CACHE_ID_CONFIG_DS,configDbDS);
            }
            else
            {
                logger.info("Using previously created config DS...");
            }

            RVo[] configVoArr = JdbcDataSourceUtil.executeSelectQuery(configDbDS, configQuery, paramSource);
            if(configVoArr != null && configVoArr.length == 1)
            {
                RVo[] voArr = null;
                logger.info("Found unique record for AlertID: " + inputMsg);
                String dbUid = configVoArr[0].getFieldData(QUERY_DB_UID);

                Integer queryExecCount = 0;
                Object count = cacheManager.getItemFromMetadataCache(METADATA_CACHE_ID_DS_KEY_COUNT + dbUid);
                if(count != null)
                {
                    queryExecCount = (Integer) cacheManager.getItemFromMetadataCache(METADATA_CACHE_ID_DS_KEY_COUNT + dbUid);
                }

                int maxQueryExecCnt = propertySingleton.getUserPropertyInt(PROPERTY_NAME_DATABASE_MAX_QUERIES_ALLOWED);
                int maxQueryInterval = propertySingleton.getUserPropertyInt(PROPERTY_NAME_DATABASE_MAX_QUERIES_INTERVAL_MINUTES);
                int maxQueryIntervalMillis = maxQueryInterval * 1000 * 60;

                long lastQueriedTime = 0L;
                Object timestamp = cacheManager.getItemFromMetadataCache(METADATA_CACHE_ID_DS_KEY_TIME + dbUid);
                if(timestamp != null)
                {
                    lastQueriedTime = (Long) cacheManager.getItemFromMetadataCache(METADATA_CACHE_ID_DS_KEY_TIME + dbUid);
                }
                long currentTime = System.currentTimeMillis();

                StringBuilder stringBuilder = new StringBuilder("dbUid=<");
                stringBuilder.append(dbUid);
                stringBuilder.append(">, queryExecCount=<");
                stringBuilder.append(queryExecCount);
                stringBuilder.append(">, lastQueriedTime=<");
                stringBuilder.append(lastQueriedTime);
                stringBuilder.append(">, maxQueryExecCnt=<");
                stringBuilder.append(maxQueryExecCnt);
                stringBuilder.append(">, currentTime=<");
                stringBuilder.append(currentTime);
                stringBuilder.append(">");

                logger.info("Found unique record for AlertID: " + inputMsg);

                if(queryExecCount!= null && queryExecCount > maxQueryExecCnt && currentTime < (lastQueriedTime + maxQueryIntervalMillis))
                {
                    voArr = new RVo[1];
                    int i=0;
                    voArr[i] = new RVo();
                    voArr[i].setFieldData("WARNING", "Execution skipped!");
                    String warnStr = "Too many queries are executed on this DB.. Please wait";
                    response.put(QUERY_RESPONSE,voArr);
                    response.put(QUERY_POST_INSTRUCTION,warnStr);
                    response.put(RESPONSE_TYPE,RESPONSE_TYPE_ERROR);
                }
                else
                {
                    if(queryExecCount!= null && queryExecCount > maxQueryExecCnt)
                    {
                        //if we are here, it means time interval is passed, to reset counter to 1(for current execution)
                        cacheManager.addItemToMetadataCache(METADATA_CACHE_ID_DS_KEY_COUNT + dbUid, 1);
                    }
                    else {
                        cacheManager.addItemToMetadataCache(METADATA_CACHE_ID_DS_KEY_COUNT + dbUid, ++queryExecCount);
                    }

                    cacheManager.addItemToMetadataCache(METADATA_CACHE_ID_DS_KEY_TIME + dbUid, currentTime);

                    String query = configVoArr[0].getFieldData(QUERY_DB_SQL_TEXT);
                    String dbUrl = configVoArr[0].getFieldData(QUERY_DB_URL);
                    String dbUser = configVoArr[0].getFieldData(QUERY_DB_USER);
                    String dbPass = configVoArr[0].getFieldData(QUERY_DB_PASS);
                    String queryParams = configVoArr[0].getFieldData(QUERY_INPUT_PARAMS);
                    String postInstr = configVoArr[0].getFieldData(QUERY_POST_INSTRUCTION);

                    stringBuilder = new StringBuilder("Running query <");
                    stringBuilder.append(query);
                    stringBuilder.append("> on ");
                    stringBuilder.append("DB_UID <");
                    stringBuilder.append(dbUid);
                    stringBuilder.append("> DB_URL <");
                    stringBuilder.append(dbUrl);
                    stringBuilder.append("> DB_USER <");
                    stringBuilder.append(dbUser);
                    if(StringUtils.isNotEmpty(queryParams)) {
                        stringBuilder.append("> PARAM <");
                        stringBuilder.append(queryParams);
                    }
                    stringBuilder.append(">");
                    logger.info(stringBuilder.toString());

                    DataSource appDbDS = (DataSource) cacheManager.getItemFromCache(CACHE_ID_APP_DS_PREFIX + dbUid);
                    if (appDbDS == null) {
                        logger.info("No DataSource found! creating DS for " + dbUid);
                        appDbDS = JdbcDataSourceUtil.getDataSource(dbUrl, dbUser, dbPass);
                        cacheManager.addItemToCache(CACHE_ID_APP_DS_PREFIX + dbUid, appDbDS);
                    } else {
                        logger.info("Using previously created DS for " + dbUid);
                    }

                    voArr = JdbcDataSourceUtil.executeSelectQuery(appDbDS, query, JsonUtil.parseJsonStringToSqlParam(queryParams));
                    response.put(QUERY_RESPONSE,voArr);
                    response.put(QUERY_POST_INSTRUCTION,postInstr);
                    if(instr != null && instr.equals(DEBUG_INSTR_QUERY)) {
                        response.put(DEBUG_INSTR_QUERY,query);
                    }
                    response.put(RESPONSE_TYPE,RESPONSE_TYPE_NORMAL);
                }
            }
            if(configVoArr.length > 1)
            {
                logger.warn("Found multiple records for search key: " + inputMsg);
                RVo[] voArr = new RVo[configVoArr.length];
                int i=0;
                for(RVo vo: configVoArr)
                {
                    voArr[i] = new RVo();
                    voArr[i].setFieldData(QUERY_ACCOUNT, vo.getFieldData(QUERY_ACCOUNT));
                    voArr[i].setFieldData(QUERY_ALERT_ID, vo.getFieldData(QUERY_ALERT_ID));
                    i++;
                }

                String warnStr = "Above are similar Alert ids.. please type relevant id to proceed and/or select Account.";
                response.put(QUERY_RESPONSE,voArr);
                response.put(QUERY_POST_INSTRUCTION,warnStr);
                response.put(RESPONSE_TYPE,RESPONSE_TYPE_WARNING);
            }
            else
            {
                logger.error("ERROR!! - Didn't find unique record for AlertID: " + inputMsg);
            }
        }
        return response;
    }

    private Map<String,Object> dummyResponse(String inputMsg)
    {
        Map<String,Object> response = new HashMap<>();
        RVo[] voArr;
        switch (inputMsg)
        {
            case "bap":
                voArr = new RVo[3];
                int i=0;
                voArr[i] = new RVo();
                voArr[i].setFieldData("PENDING", "100");
                voArr[i].setFieldData("PROCESSED", "1111");
                voArr[i].setFieldData("FAILED", "1");
                i++;
                voArr[i] = new RVo();
                voArr[i].setFieldData("PENDING", "200");
                voArr[i].setFieldData("PROCESSED", "2222");
                voArr[i].setFieldData("FAILED", "2");
                i++;
                voArr[i] = new RVo();
                voArr[i].setFieldData("PENDING", "300");
                voArr[i].setFieldData("PROCESSED", "3333");
                voArr[i].setFieldData("FAILED", "3");

                response.put(QUERY_POST_INSTRUCTION,"Dummy BAP data.. Ignore!");
                break;

            default:
                voArr = new RVo[1];
                voArr[0] = new RVo();
                voArr[0].setFieldData("DATA", "0");
                voArr[0].setFieldData(inputMsg, "processed");
                response.put(QUERY_POST_INSTRUCTION,"Dummy response.. Ignore!");
        }

        response.put(QUERY_RESPONSE,voArr);
        return response;
    }
}
