package com.xperia.xpense_tracker.config.datasource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.xperia.xpense_tracker.repository.tracker",
        entityManagerFactoryRef = "trackerEntityManagerFactory",
        transactionManagerRef = "trackerTransactionManager"
)
public class XpenseTrackerDatasourceConfig {

    @Primary
    @Bean(name = "xpenseDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.tracker")
    public DataSource xpenseDatasource(){
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "trackerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean trackerEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                              @Qualifier("xpenseDatasource") DataSource dataSource){

        return builder
                .dataSource(dataSource)
                .packages("com.xperia.xpense_tracker.models.entities.tracker")
                .persistenceUnit("tracker")
                .build();
    }

    @Primary
    @Bean(name = "trackerTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("trackerEntityManagerFactory") EntityManagerFactory trackerEntityManagerFactory) {
        return new JpaTransactionManager(trackerEntityManagerFactory);
    }
}
