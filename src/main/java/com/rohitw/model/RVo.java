package com.rohitw.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Record VO
 * Created by ROHITW on 1/1/2017.
 */
public class RVo
{
    protected String dbObjectName;
    protected Map<String,String> rowData = new LinkedHashMap<>();

    public String getDbObjectName() {
        return dbObjectName;
    }

    public void setDbObjectName(String dbObjectName) {
        this.dbObjectName = dbObjectName;
    }

    public void setFieldData(String fieldName,String fieldValue)
    {
        rowData.put(fieldName,fieldValue);
    }

    public String getFieldData(String fieldName)
    {
        return rowData.get(fieldName);
    }

    public void setRowData(Map<String, String> inputRow)
    {
        rowData.putAll(inputRow);
    }

    public Map<String,String> getRowData()
    {
        return rowData;
    }
}
