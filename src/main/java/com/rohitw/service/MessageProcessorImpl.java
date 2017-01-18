package com.rohitw.service;

import com.rohitw.util.CacheManager;
import com.rohitw.util.JdbcDataSourceUtil;
import com.rohitw.model.RVo;
import com.rohitw.util.JsonUtil;
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

    private Logger logger = Logger.getLogger(MessageProcessorImpl.class);
    private CacheManager cacheManager = CacheManager.INSTANCE;

    @Override
    public Map<String,Object> processMessageImpl(String inputMsg, String instr) {

        Map<String,Object> response = null;

        if(instr != null && instr.equals("dummy")) {
            logger.info("processing dummy message");
            response = dummyResponse(inputMsg);
        }
        else
        {
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            paramSource.addValue("alertID", inputMsg);

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

            RVo[] configVoArr = JdbcDataSourceUtil.executeSelectQuery(configDbDS, JdbcDataSourceUtil.CONFIG_QUERY , paramSource);
            if(configVoArr != null && configVoArr.length == 1)
            {
                logger.info("Found unique record for AlertID: " + inputMsg);
                String query = configVoArr[0].getFieldData(QUERY_DB_SQL_TEXT);

                String dbUid = configVoArr[0].getFieldData(QUERY_DB_UID);
                String dbUrl = configVoArr[0].getFieldData(QUERY_DB_URL);
                String dbUser = configVoArr[0].getFieldData(QUERY_DB_USER);
                String dbPass = configVoArr[0].getFieldData(QUERY_DB_PASS);
                String queryParams = configVoArr[0].getFieldData(QUERY_INPUT_PARAMS);
                String postInstr = configVoArr[0].getFieldData(QUERY_POST_INSTRUCTION);

                StringBuilder stringBuilder = new StringBuilder("\nRunning query <");
                stringBuilder.append(query);
                stringBuilder.append("> on ");
                stringBuilder.append("DB_UID <");
                stringBuilder.append(dbUid);
                stringBuilder.append("> DB_URL <");
                stringBuilder.append(dbUrl);
                stringBuilder.append("> DB_USER <");
                stringBuilder.append(dbUser);
                stringBuilder.append("> PARAM <");
                stringBuilder.append(queryParams);
                stringBuilder.append(">");
                logger.info(stringBuilder.toString());

                DataSource appDbDS = (DataSource) cacheManager.getItemFromCache(CACHE_ID_APP_DS_PREFIX + dbUid);
                if(appDbDS == null)
                {
                    logger.info("No DataSource found! creating DS for "+ dbUid);
                    appDbDS = JdbcDataSourceUtil.getDataSource(dbUrl,dbUser,dbPass);
                    cacheManager.addItemToCache(CACHE_ID_APP_DS_PREFIX + dbUid, appDbDS);
                }
                else
                {
                    logger.info("Using previously created DS for "+ dbUid);
                }

                RVo[] voArr = JdbcDataSourceUtil.executeSelectQuery(appDbDS,query,JsonUtil.parseJsonStringToSqlParam(queryParams));

                response = new HashMap<>();
                response.put(QUERY_RESPONSE,voArr);
                response.put(QUERY_POST_INSTRUCTION,postInstr);
                if(instr != null && instr.equals(DEBUG_INSTR_QUERY)) {
                    response.put(DEBUG_INSTR_QUERY,query);
                }
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
