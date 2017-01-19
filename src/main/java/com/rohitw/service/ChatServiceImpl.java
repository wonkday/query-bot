package com.rohitw.service;

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
        String output = processor.processMessage(message, null);
        logger.debug("O/P=> " + output);
        return output;
    }

    @Override
    public String processRequest(HttpSession session, String message) {
        logger.info("processing request message - " + message);

        if(message.startsWith("$debug"))
        {
            String[] str = message.split("=");
            logger.info("Received special instruction....");
            String key = str[0];
            String val = str[1];
            logger.info("KEY=" + key + ", VAL=" + val);
            session.setAttribute(key,val);
            return "command set, please continue..";
        }
        String debugInstr = String.valueOf(session.getAttribute("$debug"));

        MessageProcessor processor = new MessageProcessorImpl();
        String output = processor.processMessage(message,debugInstr);
        logger.debug("O/P=> " + output);
        return output;
    }
}
