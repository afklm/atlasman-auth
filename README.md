Atlasman Authentication Server
==============================

Building
--------

The configuration for this project is stored in a private repository to hide sensitive information.
To properly build this project create a private (or public) repository and include the following:

- application.yml (see sample below)
- logback.xml (see sample below)
- a java keystore

Store this in a repository under the following convention

atlasman-authorization-server/**

To run the build there are two options.

Option 1, provide the parameters manualy:

`
gradle build -Prepository={repository url} -Pusername={repo username} -Ppassword={repo password}
`

Option 2, include a gradle.properties file which contains the project parameters:
```
- repository={repository url}
- username={repo username}
- password={repo password}
```

The build will clone the repository and extract the configuration to include it in the artifact.

To run the server locally via gradle use:

`gradle bootRun` (enable the development profile by default)

or to run the artifact (providing the spring profile manually):

`java -jar build/libs/atlasman-auth-0.0.1.472e0ed.jar --spring.profiles.active=development`

Introduction
------------

For now the auth server stores tokens in memory and requires a keypair to sign the json web tokens. The implementation will probably change in the future
but for now this is it.

Available integrations:

- Atlassian Crowd

application.yml sample
----------------------

```
oauth2:
  client:
    id: {client-id}
    secret: {client-secret}
  token:
    validityInSeconds: {token-validity-in-seconds}
    refreshValidityInSeconds: {refresh-token-validity-in-seconds}
  keystore:
    alias: {keystore-alias}
    storePass: {keystore-storepass}
    classpath: {keystore-path}
atlassian:
  crowd:
    name: {crowd-username}
    password: {crowd-password}
    validationinterval: {crowd-validation-interval}
    administators: {administrators}
-----------------------------------
spring:
  profiles: development
atlassian:
  crowd:
    url: {crowd-url}
--------------------
spring:
  profiles: production
atlassian:
  crowd:
    url: {crowd-url}
```

basic logback.xml example
-------------------------

```
<configuration>

    <include resource="org/springframework/boot/logging/logback/defaults.xml" />

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [ %thread ] > %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.afkl" level="debug"/>

    <root level="info">
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>
```
