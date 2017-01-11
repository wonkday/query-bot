package com.rohitw.service;

import com.rohitw.model.RVo;
import org.apache.log4j.Logger;

/*
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
*/
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static com.rohitw.init.AppConfigConstants.*;

/**
 * Created by ROHITW on 12/31/2016.
 */
public class MessageFormatter
{
    private static final String HTML_TAG_TABLE_START = "<table border=\"1\">";
    private static final String HTML_TAG_TABLE_END = "</table>";

    private static final String HTML_TAG_TR_START = "<tr>";
    private static final String HTML_TAG_TR_END = "</tr>";

    private static final String HTML_TAG_TD_START = "<td style=\"text-align: center; min-width:100px\">";
    private static final String HTML_TAG_TD_END = "</td>";

    private static final String HTML_TAG_SPAN_START = "<span style=\"font-weight:bold\">";
    private static final String HTML_TAG_SPAN_END = "</span>";

    private static final String HTML_TAG_BR = "<br>";

    private static Logger logger = Logger.getLogger(MessageFormatter.class);

    public static String formatToHtml(Map<String,Object>  response)
    {
        if(response == null)
        {
            logger.error("ERROR! - response is null");
            return "No Data!!";
        }
        RVo[] tempOutput = (RVo[]) response.get(QUERY_RESPONSE);
        String postInstruction = String.valueOf(response.get(QUERY_POST_INSTRUCTION));
        String debugData = String.valueOf(response.get(DEBUG_INSTR_QUERY));

        if(tempOutput == null)
        {
            logger.warn("WARNING! - no data found!");
            return "No Data!!";
        }
        StringBuilder strRowHdr = new StringBuilder(HTML_TAG_TABLE_START);
        StringBuilder strRowData = null;
        boolean isHdrNotAdded = true;
        for(RVo vo: tempOutput)
        {
            strRowData = new StringBuilder();
            strRowHdr.append(HTML_TAG_TR_START);
            if(isHdrNotAdded) {
                strRowData.append(HTML_TAG_TR_START);
            }
            HashMap fields = vo.getRowData();
            Iterator iterator = fields.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry field = (Map.Entry) iterator.next();
                if(isHdrNotAdded) {
                    strRowHdr.append(HTML_TAG_TD_START);
                    strRowHdr.append(HTML_TAG_SPAN_START);
                    strRowHdr.append(field.getKey());
                    strRowHdr.append(HTML_TAG_SPAN_END);
                    strRowHdr.append(HTML_TAG_TD_END);
                }
                strRowData.append(HTML_TAG_TD_START);
                strRowData.append(field.getValue());
                strRowData.append(HTML_TAG_TD_END);
            }
            strRowData.append(HTML_TAG_TR_END);

            if(isHdrNotAdded)
            {
                strRowHdr.append(HTML_TAG_TR_END);
            }
            isHdrNotAdded = false;

            if(strRowData != null) {
                strRowHdr.append(strRowData);
            }
        }
        strRowData.append(HTML_TAG_TR_END);
        strRowHdr.append(HTML_TAG_TABLE_END);

        if(postInstruction!=null && !postInstruction.equals("null"))
        {
            strRowHdr.append(HTML_TAG_BR);
            strRowHdr.append(HTML_TAG_SPAN_START);
            strRowHdr.append("Instruction:");
            strRowHdr.append(HTML_TAG_SPAN_END);
            strRowHdr.append(" ");
            strRowHdr.append(postInstruction);
        }

        if(debugData!=null && !debugData.equals("null"))
        {
            strRowHdr.append(HTML_TAG_BR);
            strRowHdr.append(HTML_TAG_SPAN_START);
            strRowHdr.append("Debug Info:");
            strRowHdr.append(HTML_TAG_SPAN_END);
            strRowHdr.append(" ");
            strRowHdr.append(debugData);
        }

        return strRowHdr.toString();
    }
}
