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

### Test the application

To test the application we need to use an additional tool that can send custom HTTP requests, and the one we will use is a *Mozilla Firefox* add-on  called [RESTClient](https://addons.mozilla.org/en-US/firefox/addon/restclient/).

> Similar extension can be found for *Google Chrome*, and those comfortable in the command-line can use `curl` (a Unix/Linux tool).

Open the *RESTClient* via `Tools` > `RESTClient`, input `http://localhost:8080` into its URL field, leave the HTTP method set to `GET`, and click `Send`. You should see a response like this (under *Response Body (Raw)* tab):

```json
{
  "_links" : {
    "people" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    }
  }
}
```

> **Note:**  *Spring Data REST* uses the [HAL format](http://stateless.co/hal_specification.html) for JSON output to supply additional information on the requested resources, like links adjacent to the data that is served.

As a response to our visit to the application root resource (`/`) we gòt a directory of available resources. There is a **people** link located at `http://localhost:8080/people`, and it has some options such as `?page`, `?size`, and `?sort`.

When we continue to `http://localhost:8080/people` we get an "empty" response - no actual data, just "*number of people = 0*":

```json
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 0,
    "totalPages" : 0,
    "number" : 0
  }
}
```

So let's create some people!

Set the following values into *RESTClient*:

**Method:** `POST`  
**URL:** `http://localhost:8080/people`  
**Body:** `{ "firstName" : "Frodo", "lastName" : "Baggins" }`

We also need to set an HTTP request header so the application knows the payload (i.e. body) contains a JSON object. From the *RESTClient* menu `Headers` > `Custom Header`:

**Name:** `Content-Type`  
**Value:** `application/json`

Check the `Save to favorite` check-box (this is so we don't have to type it in all the time since it's a common request header in a REST application), and click `Okay`.

When you send out the request the application responds with a simple success & location under the *Response Headers* tab:

```
Status Code: 201 Created
Content-Length: 0
Location: http://localhost:8080/people/1
```

The operation was a success (status code `2xx`), there is no data in the response (content length is `0`), but we did get something useful - the **location** of our newly created person.

Go ahead and request it - `GET http://localhost:8080/people/1` and clear out headers and body:

```json
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
```

If we could check the database - which we can't at the moment because it's an in-memory H2 database (see [how to switch to MySQL](practice-spring-jpa.md#appendix-connect-to-a-production-database)) - we would see a `(1, "Frodo", "Baggins")` record in the `PERSON` table.

If we repeat the `GET http://localhost:8080/people` request we would get this response:

```json
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
  "_embedded" : {
    "people" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/1"
        }
      }
    } ]
  },
  "page" : {
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}
```

The **persons** object contains a list with Frodo. Notice how it includes a **self** link. *Spring Data REST* also uses [Evo Inflector](http://www.atteo.org/2011/12/12/Evo-Inflector.html) to pluralize the name of the entity for groupings.

Let's create some more people just like we did earlier -  `POST` method, `Content-Type` header, and these bodies (one at a time):

```json
{ "firstName" : "Bilbo", "lastName" : "Baggins" }
{ "firstName" : "Sam", "lastName" : "Gamgee" }
{ "firstName" : "Merry", "lastName" : "Brandybuck" }
{ "firstName" : "Pippin", "lastName" : "Tuk" }
```

If we repeat the `GET http://localhost:8080/people` request this time we would get a list of five people.

We have made a mistake with one of the names, Pippin's last name should be "Took", but how do we fix it?

We should make a `PUT` request to Pippin's URL (`http://localhost:8080/people/5`) with the `Content-Type` header and this body:

```json
{ "firstName" : "Pippin", "lastName" : "Took" }
```

> Note that we must provide the full object, i.e. we can't just send out `lastName` because `PUT` is a *replace* command, it's not *update these fields*.

To modify only a part of an object we must make a `PATCH` request to its URL with the fields to be modified. Let's give Pippin his real first name by sending a request `PATCH http://localhost:8080/people/5` (it's not in the list of methods, just type it in):

```json
{ "firstName" : "Peregrin" }
```
It worked!

How about deleting it? Send a `DELETE` request to `http://localhost:8080/people/5` (no headers, no body) and the response is `204` (OK) - Pippin is gone. If we try to `GET http://localhost:8080/people/5` we get the (in)famous response `404` (not found).

Let's check what does the **search** URL do. Do a `GET http://localhost:8080/people/search` request and...

```json
{
  "_links" : {
    "findByLastName" : {
      "href" : "http://localhost:8080/people/search/findByLastName{?name}",
      "templated" : true
    }
  }
}
```

OK, let's find all the people named "Baggins" - `GET http://localhost:8080/people/search/findByLastName?name=Baggins` and the response is:

```json
{
  "_embedded" : {
    "people" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/1"
        }
      }
    },{
      "firstName" : "Bilbo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/2"
        }
      }
    } ]
  }
}
```

> So, if we were to add a `findByFirstName` method to the `PersonRepository` interface (and recompile!) we would instantly be able to search people on first name too? Try it out!

### Summary

Congratulations! You have just created an application with a hypermedia-based RESTful front end and a JPA-based back end.
