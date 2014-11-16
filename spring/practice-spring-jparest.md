# Practice: Accessing JPA Data with REST
Based on: http://spring.io/guides/gs/accessing-data-rest/

This step-by-step guide walks you through the process of building an application that accesses relational JPA data through a [hypermedia-based](http://spring.io/guides/gs/rest-hateoas) [RESTful](http://spring.io/understanding/REST) front end.

## What you’ll build

You will build an application that lets you create and retrieve `Person` objects stored in a database using *Spring Data REST*.

## What you’ll need

- About 15 minutes
- JDK 1.6 or later
- Maven 3.0+
- A favorite text editor or IDE
- A REST client (e.g. `curl`) or a web-browser with the right extension/add-on.


> In this guide you will find menu navigation specific to the [Spring Tool Suite (STS)](http://spring.io/tools/sts).  
For the REST stuff we are using [Mozilla Firefox](https://www.mozilla.org/en-US/firefox/new/) with the [RESTClient](https://addons.mozilla.org/en-US/firefox/addon/restclient/) add-on.

### Create a project

Start by creating a new (simple) Maven project in STS:
  1. From the menu `File` > `New` > `Project...`

  2. Select `Maven` > `Maven Project`

  3. Click `Next` (or use the shortcut: `Alt + Shift + N` > `Maven Project`).

  4. Check the `Create a simple project` check-box.

  5. Click `Next`.

  6. Input the following values in the `Artifact` section:

  **Group Id:** `cebence.practice.spring`  
  **Artifact Id:** `practice-spring-jparest`  
  **Version:** `1.0`  
  **Name:** `Spring Data REST practice`

  We also need these in the `Parent Project` section:

  **Group Id:** `org.springframework.boot`  
  **Artifact Id:** `spring-boot-starter-parent`  
  **Version:** `1.1.9.RELEASE` (latest at the time of writing)

  7. Click `Finish`.

Note that we declared our project as a *child* project of `spring-boot-starter-parent` although we are not creating a hierarchy of projects.

> In case Maven can't find Spring Boot artifacts on default Maven Central repository you will need to adding these repositories into `pom.xml`:
```xml
<repositories>
  <repository>
    <id>spring-releases</id>
    <url>http://repo.spring.io/libs-release</url>
  </repository>
</repositories>
<pluginRepositories>
  <pluginRepository>
    <id>spring-releases</id>
    <url>http://repo.spring.io/libs-release</url>
  </pluginRepository>
</pluginRepositories>
```

For Maven needs help from Spring Boot to create a runnable JAR so we need to add Spring Boot Maven plug-in:

1. Open the `pom.xml` file.

2. Add the following before the `</project>`:

  ```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
  ```

3. Save the file.

To test *it works* we'll build and test the project with `Run` > `Run As` > `8 Maven Test`. You will recognize a successful build by this ending of Maven's output:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.460s
[INFO] Finished at: Wed Nov 15 09:57:12 CET 2014
[INFO] Final Memory: 7M/16M
[INFO] ------------------------------------------------------------------------
```

### Add project dependencies

Righ-click on `pom.xml` > `Maven` > `Add Dependency` and input the following:

**Group Id:** `org.springframework.boot`  
**Artifact Id:** `spring-boot-starter-data-jpa`

You can leave the *Version* empty as it will be copied from the parent project.

Again for the REST:

**Group Id:** `org.springframework.boot`  
**Artifact Id:** `spring-boot-starter-data-rest`

And again for the database:

**Group Id:** `com.h2database`  
**Artifact Id:** `h2`

> Note this can also be done by manually adding the following to `pom.xml`:
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
  <dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
  </dependency>
</dependencies>
```

### Add UTF-8 support to our Maven project

Add this into `pom.xml`:

```xml
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

### Create a resource representation class

Create a new resource representation (i.e. domain object or entity) class to represent a person.

1. From the menu `File` > `New` > `Class` and input the following:

  **Source folder:** `practice-spring-jparest/src/main/java`  
  **Package:** `practice.spring.jparest`  
  **Name:** `Person`

2. Click `Finish`.

3. Replace the content with the following:

  ```java
  package practice.spring.jparest;

  import javax.persistence.Entity;
  import javax.persistence.GeneratedValue;
  import javax.persistence.GenerationType;
  import javax.persistence.Id;

  @Entity
  public class Person {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String firstName;
    private String lastName;
  }
  ```

4. Generate getters and setters (`Source` > `Generate Getters and Setters...` > `Select All` > `OK`).

5. Save the file.

### Create a repository interface

1. From the menu `File` > `New` > `Interface` and input the following:

  **Source folder:** `practice-spring-jparest/src/main/java`  
  **Package:** `practice.spring.jparest`  
  **Name:** `PersonRepository`

2. Click `Add...`, type in `PagingAndSortingRepository`, click `OK`.

3. Click `Finish`.

4. Replace the content with the following:

  ```java
  package practice.spring.jparest;

  import java.util.List;
  import org.springframework.data.repository.PagingAndSortingRepository;
  import org.springframework.data.repository.query.Param;
  import org.springframework.data.rest.core.annotation.RepositoryRestResource;

  @RepositoryRestResource(collectionResourceRel = "people", path = "people")
  public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {
    List<Person> findByLastName(@Param("name") String lastName);
  }
  ```

5. Save the file.

> **Note:** `@RepositoryRestResource` is just used to change the export details, i.e. to rename default path from `/persons` to `/people`.

### Create an Application class

1. From the menu `File` > `New` > `Class` and input the following:

  **Source folder:** `practice-spring-jparest/src/main/java`  
  **Package:** `practice.spring.jparest`  
  **Name:** `Application`

2. Click `Finish`.

3. Replace the content with the following:

  ```java
  package practice.spring.jparest;

  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.context.annotation.Import;
  import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
  import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

  @Configuration
  @EnableAutoConfiguration
  @EnableJpaRepositories
  @Import(RepositoryRestMvcConfiguration.class)
  public class Application {
    public static void main(String[] args) {
      SpringApplication.run(Application.class);
    }
  }
  ```

4. Save the file.

### Build an executable JAR

In order to make the JAR executable we need to tell Spring Boot
which class to start, i.e. point it to our `Application` class.

Per [Spring documentation](http://docs.spring.io/autorepo/docs/spring-boot/1.1.8.RELEASE/maven-plugin/usage.html) we need add the `start-class` property to our POM, so open the `pom.xml`, switch to the `Overview` tab (tabs are at the bottom), and in the `Properties` section click `Create...` and input the following:

  **Name:** `start-class`  
  **Value:** `practice.spring.jparest.Application`

> Advanced Maven users can update `pom.xml` directly and add:
```xml
<properties>
  <start-class>practice.spring.jparest.Application</start-class>
</properties>
```

To build the JAR do the following:

1. From the menu `Run` > `Run As` > `3 Maven build` and input the following:

  **Goals:** `package`

2. Click `Run`.

> Advanced users can do it from the command line:
```
mvn clean package
```

Finally, run it with `Run` > `Run As` > `2 Spring Boot App`.

> Or use the command line - any of these commands will work
```
java -jar target/practice-spring-jparest-1.0.jar
mvn spring-boot:run
```
