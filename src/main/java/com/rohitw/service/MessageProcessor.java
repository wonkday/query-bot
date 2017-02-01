package com.rohitw.service;

import com.rohitw.model.RVo;

import java.util.Map;


/**
 * Created by ROHITW on 12/31/2016.
 */
public abstract class MessageProcessor
{

    public String processMessage(String inputMsg, String acct, String instr)
    {
        preProcess(inputMsg);
        Map<String,Object>  tempOutput = processMessageImpl(inputMsg, acct, instr);
        postProcess(tempOutput);

        String formattedOutput = MessageFormatter.formatToHtml(tempOutput);
        return formattedOutput;
    }

    public void preProcess(String inputMsg)
    {

    }

    public abstract Map<String,Object> processMessageImpl(String inputMsg, String acct, String instr);

    public void postProcess(Map<String,Object> output)
    {

    }
}
