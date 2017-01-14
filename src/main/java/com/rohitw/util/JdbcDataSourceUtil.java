package com.rohitw.util;

import com.rohitw.model.RVo;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.rohitw.init.AppConfigConstants.*;

/**
 *
 * Created by ROHITW on 1/7/2017.
 */
public class JdbcDataSourceUtil
{
    private static Logger logger = Logger.getLogger(JdbcDataSourceUtil.class);
    public static final String CONFIG_QUERY = "SELECT * FROM bot_query_config WHERE is_active='Y' AND alert= :alertID";

    /**
     * Database types
     */
    public enum DbType
    {
        ORACLE("oracle"),
        MYSQL("mysql"),
        HSQL("hsql"),
        SQLITE("sqlite");

        private DbType(String str)
        {
            dbType = str;
        }

        private final String dbType;
    }

    public static DataSource getDataSource()
    {
        UserPropertySingleton instance = UserPropertySingleton.getInstance();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        try{
            dataSource.setDriverClassName(instance.getUserPropertyString(PROPERTY_NAME_DATABASE_DRIVER));
            String dbUrl = instance.getUserPropertyString(PROPERTY_NAME_DATABASE_URL);
            dataSource.setUrl(dbUrl);

            String dbType = instance.getUserPropertyString(PROPERTY_NAME_DATABASE_TYPE);

            if (DbType.MYSQL.toString().equalsIgnoreCase(dbType)
                    || DbType.HSQL.toString().equalsIgnoreCase(dbType)
                    || DbType.ORACLE.toString().equalsIgnoreCase(dbType))
            {
                dataSource.setUsername(instance.getUserPropertyString(PROPERTY_NAME_DATABASE_USERNAME));
                dataSource.setPassword(instance.getUserPropertyString(PROPERTY_NAME_DATABASE_PASSWORD));
            }
        }catch(Exception ex)
        {
            logger.error("getDataSource()", ex);
        }
        return dataSource;
    }

    public static DataSource getDataSource(String dbUrl,String userName, String password)
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        try {
            UserPropertySingleton instance = UserPropertySingleton.getInstance();
            dataSource.setDriverClassName(instance.getUserPropertyString(PROPERTY_NAME_DATABASE_DRIVER));
            String dbType = instance.getUserPropertyString(PROPERTY_NAME_DATABASE_TYPE);
            dataSource.setUrl(dbUrl);
            if (DbType.MYSQL.toString().equalsIgnoreCase(dbType) || DbType.HSQL.toString().equalsIgnoreCase(dbType)
                    || DbType.ORACLE.toString().equalsIgnoreCase(dbType) || DbType.SQLITE.toString().equalsIgnoreCase(dbType))
            {
                dataSource.setUsername(userName);
                dataSource.setPassword(password);
            }
        }catch(Exception ex)
        {
            logger.error("getDataSource(url)", ex);
        }
        return dataSource;
    }

    public static Properties hibernateProperties()
    {
        UserPropertySingleton instance = UserPropertySingleton.getInstance();
        Properties properties = new Properties();
        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, instance.getUserPropertyString(PROPERTY_NAME_HIBERNATE_DIALECT));
        properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, instance.getUserPropertyString(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

        String strNamingStrategyFlag = instance.getUserPropertyString(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY_FLAG);
        if ("true".equalsIgnoreCase(strNamingStrategyFlag))
        {
            properties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,instance.getUserPropertyString(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
        }
        return properties;
    }

    public static RVo[] executeSelectQuery(DataSource dataSource, String query)
    {
        return executeSelectQuery(dataSource,query, new MapSqlParameterSource());
    }

    public static RVo[] executeSelectQuery(DataSource dataSource, String query,MapSqlParameterSource paramSource)
    {
        logger.debug("Executing query -->" + query);
        List<RVo> resultList = new ArrayList<>();
        int count = 0;
        try {
            UserPropertySingleton instance = UserPropertySingleton.getInstance();
            JdbcTemplate jdbcTmpl = new JdbcTemplate(dataSource);
            jdbcTmpl.setMaxRows(instance.getUserPropertyInt(PROPERTY_NAME_DATABASE_MAX_ROWS));
            jdbcTmpl.setQueryTimeout(instance.getUserPropertyInt(PROPERTY_NAME_DATABASE_QUERY_TIMEOUT));
            NamedParameterJdbcTemplate namedJdbcTmpl = new NamedParameterJdbcTemplate(jdbcTmpl);
            SqlRowSet rowSet = namedJdbcTmpl.queryForRowSet(query, paramSource);

            int columnCount = rowSet.getMetaData().getColumnCount();
            logger.debug("Column Count: " + columnCount);

            while (rowSet.next()) {
                RVo row = new RVo();
                String colName, val;
                for (int i = 1; i <= columnCount; i++)
                {
                    colName = rowSet.getMetaData().getColumnName(i);
                    val = rowSet.getString(i);
                    row.setFieldData(colName.toUpperCase(), val);
                }
                resultList.add(row);
                count++;
            }
        }catch(Exception ex)
        {
            logger.error("getDataSource(url)", ex);
        }
        return resultList.toArray(new RVo[count]);
    }

}
