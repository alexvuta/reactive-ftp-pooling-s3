package com.vuta.reactive.filepooling.aws.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 10 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "vuta.ftp")
public class FtpClientConfiguration {

  private String server;

  private int port;

  private int poolInterval;

  private String username;

  private String password;

  private String path;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getPoolInterval() {
    return poolInterval;
  }

  public void setPoolInterval(int poolInterval) {
    this.poolInterval = poolInterval;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
