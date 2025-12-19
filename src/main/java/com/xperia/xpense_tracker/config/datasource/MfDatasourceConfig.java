package com.xperia.xpense_tracker.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.xperia.repository.mf",
        entityManagerFactoryRef = "mfEntityManagerFactory",
        transactionManagerRef = "mfTransactionManager"
)
public class MfDatasourceConfig {

    @Bean(name = "mfDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mf.hikari")
    public DataSource mfDatasource(){
        return DataSourceBuilder
                .create().type(HikariDataSource.class)
                .build();
    }


    @Bean(name = "mfEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mfEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                         @Qualifier("mfDataSource") DataSource dataSource){

        return builder
                .dataSource(dataSource)
                .packages("org.xperia.entities.mf")
                .persistenceUnit("mf")
                .build();
    }

    @Bean(name = "mfTransactionManager")
    public PlatformTransactionManager mfTransactionManager(
            @Qualifier("mfEntityManagerFactory") EntityManagerFactory mfEntityManagerFactory){
        return new JpaTransactionManager(mfEntityManagerFactory);
    }
}
