[![Build Status](https://travis-ci.org/assertthat/assertthat-bdd-maven-plugin.svg?branch=master)](https://travis-ci.org/assertthat/assertthat-bdd-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.assertthat.plugins/assertthat-bdd-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.assertthat.plugins/assertthat-bdd-maven-plugin)

## Description

Maven plugin for interaction with [AssertThat BDD Jira plugin](https://marketplace.atlassian.com/apps/1219033/assertthat-bdd-test-management-in-jira?hosting=cloud&tab=overview).

Main features are:

- Download feature files before test run
- Filter features to download based on mode (automated/manual/both), or/and JQL
- Upload cucumber json after the run to AsserTthat Jira plugin

## Installation

Full plugin configuration below, optional properties can be omitted

```xml
 <plugin>
    <groupId>com.assertthat.plugins</groupId>
    <artifactId>assertthat-bdd-maven-plugin</artifactId>
    <version>1.6.2</version>
    <configuration>
        <projectId>
            <!--Jira project id e.g. 10001-->
        </projectId>
        <!--Optional can be supplied as environment variable ASSERTTHAT_ACCESS_KEY -->
        <accessKey>
            <!-- ASSERTTHAT_ACCESS_KEY -->
        </accessKey>
        <!--Optional can be supplied as environment variable ASSERTTHAT_SECRET_KEY -->
        <secretKey>
            <!-- ASSERTTHAT_SECRET_KEY -->
        </secretKey>
        <!-- Used for jira server integration only. If using cloud remove this option-->
        <jiraServerUrl>https://mycompanyjira.com</jiraServerUrl>
    </configuration>
    <executions>
        <execution>
            <configuration>
                <!--Optional - default ./features-->
                <outputFolder>src/test/resources/com/assertthat/features</outputFolder>
                <!--Optional - all features downloaded by default - should be a valid JQL-->
                <jql>project = XX AND key in ('XXX-1')</jql>
                <!--Optional - default automated (can be one of: manual/automated/both)-->
                <mode>automated</mode>
                <!--Optional - tag expression filter for scenarios. More on tag expressions https://cucumber.io/docs/cucumber/api/#tag-expressions-->
                <tags>(@smoke or @ui) and (not @slow)</tags>
                <!--Optional - prepend ordinal to feature name (default is true)-->                
                <numbered>false</numbered>
            </configuration>
            <id>features</id>
            <goals>
                <goal>features</goal>
            </goals>
            <phase>pre-integration-test</phase>
        </execution>
        <execution>
            <id>report</id>
            <goals>
                <goal>report</goal>
            </goals>
            <phase>post-integration-test</phase>
            <configuration>
                <!--Optional - default ./report-->
                <jsonReportFolder>target/report/surefire-reports/cucumber/</jsonReportFolder>
                <!--Optional - default - **/*.json -->
                <jsonReportIncludePattern>**/cucumber.json</jsonReportIncludePattern>
                <!--Optional - default cucumber (can be one of: cucumber/karate)-->
                <type>cucumber</type>
                <!--Optional - default 'Test run Test run dd MMM yyyy HH:mm:ss'-->
                <runName>Custom test run name</runName>
                <!--Optional - Run metadata -->
               <metadata>{"env ":"uat ","build":"456"}</metadata>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Usage
We recommend ruuning cucumber tests on `integration-test` phase as 

- download features is running on `pre-integration-test` phase 
-  report submission on `post-integration-test`

### Example project 

Refer to example project [assertthat-bdd-maven-example](https://github.com/assertthat/assertthat-bdd-maven-example)
