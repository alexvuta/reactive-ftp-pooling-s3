package com.vuta.reactive.filepooling.aws.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.vuta.reactive.filepooling.aws.beans.FileHolder;
import com.vuta.reactive.filepooling.aws.configuration.S3ClientConfiguration;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 11 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Service
public class S3FileProcessor {

  Logger logger = LoggerFactory.getLogger(S3FileProcessor.class);

  @Value("${vuta.aws.s3.bucketName}")
  private String BUCKET_NAME;

  @Autowired
  S3ClientConfiguration s3Client;

  @Autowired
  private FileFlux s3FileFlux;

  private int numFiles = 0;

  @PostConstruct
  public void init() {
    // subscribe the flux
    subscribe();
  }

  public void subscribe() {
    s3FileFlux.getParallelFlux().doOnSubscribe(Item -> {
      logger.info("======> Subscribed to S3 FLUX");

    }).map(this::saveFile).map(this::finalizer)
        // on error log to console
        .doOnError(e -> {
          logger.error("======> S3 ERROR: {}", e.getMessage());
        }).subscribe(value -> {
          numFiles++;
          logger.info("=== S3 {} ===> completed", numFiles);

        });
  }

  /**
   * Save a FileHolder to S3 AWS Bucket
   * 
   * @param fileHolder
   * @return
   */
  public FileHolder saveFile(FileHolder fileHolder) {

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(fileHolder.getSize());
    InputStream inputStream = null;

    try {
      inputStream = new ByteArrayInputStream(fileHolder.getBytes());
      s3Client.awsS3Client().putObject(BUCKET_NAME, fileHolder.getFileName(), inputStream, objectMetadata);
    } catch (Exception e) {
      logger.error("Failed to save file {} on AWS S3. Error: {}", fileHolder.getFileName(), e.getMessage());
    } finally {
      try {
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return fileHolder;
  }

  /**
   * Free Up memory
   * 
   * @param file
   * @return
   */
  private FileHolder finalizer(FileHolder file) {
    file.setBytes(null);
    return file;
  }

}
