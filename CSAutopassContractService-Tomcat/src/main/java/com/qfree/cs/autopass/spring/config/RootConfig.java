package com.qfree.cs.autopass.spring.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;

import com.qfree.cs.autopass.ws.ContractWs;
import com.qfree.cs.autopass.ws.ContractWsSEI;
import com.qfree.cs.autopass.ws.config.AppConfigParams;
import com.qfree.cs.autopass.ws.service.ContractService;
import com.qfree.cs.autopass.ws.service.ContractServiceJdbcRaw;

//import com.borgsoftware.springmvc.spring.web.PropertyTest;

/**
 * The main/root class for Java-based configuration for the root Spring
 * container shared by all servlets and filters.
 */
@Configuration
@ImportResource("/WEB-INF/spring/root-context.xml")
// This is for a *single* properties file:
@PropertySource("classpath:config.properties")
// This is for *multiple* properties files (Spring 4+). The @PropertySource 
// elements must be comma-separated:
//@PropertySources({
//		@PropertySource("classpath:config.properties")
//})
public class RootConfig {

	//	private static final Logger logger = LoggerFactory.getLogger(RootConfig.class);

	// Load application configuration parameters so they can be injected into
	// beans below where necessary.

	@Value("${db.username}")
	private String dbUsername;

	@Value("${db.password}")
	private String dbPassword;

	@Value("${db.jdbc.driverclass}")
	private String jdbcDriverClass;

	@Value("${db.jdbc.url}")
	private String jdbcUrl;

	@Value("${db.concurrent-call.maxcalls}")
	private int dbConcurrentCallsMaxCalls;

	@Value("${db.concurrent-call.waitsecs}")
	private long dbConcurrentCallsWaitSecs;

    /**
     * This bean must be declared if any beans associated with this Spring
     * container use {@literal @}Value notation to inject property values stored
     * in a property file specified with a {@literal @}PropertySource annotation
     * above. The {@literal @}PropertySource annotation may appear in this
     * configuration class or in another class processed by this container.
     * 
     * Note that each Spring container needs its own
     * PropertySourcesPlaceholderConfigurer bean if {@literal @}Value is used
     * with beans/classes defined in that container. For example, we need this
     * PropertySourcesPlaceholderConfigurerbean if beans created by this
     * container use {@literal @}Value, but WeConfig.java *also* needs its own
     * PropertySourcesPlaceholderConfigurer bean for beans/classes that use
     * {@literal @}Value that are defined in that container, e.g., MVC
     * controller classes/beans.
     * 
     * @return
     */
	@Bean
	public static PropertySourcesPlaceholderConfigurer
			propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

    /**
     * This is an alternate way to set up a PropertySourcesPlaceholderConfigurer
     * bean, but here we specify the property files in the bean definition and
     * not in one or more @PropertySource entries above.
     * 
     * @return
     */
	//    @Bean
	//    public static PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
	//        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
	//        Resource[] resourceLocations = new Resource[] {
	//                new ClassPathResource("propertyfile1.properties"),
	//                new ClassPathResource("propertyfile2.properties")
	//        };
	//        p.setLocations(resourceLocations);
	//        return p;
	//    }

	// Only used with ContractServiceJdbcRaw:
	@Bean
	public AppConfigParams appConfigParams() {
		final AppConfigParams object = new AppConfigParams();
		object.setJdbcDriverClass(this.jdbcDriverClass);
		object.setJdbcUrl(this.jdbcUrl);
		object.setDbUsername(this.dbUsername);
		object.setDbPassword(this.dbPassword);
		object.setConcurrentCalls_permits(this.dbConcurrentCallsMaxCalls);
		object.setConcurrentCalls_timeoutsecs(this.dbConcurrentCallsWaitSecs);
		return object;
	}

	// This is a simple DataSource provided by Spring. Not suitable for
	// production, but can be used for testing.
	//	@Bean
	//	public DataSource dataSource() {
	//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
	//		dataSource.setDriverClassName(this.jdbcDriverClass);
	//		dataSource.setUrl(this.jdbcUrl);
	//		dataSource.setUsername(this.dbUsername);
	//		dataSource.setPassword(this.dbPassword);
	//		return dataSource;
	//	}

	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(this.jdbcDriverClass);
		dataSource.setUrl(this.jdbcUrl);
		dataSource.setUsername(this.dbUsername);
		dataSource.setPassword(this.dbPassword);
		dataSource.setDefaultCatalog("ServerCommon");
		dataSource.setInitialSize(0);
		dataSource.setMaxActive(this.dbConcurrentCallsMaxCalls + 8);	// This is for DBCB v1.4
		//		dataSource.setMaxTotal(this.dbConcurrentCallsMaxCalls + 8);	// This is for DBCB v2.0 (API change)
		dataSource.setMaxIdle(this.dbConcurrentCallsMaxCalls / 2 + 8);
		dataSource.setMinIdle(0);
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(this.dataSource());
	}

	@Bean
	public ContractService contractService() {
		return new ContractServiceJdbcRaw(this.appConfigParams());
		//		return new ContractServiceJdbcSpring(
		//				new SimpleJdbcCall(this.dataSource()).withProcedureName("qp_WSC_ContractCreateTest"),
		//				new SimpleJdbcCall(this.dataSource()).withProcedureName("qp_WSC_ContractCreate"),
		//				new SimpleJdbcCall(this.dataSource()).withProcedureName("qp_WSC_ServiceTest"),
		//				new SimpleJdbcCall(this.dataSource()).withProcedureName("qp_WSC_PaymentMethodGet"),
		//				new SimpleJdbcCall(this.dataSource()).withProcedureName("qp_WSC_PaymentMethodUpdate"));
	}

	@Bean
	public ContractWsSEI contractWs() {
		final ContractWsSEI object = new ContractWs();
		object.setContractService(this.contractService());
		return object;
	}

}
