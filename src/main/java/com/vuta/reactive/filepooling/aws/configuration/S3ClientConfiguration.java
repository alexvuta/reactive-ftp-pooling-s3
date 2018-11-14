package com.vuta.reactive.filepooling.aws.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 12 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Configuration
public class S3ClientConfiguration {

  @Bean
  public AmazonS3 awsS3Client() {
    return AmazonS3ClientBuilder.defaultClient();
  }

}
