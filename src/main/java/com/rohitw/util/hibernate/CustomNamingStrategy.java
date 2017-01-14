package com.rohitw.util.hibernate;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * Created by ROHITW on 3/21/2015.
 */
public class CustomNamingStrategy extends ImprovedNamingStrategy
{

    private static final String TABLE_PREFIX = "rw_";

    /**
     * Transforms class names to table names by using the described naming conventions.
     * @param className
     * @return  The constructed table name.
     */
    @Override
    public String classToTableName(String className)
    {
        String tableNameInSingularForm = super.classToTableName(className);
        return transformTableName(tableNameInSingularForm);
    }

    private String transformTableName(String tableNameInSingularForm)
    {
        StringBuilder pluralForm = new StringBuilder();
        pluralForm.append(TABLE_PREFIX);
        pluralForm.append(tableNameInSingularForm);
        return pluralForm.toString();
    }
}
