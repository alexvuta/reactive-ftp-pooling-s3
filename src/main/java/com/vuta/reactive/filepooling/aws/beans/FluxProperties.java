package com.vuta.reactive.filepooling.aws.beans;

import org.springframework.context.annotation.Configuration;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 12 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@Configuration
public class FluxProperties {

  private Integer concurrency;

  private Integer backpressureSize;

  private Boolean distinctionEnabled;

  public int getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(Integer concurrency) {
    this.concurrency = concurrency;
  }

  public Integer getBackpressureSize() {
    return backpressureSize;
  }

  public void setBackpressureSize(Integer backpressureSize) {
    this.backpressureSize = backpressureSize;
  }

  public Boolean isDistinctionEnabled() {
    return distinctionEnabled;
  }

  public void setDistinctionEnabled(Boolean distinctionEnabled) {
    this.distinctionEnabled = distinctionEnabled;
  }

}
