package com.vuta.reactive.filepooling.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Vuta Alexandru https://vuta-alexandru.com Created at 10 nov. 2018
 *         contact email: verso.930[at]gmail.com
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan
public class FilePoolerSpringBootApp {

  public static void main(String[] args) {

    SpringApplication.run(FilePoolerSpringBootApp.class, args);

  }

}
