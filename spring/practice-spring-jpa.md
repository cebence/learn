# Practice: Spring Data JPA
Based on: http://spring.io/guides/gs/accessing-data-jpa/

This (literally) step-by-step guide walks you through the process of building an application that uses Spring Data JPA to store and retrieve data in a relational database.

## What you’ll build

You’ll build an application that stores `Person` POJOs in a memory-based database and perform a few queries on that data.

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
  **Artifact Id:** `practice-spring-jpa`  
  **Version:** `1.0`  
  **Name:** `Spring Data JPA practice`

  We also need these in the `Parent Project` section:

  **Group Id:** `org.springframework.boot`  
  **Artifact Id:** `spring-boot-starter-parent`  
  **Version:** `1.1.8.RELEASE` (latest at the time of writing)

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
**Artifact Id:** `spring-boot-starter-data-jpa`

You can leave the *Version* empty as it will be copied from the parent project.

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
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
  </dependency>
</dependencies>
```
But it's easier and faster to do it the former way, especially since you can search for a dependency by id/name in the bottom part of the *Add Dependency* dialog. Small wrinkle though, STS/Eclipse can find only those dependencies that have already been used/downloaded by you on that workstation.

### Create a resource representation class, i.e. entity

1. From the menu `File` > `New` > `Class` and input the following:

  **Source folder:** `practice-spring-jpa/src/main/java`  
  **Package:** `practice.spring.jpa`  
  **Name:** `Person`

2. Click `Finish`.

3. Replace the content with the following:
```java
  package practice.spring.jpa;

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

    protected Person() {
    }

    public Person(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
    }

    @Override
    public String toString() {
      return String.format(
        "Person[id=%d, firstName='%s', lastName='%s']",
        id, firstName, lastName);
    }
}
```

4. Generate getters and setters (`Source` > `Generate Getters and Setters...` > `Select All` > `OK`).

5. Save the file.

### Create a repository interface

1. From the menu `File` > `New` > `Interface` and input the following:

  **Source folder:** `practice-spring-jpa/src/main/java`  
  **Package:** `practice.spring.jpa`  
  **Name:** `PersonRepository`

2. Click `Add...`, type in `CrudRepository`, click `OK`.

3. Click `Finish`.

4. Replace the content with the following:
```java
  package practice.spring.jpa;

  import java.util.List;
  import org.springframework.data.repository.CrudRepository;

  public interface PersonRepository extends CrudRepository<Person, Long> {
    List<Person> findByLastName(String lastName);
  }
```

5. Save the file.

### Create an Application class

1. From the menu `File` > `New` > `Class` and input the following:

  **Source folder:** `practice-spring-jpa/src/main/java`  
  **Package:** `practice.spring.jpa`  
  **Name:** `Application`

2. Click `Finish`.

3. Replace the content with the following:
```java
  package practice.spring.jpa;

  import java.util.List;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
  import org.springframework.context.ConfigurableApplicationContext;
  import org.springframework.context.annotation.Configuration;

  @Configuration
  @EnableAutoConfiguration
  public class Application {
    public static void main(String[] args) {
      ConfigurableApplicationContext context = SpringApplication.run(Application.class);
      PersonRepository repository = context.getBean(PersonRepository.class);

      // Save a few persons in the database.
      repository.save(new Person("John", "Doe"));
      repository.save(new Person("Jane", "Doe"));
      repository.save(new Person("Neil", "O'Brian"));
      repository.save(new Person("Brian", "O'Neil"));
      repository.save(new Person("Chloe", "McCane"));

      // Fetch everyone.
      Iterable<Person> persons = repository.findAll();
      System.out.println("Persons found with findAll():");
      System.out.println("-------------------------------");
      for (Person person : persons) {
        System.out.println(person);
      }
      System.out.println();

      // Fetch an individual by ID.
      Person person = repository.findOne(1L);
      System.out.println("Person found with findOne(1L):");
      System.out.println("-------------------------------");
      System.out.println(person);
      System.out.println();

      // Fetch people by last name.
      List<Person> does = repository.findByLastName("Doe");
      System.out.println("Persons found with findByLastName('Doe'):");
      System.out.println("-------------------------------");
      for (Person doe : does) {
        System.out.println(doe);
      }
      System.out.println();
    }
  }
```

4. Save the file.

### Build an executable JAR

In order to make the JAR executable we need to tell Spring Boot
which class to start, i.e. point it to our `Application` class.

Per [Spring documentation](http://docs.spring.io/autorepo/docs/spring-boot/1.1.8.RELEASE/maven-plugin/usage.html) we need add the `start-class` property to our POM, so open the `pom.xml`, switch to the `Overview` tab (tabs are at the bottom), and in the `Properties` section click `Create...` and input the following:

  **Name:** `start-class`  
  **Value:** `practice.spring.jpa.Application`

> Advanced Maven users can update `pom.xml` directly and add:
```xml
<properties>
  <start-class>practice.spring.jpa.Application</start-class>
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
java -jar target/practice-spring-jpa-1.0.jar
mvn spring-boot:run
```

You should see something like this:

```
== Persons found with findAll():
-------------------------------
Person[id=1, firstName='John', lastName='Doe']
Person[id=2, firstName='Jane', lastName='Doe']
Person[id=3, firstName='Neil', lastName='O'Brian']
Person[id=4, firstName='Brian', lastName='O'Neil']
Person[id=5, firstName='Chloe', lastName='McCane']

Person found with findOne(1L):
-------------------------------
Person[id=1, firstName='John', lastName='Doe']

Persons found with findByLastName('Doe'):
-------------------------------
Person[id=1, firstName='John', lastName='Doe']
Person[id=2, firstName='Jane', lastName='Doe']
```

> For this to really work the project **must** inherit from `spring-boot-starter-parent` (that we specified at the beginning). The [POM in question](http://central.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/1.1.8.RELEASE/spring-boot-starter-parent-1.1.8.RELEASE.pom) integrates Spring Boot into Maven. Spring Boot will add itself to the `MANIFEST` as the `Main-Class`, and our application class as `Start-Class`, e.g.:
```
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: practice.spring.jpa.Application
```

## Appendix: Connect to a production database

It all looks very nice but where's *the real database*, i.e. how to connect to a production database running on a remote *MySQL* server?

> We will simulate a production server with the *MySQL* server that comes with the [XAMPP](http://portableapps.com/apps/development/xampp) installation. The server is configured to accept connections from a user named `root` **without** the password (i.e. empty password).

First, we need to tell *Spring Data* which database to connect to. This can be done through (Java) code, but it is much better to do it with a configuration file (no need to recompile everything when we change the database).

### Replace the JDBC driver
We need to replace the *H2* JDBC driver with the *MySQL* one. Open the `pom.xml` and change the H2 dependency into this one:

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>5.1.34</version>
</dependency>
```

### Configure the DataSource

*Spring Data* will look for any configuration customizations in the `application.properties` file.

1. From the menu `File` > `New` > `Folder` and input:

  **Folder name:** `src/main/resources`

2. From the menu `File` > `New` > `File` and input:

  **File name:** `application.properties`

3. Paste in the following:
```
  spring.datasource.url=jdbc:mysql://localhost/test
  spring.datasource.username=root
  spring.datasource.password=
  spring.datasource.driverClassName=com.mysql.jdbc.Driver
```

> Note that we are using the `test` database on our development *MySQL* server. To connect to a production database please specify the correct connection URL.

### Initialize the database schema

When an in-memory *H2* database was used *Spring Data* detected it, automatically configured it and created a database schema, i.e. created the `CUSTOMER` database table. Since we are *providing* the database we must create the necessary tables.

Connect to the *MySQL* server and issue the following SQL statement:

```sql
create table CUSTOMER (
  ID int primary key auto_increment,
  FIRST_NAME varchar(100),
  LAST_NAME varchar(100)
)
```

Note that [camel-cased](http://en.wikipedia.org/wiki/CamelCase) property names need to be modified so the words are separated with the underscore, i.e. `firstName` became `FIRST_NAME`. This naming policy is a part of the [JPA](http://en.wikipedia.org/wiki/Java_Persistence_API) standard.

### That's it!

Those are all the changes required to switch to another database.

Now, just rebuild the JAR and run it:

```
  mvn clean package
  java -jar target/practice-spring-jpa-1.0.jar
```

The output should be the same as before, and when you check in the database:

```sql
select * from CUSTOMER
```

you should see this:

```
+----+------------+-----------+
| id | first_name | last_name |
+----+------------+-----------+
|  1 | John       | Doe       |
|  2 | Jone       | Doe       |
|  3 | Neil       | O'Brian   |
|  4 | Brian      | O'Neil    |
|  5 | Chloe      | McCane    |
+----+------------+-----------+
```
