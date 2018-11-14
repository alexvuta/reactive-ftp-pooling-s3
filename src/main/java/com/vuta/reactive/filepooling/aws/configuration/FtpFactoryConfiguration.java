package com.vuta.reactive.filepooling.aws.configuration;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vuta.reactive.filepooling.aws.ftp.FtpClientFactory;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 10 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Configuration
public class FtpFactoryConfiguration {

  @Bean
  public ObjectPool<FTPClient> objectPool(FtpClientFactory ftpClientFactory) {

    GenericObjectPoolConfig<FTPClient> config = new GenericObjectPoolConfig<FTPClient>();

    config.setMinIdle(10);
    config.setJmxEnabled(false);
    config.setMaxIdle(15);
    config.setMaxTotal(100);
    config.setTestOnBorrow(true);
    config.setMinEvictableIdleTimeMillis(10000);
    config.setTestWhileIdle(true);
    config.setSoftMinEvictableIdleTimeMillis(4000);
    config.setMaxWaitMillis(2000);
    config.setTimeBetweenEvictionRunsMillis(5000);

    AbandonedConfig abandonedConfig = new AbandonedConfig();
    abandonedConfig.setRemoveAbandonedOnMaintenance(true);
    abandonedConfig.setRemoveAbandonedTimeout(5000);

    return new GenericObjectPool<FTPClient>(ftpClientFactory, config, abandonedConfig);
  }
}
