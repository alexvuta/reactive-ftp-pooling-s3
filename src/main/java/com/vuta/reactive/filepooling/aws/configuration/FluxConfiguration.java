package com.vuta.reactive.filepooling.aws.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vuta.reactive.filepooling.aws.beans.FluxProperties;
import com.vuta.reactive.filepooling.aws.service.FileFlux;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 12 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Configuration
public class FluxConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "vuta.flux.ftp")
  public FluxProperties ftpFluxProperties() {
    return new FluxProperties();
  }

  @Bean
  @ConfigurationProperties(prefix = "vuta.flux.s3")
  public FluxProperties s3FluxProperties() {
    return new FluxProperties();
  }

  @Bean
  FileFlux ftpFileFlux(FluxProperties ftpFluxProperties) {
    return new FileFlux(ftpFluxProperties);
  }

  @Bean
  FileFlux s3FileFlux(FluxProperties s3FluxProperties) {
    return new FileFlux(s3FluxProperties);
  }
}
