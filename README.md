# red5pro-common

## Common / shared interfaces and adapters

The interfaces and adapters contained here-in are for use in Red5 Pro.

### Networking

Classes to support networking are as follows:

* NetworkManager - provides a public and local / private IP address for the instance
* PortManager - maintains a given port range and supplies available ports to callers

`NetworkManager` selects its address lookup behavior through a topology mode:

* `DEFAULT` - gets the public IP from `https://checkip.amazonaws.com` (overridable with the `ip.check.uri` system property). It infers the local address by opening a UDP socket to Google DNS (`8.8.8.8`).
* `PROPERTIES` - checks `PUBLIC_IP` and `LOCAL_IP` environment variables first, then the `public.ip` and `local.ip` JVM system properties, then the `force.public.ip` and `force.local.ip` values in `network.properties`.
* `AWS` - reads public and local addresses from the EC2 instance metadata service. If a public address lookup returns no value, it falls back to the configured IP check service.

### Requirements

* JDK 25 or newer (the project compiles with `maven.compiler.release=25`)
* Maven 3.9 or newer

The build enforces both minimums. If a local `~/.mavenrc` sets `JAVA_HOME` to an older JDK, Maven will fail with `release version 25 not supported`; point it at a JDK 25 install or bypass it with `MAVEN_SKIP_RC=1`.

### Building

```sh
# build the jar, sources and javadoc (tests are skipped by default)
mvn clean package

# build and run the tests
mvn clean package -DskipTests=false
```

Some network tests read `src/test/resources/conf/network.properties`, and the AWS topology test adapts to whether the EC2 metadata service is reachable.

### Maven

Including in your maven pom is as easy as adding this xml to your dependencies

```xml
<dependency>
    <groupId>com.red5pro</groupId>
    <artifactId>red5pro-common</artifactId>
    <version>17.0.0.0</version>
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

[Directory Listing](https://red5pro.jfrog.io/ui/native/ext-release-local/com/red5pro/red5pro-common)

#### Example Demo Application

A simple example of using common in your projects is available [here](https://github.com/red5pro/red5pro-server-examples/tree/develop/common-demo).

#### Deploy to Artifactory

**For Internal Use Only**

`mvn clean deploy`

This assumes you have your maven `settings.xml` properly configured of course.
