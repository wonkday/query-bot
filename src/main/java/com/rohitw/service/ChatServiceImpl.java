package com.rohitw.service;

import com.rohitw.init.AppConfigConstants;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;

/**
 * Created by ROHITW on 12/31/2016.
 */
public class ChatServiceImpl implements ChatService {

    private Logger logger = Logger.getLogger(ChatServiceImpl.class);

    @Override
    public String processRequest(String message) {
        logger.info("processing request message - " + message);
        MessageProcessor processor = new MessageProcessorImpl();
        String output = processor.processMessage(message, null, null);
        logger.debug("O/P=> " + output);
        return output;
    }

    @Override
    public String processRequest(HttpSession session, String message) {
        logger.info("processing request message - " + message);

        if(message.startsWith(AppConfigConstants.INSTRUCTION_DEBUG))
        {
            return setSessionKey(session,message);
        }
        if(message.startsWith(AppConfigConstants.INSTRUCTION_ACCOUNT))
        {
            logger.info(setSessionKey(session,message));
        }
        String debugInstr = String.valueOf(session.getAttribute(AppConfigConstants.INSTRUCTION_DEBUG));
        String acct = (String) session.getAttribute(AppConfigConstants.INSTRUCTION_ACCOUNT);

        MessageProcessor processor = new MessageProcessorImpl();
        String output = processor.processMessage(message,acct,debugInstr);
        logger.debug("O/P=> " + output);
        return output;
    }

    private String setSessionKey(HttpSession session, String msg)
    {
        logger.info("Received special instruction....");
        String[] str = msg.split("=");
        String key = str[0];
        String val = str[1];
        logger.info("KEY=" + key + ", VAL=" + val);
        session.setAttribute(key,val);
        return key + " set, please continue..";
    }
}
