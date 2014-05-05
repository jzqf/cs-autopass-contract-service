package com.qfree.cs.autopass.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.qfree.cs.autopass.service.SpringInjectionTest;
import com.qfree.cs.autopass.ws.ContractService;
import com.qfree.cs.autopass.ws.ContractServiceSEI;

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

	private static final Logger logger = LoggerFactory.getLogger(RootConfig.class);

	@Value("${db.server}")
	private String dbServer;

	@Value("${db.port}")
	private String dbPort;

	@Value("${db.username}")
	private String dbUsername;

	@Value("${db.password}")
	private String dbPassword;

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

	@Bean
	public SpringInjectionTest springInjectionTest() {
		final SpringInjectionTest s = new SpringInjectionTest();

		logger.info("this.dbServer = {}", this.dbServer);	// this works

		s.setDbServer(this.dbServer);
		return s;
	}

	@Bean
	public ContractServiceSEI contractService() {
		final ContractServiceSEI c = new ContractService();
		//		c.setDbServer(this.dbServer);
		return c;
	}

}
