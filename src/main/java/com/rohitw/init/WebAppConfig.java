package com.rohitw.init;

import java.util.List;

import javax.annotation.Resource;

import com.rohitw.util.JdbcDataSourceUtil;
import com.rohitw.util.UserPropertySingleton;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import static com.rohitw.init.AppConfigConstants.*;



@Configuration
@ComponentScan("com.rohitw")
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@Import({WebSecurityConfig.class})
public class WebAppConfig extends WebMvcConfigurerAdapter
{
    private Logger logger = Logger.getLogger(WebAppConfig.class);

    @Resource
	private Environment env;

    //@Bean
    public HibernateTransactionManager transactionManager() {
        logger.trace("ENTRY: setting up Hibernate txn mgr");
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        logger.trace("EXIT: setting up Hibernate txn mgr");
        return transactionManager;
    }

    //@Bean
	public LocalSessionFactoryBean sessionFactory() {
        logger.trace("ENTRY: setting up db session");
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        String initWithDbFlag = env.getRequiredProperty(PROPERTY_NAME_INIT_WITH_DATABASE);
        if("true".equalsIgnoreCase(initWithDbFlag)) {
            sessionFactoryBean.setDataSource(JdbcDataSourceUtil.getDataSource());
            sessionFactoryBean.setHibernateProperties(JdbcDataSourceUtil.hibernateProperties());
        }
        else
        {
            logger.trace("WARNING: bypassing db init...");
        }
		sessionFactoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
        logger.trace("EXIT: setting up app session");
        return sessionFactoryBean;
	}
	


	@Bean
	public UrlBasedViewResolver setupViewResolver() {
		UrlBasedViewResolver resolver = new UrlBasedViewResolver();
		resolver.setPrefix("/WEB-INF/pages/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		return resolver;
	}

    // equivalents for <mvc:resources/> tags
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/").setCachePeriod(PROPERTY_ONE_YEAR_IN_SECONDS);
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/").setCachePeriod(PROPERTY_ONE_YEAR_IN_SECONDS);

        loadProperties();
    }

    // equivalent for <mvc:default-servlet-handler/> tag
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
    {
        configurer.enable();
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver createMultipartResolver()
    {
        CommonsMultipartResolver resolver=new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        resolver.setMaxUploadSize(env.getRequiredProperty(PROPERTY_NAME_FILE_UPLOAD_SIZE, Long.class));
        return resolver;
    }

    private void loadProperties()
    {
        logger.trace("ENTRY: started loading user properties...");
        UserPropertySingleton instance = UserPropertySingleton.getInstance();
        instance.setUserProperty(PROPERTY_NAME_DATABASE_DRIVER,env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_TYPE,env.getRequiredProperty(PROPERTY_NAME_DATABASE_TYPE));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_USERNAME,env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_PASSWORD,env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_URL,env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_MAX_ROWS,env.getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_ROWS));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_QUERY_TIMEOUT,env.getRequiredProperty(PROPERTY_NAME_DATABASE_QUERY_TIMEOUT));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_MAX_QUERIES_ALLOWED,env.getProperty(PROPERTY_NAME_DATABASE_MAX_QUERIES_ALLOWED));
        instance.setUserProperty(PROPERTY_NAME_DATABASE_MAX_QUERIES_INTERVAL_MINUTES,env.getProperty(PROPERTY_NAME_DATABASE_MAX_QUERIES_INTERVAL_MINUTES));

        instance.setUserProperty(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        instance.setUserProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
        instance.setUserProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));

        instance.setUserProperty(PROPERTY_NAME_ALERT_QUERY,env.getProperty(PROPERTY_NAME_ALERT_QUERY));

        logger.trace("EXIT: finished loading user properties...");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        // http
        HttpMessageConverter converter = new StringHttpMessageConverter();
        converters.add(converter);
        logger.info("HttpMessageConverter added");

        // string
        converter = new FormHttpMessageConverter();
        converters.add(converter);
        logger.info("FormHttpMessageConverter added");

        // json
        converter = new MappingJackson2HttpMessageConverter();
        converters.add(converter);
        logger.info("MappingJackson2HttpMessageConverter added");
    }

}

