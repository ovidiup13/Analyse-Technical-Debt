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

For bug/code analysis:

* FindBugs/Spotbugs 3.1.1

## License

MIT License

Copyright (c) 2018 Ovidiu Popoviciu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
