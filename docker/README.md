docker/docker-compose
==========

This is a selection of docker images and docker-compose setups for developing/deploying Sakai.

Layout
======

tomcat - This is an Docker image ready for a copy of Sakai. It containts Java and Tomcat. This image 
available from the Docker Hub as https://hub.docker.com/r/buckett/sakai-tomcat/
sakai - This is the main docker and docker-compose folder
apache - This ia a copy of apache that has Shibboleth enabled for it.

Setup
=====

If you are on Linux then you should install docker and docker-compose, if you're on Windows or Mac then 
you should install the Docker Toolbox. If you're using the Docker Toolbox you then need to create you're 
virtual machine and because Sakai has large memory requirements you should increase the default resources
allocated to it as long as your host has sufficient resources. This allocates 2GB and 2 CPUs.

```
export VIRTUALBOX_CPU_COUNT="2"
export VIRTUALBOX_MEMORY_SIZE="2048"
```

then create the VM and setup docker to use this VM:
```
docker-machine create -d virtualbox dev
eval $(docker-machine env dev)
```

Development
===========

If you're on Windows of Mac make sure that you have your Sakai deployment inside you're home folder as by default docker machine will only make those folders available to a docker container.

With your source checkout of Sakai build it and deploy it into the folder `sakai/tomcat`

```
cd sakai/checkout
mvn install directory:directory-of sakai:deploy 
```

Once it's deployed bring up the application and supporting services:
```
cd sakai
docker-compose up
```

This will startup a copy of MySQL and Tomcat (running Sakai). They will be configured to
use each other, to stop them all just ^C. 
In general use you will probably want to start them up in the background:
```
docker-compose up -d
```
You can then look at the logs with
```
docker-compose logs
```
If you build and deploy any webapps they should be picked up, but if you want to just restart tomcat
after making changes to the build you can use:
```
docker-compose restart app
```
If you wish to test out config changes they should go in `sakai/local.properties` but
if you wish to include them in general builds they should get put in `sakai/placeholder.properties`
If someone updates the image that is used in this build then you will need to pull down the newer one. This can be done with
```
docker-compose pull
```
which will check for a newer copy of the images used.


Production
==========

With your source checkout of Sakai build it it and deploy it into the foler `sakai/tomcat`
Once it's deployed you need to build the docker image:
```
docker build -t username/sakai sakai
```
Then to send the image to the hosting team push the image to the docker registry
```
docker push username/sakai
```

Sakai Configuration
===================

To configure Sakai we have the following files, the order listed is the order they are read with values being overritten with newer ones.:

 - `sakai/placeholder.properties` This contains 99% of out config changes for Sakai, it's bundled into the container for production builds.
 - sakai.properties This is where the container linking automatically happens if you are using `docker-compose`. It's inside the container and shouldn't be touched.
 - `sakai/local.properties` Here you can put local configuration for configuration you are testing out (before putting in placeholder.properties). In production this is also where the DB configuration is put.
 - security.properties In production this contains the passwords for the services we connect to (DB, Turnitin, etc)


YourKit Profiling
=================

We include the profiling library in our build but by default we don't startup tomcat with this agent enabled. To start Sakai with profiling enabled change the command that is run from `/opt/tomcat/bin/catalina.sh run` to `/opt/tomcat/bin/startup_with_yjp.sh`
The profiler isn't enabled for the first 100 seconds to give Sakai time to startup before slowing it down. By default it will listen on port 10001.

Producing a heap dump
=====================

Becuase the JVM is now running inside a container you can't use the host's
tools to produce a heap dump. On a modern deployment of docker this isn't a
problem because you can use `docker exec` to get a shell inside the container
and from there use the standard process to to get a heap dump. The only small
issue is how you copy the heap dump out.

If you have a connection setup so that you can connect to the JVM with JMX 
and the JVM is still responding well to JMX. You eed to connect to the JVM with
JMX (normally you will do this by an ssh tunnel
`ssh -L5400:localhost:5400 {server}` and then startup `jvisualvm`, install the
MBean plugin into `jvisualvm` (Tools -> Plugins). Then connect to
localhost:5400. Once connected look in the MBeans tab and find the bean
`com.sun.management:type=HotSpotDiagnostic`, one of the operations available is
`dumpHeap()` which takes an argument of the file and true. Normally I put the
heap dump in /tmp/sakai.bin. It should take a little while to run but once
done there should be a file on the filesystem that can be got at.

If these methods don't work then an alternative is to just use GDB to produce
the heap dump from the host OS and then use `jmap` on another system to
perform the dump. 

    gdb –pid=16837
    (gdb) gcore /tmp/jvm.core
    Saved corefile /tmp/jvm.core
    (gdb) detach
    (gdb) quit

This gives you the file which can then be processed by `jmap`. However if you
are not on linux this needs to be done inside docker (or other linux
environment) as the JDK needs to match the one that was running when the
problems occurred. This assumes I've put the heap dump in a folder called 
`cores` relative to my current location:

    docker run --rm -it -v $(pwd)/cores/:/cores -e SAKAI_USER=root oxit/weblearn /bin/bash

Then inside the container run:

    cd /cores
    /opt/jdk/jdk-8u121/bin/jmap -dump:format=b,file=jvm.hprof /opt/jdk/jdk-8u121/bin/java java.core

And you should end up with a heap dump in the `cores` folder.

Errors
======

If you get an error message when you try to bring up the containers along the lines of:
```
docker-compose up
Recreating sakai_db_1...
Creating sakai_app_1...
Pulling image oxit/sakai:11.x...
Pulling repository docker.io/oxit/sakai
[..snipped..]
compose.progress_stream.StreamOutputError: Error: image oxit/sakai:11.x not found
```
then you need to login to the docker hub to get the image. You can do this with:
```
docker login
```
and then just attempt to start it up again.


Notes
=====

Generally put stuff in /opt/* and put scripts that are used by docker in /opt/scripts (should be more unixy and change to /opt/bin)

If the files don't seem to be appearing inside the docker container make sure your working inside your home folder.

