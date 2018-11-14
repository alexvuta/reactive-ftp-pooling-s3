package com.vuta.reactive.filepooling.aws.service;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vuta.reactive.filepooling.aws.beans.FileHolder;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 11 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Service
public class FTPFileProcessor {
  static Logger logger = LoggerFactory.getLogger(FTPFileProcessor.class);

  @Autowired
  ObjectPool<FTPClient> objectPool;

  @Autowired
  @Qualifier("ftpFileFlux")
  private FileFlux ftpFileFlux;

  @Autowired
  @Qualifier("s3FileFlux")
  private FileFlux s3FileFlux;

  @Value("${vuta.ftp.deleteAfterPool}")
  private Boolean deleteAfterPool;

  int numFiles = 0;

  @PostConstruct
  public void init() {
    subscribe();
  }

  /**
   * Subscribe to FLUX and process each file
   */
  public void subscribe() {

    // construct FLUX, operator chain and finally subscribe:
    // apply distinct filter
    ftpFileFlux.getParallelFlux()
        // publisher will be initiated on another thread
        // log on subscribe event
        .doOnSubscribe(Item -> {
          logger.info("======> Subscribed to FTP FLUX");

        })
        // get file from FTP server
        .map(this::getFile)
        // push file to S3 watcher FLUX
        .map(fileHolder -> s3FileFlux.onNewItem(fileHolder)).map(this::finalizer)

        // on error log to console
        .doOnError(e -> {
          logger.info("======> FTP ERROR: {}", e.getMessage());
          // System.exit(1);
        }).subscribe(file -> {

          logger.info("=== FTP {} ===> completed", numFiles);
          numFiles++;

        });

  }

  /**
   * Purge FTP and free up memory
   * 
   * 
   * @param fileHolder
   * @return
   */
  public FileHolder finalizer(FileHolder fileHolder) {
    FTPClient ftpClient = null;
    try {

      // delete file after pool if enabled
      if (deleteAfterPool) {
        ftpClient = objectPool.borrowObject();
        ftpClient.deleteFile(fileHolder.getFileName());
      }

    } catch (Exception e) {
      try {
        objectPool.invalidateObject(ftpClient);
      } catch (Exception e1) {
        logger.error(e1.getMessage());
      }
      logger.error("Cannot delete FTP file {}: {}", fileHolder.getFileName(), e.getMessage());
    } finally {
      try {
        objectPool.returnObject(ftpClient);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }

    return fileHolder;

  }

  public FileHolder getFile(FileHolder file) {

    InputStream stream = null;
    FTPClient ftpClient = null;

    try {
      ftpClient = objectPool.borrowObject();

      stream = ftpClient.retrieveFileStream(file.getFileName());

      if (stream != null) {
        file.setBytes(IOUtils.toByteArray(stream));
        // close input stream
        //
        stream.close();
        // close file transfer session
        ftpClient.completePendingCommand();

        // logger.info("=== {} ===> Input stream received", file.getFileName());

      } else {
        logger.error("GET FILE ERROR ftp code: {}", ftpClient.getReplyString());
        objectPool.invalidateObject(ftpClient);
      }

    } catch (Exception e) {
      try {
        objectPool.invalidateObject(ftpClient);
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      e.printStackTrace();
      logger.error("GET FILE ERROR: {}", e.getMessage());

    } finally {
      try {
        objectPool.returnObject(ftpClient);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return file;
  }

}
