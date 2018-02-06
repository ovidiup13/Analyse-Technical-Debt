# Technical Debt Analyser

A tool that analyses bugs and code violations in the history of project revisions.

## Dependencies

The project will require the following dependencies to run:

* Java 1.8
* Gradle 4.5
* Maven 3

## Gradle dependencies

Spring Boot (1.5.x should work):

* org.springframework.boot:spring-boot-starter
* org.springframework.boot:spring-boot-starter-test
* org.springframework.boot:spring-boot-starter-data-mongodb
* org.springframework.boot:spring-boot-starter-batch
* org.springframework.boot:spring-boot-gradle-plugin

Transitive:

* org.hibernate:hibernate-validator:5.3.6.Final

For Jira access:

* com.atlassian.fugue:fugue:2.6.1
* com.atlassian.jira:jira-rest-java-client-parent:4.0.0
* com.atlassian.jira:jira-rest-java-client-api:4.0.0
* com.atlassian.jira:jira-rest-java-client-core:4.0.0

For Git bindings:

* org.eclipse.jgit:org.eclipse.jgit:4.9.0.201710071750-r
