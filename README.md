# red5pro-common

## Common / shared interfaces and adapters

The interfaces and adapters contained here-in are for use in Red5 Pro.

### Networking

Classes to support networking are as follows:

* NetworkManager - provides a public and local / private IP address for the instance
* PortManager - maintains a given port range and supplies available ports to callers

The NetworkManager uses topology modes to determine the logic to utilize when locating IP addresses. Topology modes currently consist of
* DEFAULT - uses the checkip service from Amazon for the public IP and Google dns for the local IP 
* PROPERTIES - uses environment property of `PUBLIC_IP` first, system property of `public.ip`, and the `network.properties` file the public IP. For local IP the environment property is `LOCAL_IP`, system property is `local.ip`, and the same goes for the file.
* AWS - uses the instance local calls for meta-data

### Maven

Including in your maven pom is as easy as adding this xml to your dependencies

```xml
<dependency>
    <groupId>com.red5pro</groupId>
    <artifactId>red5pro-common</artifactId>
    <version>9.2.11</version>
    <scope>provided</scope>
</dependency>
```

#### Anonymous Access

For *public* access to the artifacts, you'll need to modify your Maven `.m2/settings.xml` or use the example below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd" xmlns="http://maven.apache.org/SETTINGS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <profiles>
    <profile>
      <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>libs-release</name>
          <url>https://red5pro.jfrog.io/red5pro/libs-release</url>
        </repository>
        <repository>
          <snapshots />
          <id>snapshots</id>
          <name>libs-snapshot</name>
          <url>https://red5pro.jfrog.io/red5pro/libs-snapshot</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>plugins-release</name>
          <url>https://red5pro.jfrog.io/red5pro/plugins-release</url>
        </pluginRepository>
        <pluginRepository>
          <snapshots />
          <id>snapshots</id>
          <name>plugins-release</name>
          <url>https://red5pro.jfrog.io/red5pro/plugins-release</url>
        </pluginRepository>
      </pluginRepositories>
      <id>artifactory</id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>artifactory</activeProfile>
  </activeProfiles>
</settings>
```
The `.m2` directory is located within your home directory, unless otherwise configured.

#### Example Demo Application

A simple example of using common in your projects is available [here](https://github.com/red5pro/red5pro-server-examples/tree/develop/common-demo).


#### Deploy to Artifactory

**For Internal Use Only**

`mvn clean deploy`

This assumes you have your maven `settings.xml` properly configured of course.
