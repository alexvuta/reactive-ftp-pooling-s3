package com.vuta.reactive.filepooling.aws.ftp;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vuta.reactive.filepooling.aws.beans.FileHolder;
import com.vuta.reactive.filepooling.aws.configuration.FtpClientConfiguration;
import com.vuta.reactive.filepooling.aws.service.FileFlux;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 12 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Service
public class FtpPoolingScheduler {

  @Autowired
  ObjectPool<FTPClient> objectPool;

  @Autowired
  FtpClientConfiguration ftpConfiguration;

  @Autowired
  @Qualifier("ftpFileFlux")
  private FileFlux ftpFileFlux;

  private long lastFileTransferTime = 0;

  org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(FtpPoolingScheduler.class);

  /**
   * Get List of files and push to the flux only the files that are added after
   * the last pool date
   * 
   * @return
   * @throws Exception
   */

  @Scheduled(fixedDelayString = "${vuta.ftp.poolInterval}", initialDelay = 2000)
  public void poolFTP() {
    FTPClient client = null;

    try {
      // get ftp client from FTP pool
      client = objectPool.borrowObject();

      // init array
      FTPFile[] files = null;

      // new files counter (just for simple stats)
      int newFilesCount = 0;

      // get the list of files from FTP
      files = client.mlistDir(ftpConfiguration.getPath());

      // sort files by date desc
      if (files != null) {
        Arrays.sort(files, Comparator.comparing((FTPFile file) -> file.getTimestamp()));
      }

      for (FTPFile file : files) {
        LOGGER.info("name: {} date: {} milis: {} ", file.getName(), file.getTimestamp().getTime().toString(),
            file.getTimestamp().getTimeInMillis());
        // push into sink only the files that are added between after last file
        // transfered time
        if (file.getTimestamp().getTimeInMillis() > lastFileTransferTime) {
          ftpFileFlux.onNewItem(mapToFileHolder(file));

          newFilesCount++;
        }

      }
      // set the last file timestamp as last pooling date for the nex pooling
      lastFileTransferTime = files[files.length - 1].getTimestamp().getTimeInMillis();

      LOGGER.info("FTP POOL: total files [{}] || new files [{}]", files.length, newFilesCount);
      objectPool.returnObject(client);

    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Failed to get FTP client from POOL: {}", e.getMessage());
    }

  }

  private FileHolder mapToFileHolder(FTPFile file) {

    FileHolder fileHolder = new FileHolder();
    fileHolder.setFileName(file.getName());
    fileHolder.setSize(file.getSize());
    fileHolder.setCreationDate(
        OffsetDateTime.ofInstant(Instant.ofEpochSecond(file.getTimestamp().getTimeInMillis()), ZoneId.of("UTC")));
    fileHolder.setDescription("Downloaded at " + OffsetDateTime.now().toString());

    return fileHolder;
  }

}
