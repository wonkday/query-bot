package com.rohitw.model;

import java.util.HashMap;

/**
 * Record VO
 * Created by ROHITW on 1/1/2017.
 */
public class RVo
{
    protected String dbObjectName;
    protected HashMap<String,String> rowData = new HashMap<>();

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

    public void setRowData(HashMap<String, String> inputRow)
    {
        rowData.putAll(inputRow);
    }

    public HashMap<String,String> getRowData()
    {
        return rowData;
    }
}
