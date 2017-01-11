package com.rohitw.service;

import com.rohitw.init.JdbcDataSourceUtil;
import com.rohitw.model.RVo;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.HashMap;
import java.util.Map;
import static com.rohitw.init.AppConfigConstants.*;

/**
 * Created by ROHITW on 12/31/2016.
 */
public class MessageProcessorImpl extends MessageProcessor
{

    private Logger logger = Logger.getLogger(MessageProcessorImpl.class);

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
            RVo[] configVoArr = JdbcDataSourceUtil.executeSelectQuery(JdbcDataSourceUtil.getDataSource(), JdbcDataSourceUtil.CONFIG_QUERY , paramSource);
            if(configVoArr != null && configVoArr.length == 1)
            {
                logger.info("Found unique record for AlertID: " + inputMsg);
                String query = configVoArr[0].getFieldData(QUERY_DB_SQL_TEXT);

                String dbUid = configVoArr[0].getFieldData(QUERY_DB_UID);
                String dbUrl = configVoArr[0].getFieldData(QUERY_DB_URL);
                String dbUser = configVoArr[0].getFieldData(QUERY_DB_USER);
                String dbPass = configVoArr[0].getFieldData(QUERY_DB_PASS);
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
                stringBuilder.append("> DB_PASS <");
                stringBuilder.append(dbPass);
                stringBuilder.append(">");
                logger.info(stringBuilder.toString());

                RVo[] voArr = JdbcDataSourceUtil.executeSelectQuery(JdbcDataSourceUtil.getDataSource(dbUrl,dbUser,dbPass),query);

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
                voArr[i].setFieldData("pending", "100");
                voArr[i].setFieldData("processed", "1111");
                voArr[i].setFieldData("failed", "1");
                i++;
                voArr[i] = new RVo();
                voArr[i].setFieldData("pending", "200");
                voArr[i].setFieldData("processed", "2222");
                voArr[i].setFieldData("failed", "2");
                i++;
                voArr[i] = new RVo();
                voArr[i].setFieldData("pending", "300");
                voArr[i].setFieldData("processed", "3333");
                voArr[i].setFieldData("failed", "3");

                response.put(QUERY_POST_INSTRUCTION,"Dummy BAP data.. Ignore!");
                break;

            default:
                voArr = new RVo[1];
                voArr[0] = new RVo();
                voArr[0].setFieldData("data", "0");
                voArr[0].setFieldData(inputMsg, "processed");
                response.put(QUERY_POST_INSTRUCTION,"Dummy response.. Ignore!");
        }

        response.put(QUERY_RESPONSE,voArr);
        return response;
    }


}
