package com.vuta.reactive.filepooling.aws.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vuta.reactive.filepooling.aws.configuration.FtpClientConfiguration;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 12 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Component
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {

  @Autowired
  FtpClientConfiguration ftpClientConfiguration;

  Logger logger = LoggerFactory.getLogger(FtpClientFactory.class);

  @Override
  public FTPClient create() throws Exception {

    FTPClient client = new FTPClient();

    client.connect(ftpClientConfiguration.getServer(), ftpClientConfiguration.getPort());
    int reply = client.getReplyCode();

    // check connection reply
    if (!FTPReply.isPositiveCompletion(reply)) {
      client.disconnect();
      throw new IOException("Exception in connecting to FTP Server");
    }

    client.enterLocalPassiveMode();
    // client.enterLocalActiveMode();
    client.setFileType(FTP.BINARY_FILE_TYPE);
    // client.setControlKeepAliveTimeout(2);

    client.login(ftpClientConfiguration.getUsername(), ftpClientConfiguration.getPassword());

    logger.debug("New FtpClient created");

    return client;
  }

  @Override
  public PooledObject<FTPClient> wrap(FTPClient obj) {
    return new DefaultPooledObject<FTPClient>(obj);
  }

  @Override
  public boolean validateObject(PooledObject<FTPClient> p) {

    FTPClient c = p.getObject();
    boolean valid = true;

    // send no operation command and check return status
    try {
      c.sendNoOp();
    } catch (IOException e1) {
      valid = false;
    }

    // check response code for negative completion
    if (!FTPReply.isPositiveCompletion(c.getReplyCode())) {
      valid = false;
    }

    // log on debug
    if (!valid) {
      logger.debug("FTP INVALID: STATE {} ACTIVE {} IDLE {}", p.getState(), p.getActiveTimeMillis(),
          p.getIdleTimeMillis());
    }
    return valid;
  }

  @Override
  public void destroyObject(PooledObject<FTPClient> p) {

    FTPClient c = p.getObject();

    try {
      c.disconnect();
      logger.debug("FtpClient destroyed ==  state: {} activeTime: {} idleTime {} lastReplyCode: {}", p.getState(),
          p.getActiveTimeMillis(), p.getIdleTimeMillis(), c.getReplyCode());
    } catch (Exception e) {
      logger.error("FtpClient destory failed");
    }

  }

}