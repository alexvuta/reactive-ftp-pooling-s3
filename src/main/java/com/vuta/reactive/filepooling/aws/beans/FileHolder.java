package com.vuta.reactive.filepooling.aws.beans;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 12 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
public class FileHolder {

  private String fileName;
  private OffsetDateTime creationDate;
  private byte[] bytes;
  private String description;
  private Long size;

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public OffsetDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "FileHolder [fileName=" + fileName + ", creationDate=" + creationDate + ", description=" + description + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, fileName, size);
  }

}
