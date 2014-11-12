# Practice: Spring REST
Based on: http://spring.io/guides/gs/rest-service/

This (literally) step-by-step guide walks you through the process of building a "hello world" RESTful web service with Spring.

## What you’ll build

You’ll build a service that will accept HTTP GET requests at: 
```
http://localhost:8080/greeting
```
and respond with a JSON representation of a greeting
```
{ "id": 1, "content": "Hello, World!" }
```

You can customize the greeting with an optional name parameter in the query string:

```
http://localhost:8080/greeting?name=User
```

The name parameter value overrides the default value of "World" and is reflected in the response:

```
{ "id": 1, "content": "Hello, User!" }
```

## What you’ll need

- About 15 minutes
- JDK 1.6 or later
- Maven 3.0+
- A favorite text editor or IDE

> In this guide you will find menu navigation specific to the [Spring Tool Suite (STS)](http://spring.io/tools/sts).

### Create a project

Start by creating a new (simple) Maven project in STS:
  1. From the menu `File` > `New` > `Project...`

  2. Select `Maven` > `Maven Project`

  3. Click `Next` (or use the shortcut: `Alt + Shift + N` > `Maven Project`).

  4. Check the `Create a simple project` check-box.

  5. Click `Next`.

  6. Input the following values in the `Artifact` section:

  **Group Id:** `cebence.practice.spring`  
  **Artifact Id:** `practice-spring-rest`  
  **Version:** `1.0`  
  **Name:** `Spring REST practice`

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

Maven needs help from Spring Boot to create a runnable JAR so we need to add Spring Boot Maven plug-in:

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
[INFO] Finished at: Wed Nov 12 11:05:49 CET 2014
[INFO] Final Memory: 6M/15M
[INFO] ------------------------------------------------------------------------
```

### Add project dependencies

Righ-click on `pom.xml` > `Maven` > `Add Dependency` and input the following:

**Group Id:** `org.springframework.boot`  
**Artifact Id:** `spring-boot-starter-web`

You can leave the *Version* empty as it will be copied from the parent project.

> Note this can also be done by manually adding the following to `pom.xml`:
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
</dependencies>
```
But it's easier and faster to do it the former way, especially since you can search for a dependency by id/name in the bottom part of the *Add Dependency* dialog. Small wrinkle though, STS/Eclipse can find only those dependencies that have already been used/downloaded by you on that workstation.

### Create a resource representation class, i.e. entity

1. From the menu `File` > `New` > `Class` and input the following:

  **Source folder:** `practice-spring-rest/src/main/java`  
  **Package:** `practice.spring.rest`  
  **Name:** `Greeting`

2. Click `Finish`.

3. Replace the content with the following:
```java
  package practice.spring.rest;

  public class Greeting {
    private final long id;
    private final String content;

    public Greeting(long id, String content) {
      this.id = id;
      this.content = content;
    }
  }
```

4. Generate getters and setters (`Source` > `Generate Getters and Setters...` > `Select All` > `OK`).

5. Save the file.

### Create a resource controller

1. From the menu `File` > `New` > `Class` and input the following:

  **Source folder:** `practice-spring-rest/src/main/java`  
  **Package:** `practice.spring.rest`  
  **Name:** `GreetingController`

3. Click `Finish`.

4. Replace the content with the following:
```java
  package practice.spring.rest;

  import java.util.concurrent.atomic.AtomicLong;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestParam;
  import org.springframework.web.bind.annotation.RestController;

  @RestController
  public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(
      @RequestParam(value="name", defaultValue="World")
      String name)
    {
      return new Greeting(counter.incrementAndGet(),
          String.format(template, name));
    }
  }
```

### Create an Application class

1. From the menu `File` > `New` > `Class` and input the following:

  **Source folder:** `practice-spring-rest/src/main/java`  
  **Package:** `practice.spring.rest`  
  **Name:** `Application`

2. Click `Finish`.

3. Replace the content with the following:
```java
  package practice.spring.rest;

  import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
  import org.springframework.boot.SpringApplication;
  import org.springframework.context.annotation.ComponentScan;

  @ComponentScan
  @EnableAutoConfiguration
  public class Application {
    public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
    }
  }
```

4. Save the file.

### Build an executable JAR

In order to make the JAR executable we need to tell Spring Boot
which class to start, i.e. point it to our `Application` class.

Per [Spring documentation](http://docs.spring.io/autorepo/docs/spring-boot/1.1.8.RELEASE/maven-plugin/usage.html) we need add the `start-class` property to our POM, so open the `pom.xml`, switch to the `Overview` tab (tabs are at the bottom), and in the `Properties` section click `Create...` and input the following:

  **Name:** `start-class`  
  **Value:** `practice.spring.rest.Application`

> Advanced Maven users can update `pom.xml` directly and add:
```xml
<properties>
  <start-class>practice.spring.rest.Application</start-class>
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
java -jar target/practice-spring-rest-1.0.jar
mvn spring-boot:run
```

Now that the service is up, visit http://localhost:8080/greeting, where you see:

```
{ "id": 1, "content": "Hello, World!" }
```

You can customize the greeting [with a name](http://localhost:8080/greeting?name=User) - the `name` parameter value overrides the default value of "World" and is reflected in the response:

```
{ "id": 2, "content": "Hello, User!" }
```

Note that `id` is incremented on every loading of the page - press the `F5` key a few times to see the change.

> For this to really work the project **must** inherit from `spring-boot-starter-parent` (that we specified at the beginning). The [POM in question](http://central.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/1.1.8.RELEASE/spring-boot-starter-parent-1.1.8.RELEASE.pom) integrates Spring Boot into Maven. Spring Boot will add itself to the `MANIFEST` as the `Main-Class`, and our application class as `Start-Class`, e.g.:
```
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: practice.spring.rest.Application
```
