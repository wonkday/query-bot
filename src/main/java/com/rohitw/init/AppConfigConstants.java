package com.rohitw.init;

/**
 * Created by ROHITW on 1/7/2017.
 */
public class AppConfigConstants 
{
    public static final String PROPERTY_NAME_INIT_WITH_DATABASE = "init.with.db";
    public static final String PROPERTY_NAME_DATABASE_TYPE = "db.type";
    public static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    public static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    public static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    public static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

    public static final String PROPERTY_NAME_DATABASE_MAX_ROWS = "db.query.max.rows";
    public static final String PROPERTY_NAME_DATABASE_QUERY_TIMEOUT = "db.query.timeout.sec";

    public static final String PROPERTY_NAME_DATABASE_MAX_QUERIES_ALLOWED = "db.max.queries.allowed";
    public static final String PROPERTY_NAME_DATABASE_MAX_QUERIES_INTERVAL_MINUTES = "db.max.queries.interval.min";

    public static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    public static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY_FLAG = "hibernate.use.naming.strategy";
    public static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    public static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

    public static final String PROPERTY_NAME_FILE_UPLOAD_SIZE = "file.upload.size";

    public static final String PROPERTY_NAME_ALERT_QUERY = "alert.query";

    public static final int PROPERTY_ONE_YEAR_IN_SECONDS = 31556926;

    public static final String QUERY_DB_URL = "DB_URL";
    public static final String QUERY_DB_UID = "DB_UID";
    public static final String QUERY_DB_USER = "DB_USER";
    public static final String QUERY_DB_PASS = "DB_PASS";
    public static final String QUERY_DB_SQL_TEXT = "SQL_TXT";
    public static final String QUERY_POST_INSTRUCTION = "POST_INSTR";
    public static final String QUERY_INPUT_PARAMS = "QUERY_PARAMS";
    public static final String QUERY_ALERT_ID = "ALERT";
    public static final String QUERY_ACCOUNT = "ACCOUNT";

    public static final String QUERY_RESPONSE = "response";
    public static final String DEBUG_INSTR_QUERY = "query";

    public static final String RESPONSE_TYPE = "response_type";
    public static final String RESPONSE_TYPE_NORMAL = "N";
    public static final String RESPONSE_TYPE_WARNING = "W";
    public static final String RESPONSE_TYPE_ERROR = "E";

    public static final String INSTRUCTION_DEBUG = "$debug";
    public static final String IDENTIFIER_ALERT_ID = "alertID";
    public static final String IDENTIFIER_DUMMY = "dummy";
    public static final String IDENTIFIER_ACCOUNT = "acct";
    public static final String INSTRUCTION_ACCOUNT = "$acct";
}
