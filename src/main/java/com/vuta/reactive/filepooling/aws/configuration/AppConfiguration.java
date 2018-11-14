package com.vuta.reactive.filepooling.aws.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 10 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Configuration
public class AppConfiguration {

  // provide TaskScheduler bean for FTP Pooling
  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }

}
