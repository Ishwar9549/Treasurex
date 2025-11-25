package com.treasurex.login_service.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
public class HibernateConfig {

	private final DataSource dataSource;

	public HibernateConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
	LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
		lsfb.setDataSource(dataSource);
		lsfb.setPackagesToScan("com.treasurex.login_service.entity");

		Properties props = new Properties();
		props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		// props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		props.put("hibernate.hbm2ddl.auto", "update");
		props.put("hibernate.show_sql", "true");
		lsfb.setHibernateProperties(props);

		return lsfb;
	}

	@Bean
	@Primary // ✅ add this to make Spring prefer this bean
	SessionFactory getSessionFactory(LocalSessionFactoryBean lsfb) {
		return lsfb.getObject();
	}
}
