package com.syg.ifmdruid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 链接池配置文件，如果配置文件中包含key  spring.datasource.url  该配置文件生效，否则走多数据源
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Configuration
@ConditionalOnExpression("#{environment.containsProperty('spring.datasource.url')}")
public class DataSourceConfig {

    private static String dbUrl;

    private static String username;

    private static String password;

    private static String driverClassName;

    private static int initialSize;

    private static int minIdle;

    private static int maxActive;

    private static int maxWait;

    private static String loginUsername;

    private static String loginPassword;

    private static String  validationQuery;

    private static boolean testWhileIdle;

    private static boolean  testOnBorrow;

    private static boolean testOnReturn;

    private static boolean poolPreparedStatements;

    private static int maxPoolPreparedStatementPerConnectionSize;


    /**
     * 注册DruidServlet
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean druidServletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");
        //登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername", loginUsername);
        servletRegistrationBean.addInitParameter("loginPassword", loginPassword);
        return servletRegistrationBean;
    }

    /**
     * 注册DruidFilter拦截
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean druidFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<String, String>();
        //设置忽略请求
        initParams.put("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.setInitParameters(initParams);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    /**
     * 配置DataSource
     * @return
     * @throws SQLException
     */
    @Bean(initMethod = "init",destroyMethod = "close")
    @Primary
    public DruidDataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setUrl(dbUrl);
        druidDataSource.setFilters("stat,wall");
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setUseGlobalDataSourceStat(true);
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        return druidDataSource;
    }

    @Value("${spring.datasource.url}")
    public void setDbUrl(String dbUrl) {
        DataSourceConfig.dbUrl = dbUrl;
    }

    @Value("${spring.datasource.username}")
    public void setUsername(String username) {
        DataSourceConfig.username = username;
    }

    @Value("${spring.datasource.password}")
    public void setPassword(String password) {
        DataSourceConfig.password = password;
    }

    @Value("${spring.datasource.driver-class-name}")
    public void setDriverClassName(String driverClassName) {
        DataSourceConfig.driverClassName = driverClassName;
    }

    @Value(value = "${spring.datasource.initialSize:5}")
    public void setInitialSize(int initialSize) {
        DataSourceConfig.initialSize = initialSize;
    }

    @Value(value = "${spring.datasource.minIdle:5}")
    public void setMinIdle(int minIdle) {
        DataSourceConfig.minIdle = minIdle;
    }

    @Value(value = "${spring.datasource.maxActive:50}")
    public void setMaxActive(int maxActive) {
        DataSourceConfig.maxActive = maxActive;
    }

    @Value(value = "${spring.datasource.maxWait:60000}")
    public void setMaxWait(int maxWait) {
        DataSourceConfig.maxWait = maxWait;
    }
    @Value("${spring.datasource.loginUsername:admin}")
    public void setLoginUsername(String loginUsername) {
        DataSourceConfig.loginUsername = loginUsername;
    }

    @Value("${spring.datasource.loginPassword:admin}")
    public void setLoginPassword(String loginPassword) {
        DataSourceConfig.loginPassword = loginPassword;
    }

    @Value("${spring.datasource.validationQuery:SELECT 1 FROM DUAL}")
    public void setValidationQuery(String validationQuery) {
        DataSourceConfig.validationQuery = validationQuery;
    }

    @Value("${spring.datasource.testWhileIdle:true}")
    public void setTestWhileIdle(boolean testWhileIdle) {
        DataSourceConfig.testWhileIdle = testWhileIdle;
    }

    @Value("${spring.datasource.testOnBorrow:false}")
    public void setTestOnBorrow(boolean testOnBorrow) {
        DataSourceConfig.testOnBorrow = testOnBorrow;
    }

    @Value("${spring.datasource.testOnReturn:false}")
    public void setTestOnReturn(boolean testOnReturn) {
        DataSourceConfig.testOnReturn = testOnReturn;
    }

    @Value("${spring.datasource.poolPreparedStatements:false}")
    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        DataSourceConfig.poolPreparedStatements = poolPreparedStatements;
    }

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize:20}")
    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        DataSourceConfig.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }
}
