# reactive-ftp-pooling-s3
Synchronize  a FTP server with a Amazon S3 bucket using Spring Reactor

## Purpose of this project

The scope of this project is the synchronization of a FTP server with a AWS S3 bucket; 
A thread was delegated to execute at specified intervals a FTP pool to recover the new files from FTP;
The files are pushed into a Flux, with help of some operators it will recover the InputStream from FTP, to be pushed after into another Flux for the S3 client;

Synchronize  a FTP server with a Amazon S3 bucket using Spring Reactor
<p align="center">
<img src="https://drive.google.com/uc?authuser=0&id=1M-AR8IZugyHzZn4Nj84o5whkZcJZr7Ub&export=download"/>
</p>

Flow:
- Get Files from FTP
- SORT by date
- Push only the new files added after the last pool date, to the FLUX
- File bytes are recovered from FTP in async mode
- Downloaded files are pushed to S3 Flux
- Files are uploaded to S3 bucket


## FTP

A POOL of FTP clients objects was implement using APACHE Common object pool; the configuration for pool is available under `com.vuta.reactive.filepooling.aws.configuration.FtpFactoryConfiguration.java`:
```
 GenericObjectPoolConfig<FTPClient> config = new GenericObjectPoolConfig<FTPClient>();

    config.setMinIdle(10);
    config.setJmxEnabled(false);
    config.setMaxIdle(15);
    config.setMaxTotal(100);
    config.setTestOnBorrow(true);
    config.setMinEvictableIdleTimeMillis(10000);
    config.setTestWhileIdle(true);
    config.setSoftMinEvictableIdleTimeMillis(4000);
    config.setMaxWaitMillis(2000);
    config.setTimeBetweenEvictionRunsMillis(5000);

    AbandonedConfig abandonedConfig = new AbandonedConfig();
    abandonedConfig.setRemoveAbandonedOnMaintenance(true);
    abandonedConfig.setRemoveAbandonedTimeout(5000);
```
here you need to adjust the number of connection in function of you ftp server capacity.
#### NOTE: FTP Server must support MLSD command for complete timestamp details when list files (check my PROFTPD compiling guide under linux with mode_facts that provide MLSD command support [HERE](https://gist.github.com/alexvuta/5d1b3223fcb0453d8cc34c12552c21fe)

## SPRING REACTOR

Spring reactor bring in the game the async processing and operator chaining. Files are listed by the PoolerScheduler service and pushed into a PARALEL FLUX. The flux is subscribed a given number of times (parameters in spring application.yml file) on another thread.
The files are recovered as `memory bytes` and pushed after into another PARALEL FLUX that will save the file on AMAZON S3 BUCKET.
- A backpressure (the ability for the consumer to signal the producer that the rate of emission is too high) was implemented with DROP_OLDEST strategy
- Files distinction was implemented using a hashcode
- Configurable number of paralel plux subscription

## Configuration File

```
vuta:
  ftp:
    username: vuta
    password: vuta
    server: 00.00.00.00
    port: 21
    path: /
    poolInterval: 100000 # value in milliseconds
    deleteAfterPool: false
  aws:
    s3:
      bucketName: pooling-files
  flux:
    s3:
      concurrency: 10
      backpressureSize: 1000
      distinctionEnabled: true 
    ftp:
      concurrency: 30
      backpressureSize: 100000
      distinctionEnabled: true 
spring:
  jmx:
    enabled: false
  main:
    web-environment: false
    web-application-type: none
#debug: true
#trace: true
logging:
  file: pooler-trace-log.log
```

## Performance tests

Application lunched on local machine, ftp server deployed on AWS EC2 on eu-west-1 so the network latency are considered.
TEST 10 000 .json files generated on FTP:
- 50 files/second downloaded from FTP
- 10 files/second saved to S3 BUCKET (the S3 API is limited for preventing atacks)
