# 1. 配置文件

## 1.1 外部加载顺序

1. 命令行参数

        java -jar spring-boot-02-config-02.0.0.1-SNAPSHOT.jar --server.port=8087
        java -jar spring-boot-02-config-02.0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

2. 来自java:comp/env的NDI属性
3. Java系统属性(System.getProperties() )
4. 操作系统环境变量
5. RandomValuePropertySource配置的random.*属性值
6. jar包外部的`application-{profile}.properties`或`application.yml`（带spring.profile）配置文件

        -hello
        |- a.jar
        |- application.yml

   将打好的jar包放在一个目录下，比如叫做 hello的文件夹，然后再该文件夹下新建一个名为application.yml的文件，其中指定port为8088
   ，访问路径为/boot ,然后命令行直接启动项目。`java -jar a.jar`
   浏览器通过 localhost:8088/boot/hello 可以正常访问，表示同级目录下的配置文件生效了。
7. jar包内部的application-{profile}.properties或application.yml(带spring.profile)配置文件
8. jar包外部的application.properties或application.yml(不带spring.profile)配置文件
9. jar包外部的application.properties或application.yml(不带spring.profile)配置文件
10. jar包内部的application.properties或application.yml(不带spring.profile)配置文件
11. @Configuration注解类上的propertySource
12. 通过SpringApplication.setDefaultProperties指定的默认属性。

## 1.2 springboot内部加载顺序

1. 配置文件可以放在下面四个地方
    - `file:./config`：项目根目录中config下
    - `file:./`：项目根目录下
    - `classpath:./config`：项目的resources目录中config下
    - `classpath:./`：项目的resources目录下

2. 同目录下`.properties`的优先级比`.yml`高，
3. 如果同一个配置属性，多个文件都配置了，默认使用第一次读取到的，后续不覆盖
4. 高优先级会覆盖低优先级

## 1.3 改变默认的配置文件位置

**项目打包好以后，我们可以使用命令行参数的形式，启动项目的时候来指定配置文件的新位置；指定默认加载的这些配置文件共同起作用形成互补配置
**

在 G盘目录下，创建一个application.properties文件(yml也可以)，定义端口为8085

打包项目，启动命令行：

    java -jar  spring-boot-02-config-02.0.0.1-SNAPSHOT.jar  --spring.config.location=G:/application.properties

回车运行。

浏览器访问： http:localhost:8005/boot02/hello ,显然外部指定的配置文件生效了，并且与之前的主配置文件形成了互补配置

## 1.4 使用profile配置和加载配置文件

### 1.4.1 使用`.properties`文件

假如有开发、测试、生产三个不同的环境，需要定义三个不同环境下的配置
    
    application.properties
    applicaiton-dev.properties
    applictiong-test.properties
    applicaiton-prod.properties

在`application.properties`文件中指定当前使用的环境:

```properties
server.port=8001
# 激活哪个配置文件
spring.profiles.active=dev
#spring.profiles.active=dev,prod,test
spring.profiles.include=prod
```

### 1.4.2 使用`.yml`文件

因为在yml文件中，`---`表示文档分隔符，每个文档独立，所以此时只需要一个`.yml`文件
```yaml
spring:
  profiles:
    active: prod

---
spring:
  profiles: dev

server: 8080

---
spring:
  profiles: test

server: 8081

---
spring.profiles: prod
spring.profiles.include:
  - proddb
  - prodmq

server: 8082

---
spring:
  profiles: proddb

db:
  name: mysql

---
spring:
  profiles: prodmq

mq:
  address: localhost
```

此时读取的就是prod的配置，prod包含proddb,prodmq，此时可以读取proddb,prodmq下的配置

也可以同时激活三个配置spring.profiles.active: prod,proddb,prodmq

### 1.4.3 使用Java中的`@Profile`注解

`@Profile`注解只能配合`@Configuration`和`@Component`使用

```java
@Configuration
@Profile("prod")
public class ProductionConfiguration {

    // ...

}
```
