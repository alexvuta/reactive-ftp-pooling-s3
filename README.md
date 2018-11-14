# reactive-ftp-pooling-s3
Synchronize  a FTP server with a Amazon S3 bucket using Spring Reactor

## Proposes of this project

The scope of this project is the synchronization of a FTP server with a AWS S3 bucket; 
A thread was delegated to execute at specified intervals a FTP pool to recover the new files from FTP;
The files are pushed into a Flux, with help of some operators it will recover the InputStream from FTP, to be pushed after into another Flux for the S3 client;

Synchronize  a FTP server with a Amazon S3 bucket using Spring Reactor
