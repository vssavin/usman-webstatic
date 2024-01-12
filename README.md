# What is it about?
This project uses the [spring-framework](https://github.com/spring-projects/spring-framework) and provides an API to
manage users with controllers that return simple html pages based on [Thymeleaf](https://www.thymeleaf.org/) templates.  
This project uses [usman-core](https://github.com/vssavin/usman-core) as its service layer.  
This project supports spring5 and spring6 versions.  
Usage examples can be found in the repository [usman-webstatic-samples](https://github.com/vssavin/usman-webstatic-samples)

## Configuration
### Dependencies
If your project uses spring-boot you can use `usman-webstatic-starter` dependency  to simplify configuration  
spring5:
```
<groupId>com.github.vssavin.usman-webstatic-starter</groupId>
<artifactId>usman-webstatic-starter-spring5</artifactId>
<version>0.0.3</version>
```
spring6:
```
<groupId>com.github.vssavin.usman-webstatic-starter</groupId>
<artifactId>usman-webstatic-starter-spring6</artifactId>
<version>0.0.3</version>
```

### Java code configuration
This project requires your configuration classes to have the following beans:

```java
@Bean
public UsmanWebstaticConfigurer usmanConfigurer(UsmanUrlsConfigurer urlsConfigurer, OAuth2Config oAuth2Config, List<PermissionPathsContainer> permissionPathsContainerList) {
    UsmanWebstaticConfigurer usmanConfigurer = new UsmanWebstaticConfigurer(urlsConfigurer, oAuth2Config, permissionPathsContainerList);
    usmanConfigurer.permission(new AuthorizedUrlPermission("/index.html", Permission.ANY_USER)).permission(new AuthorizedUrlPermission("/index", Permission.ANY_USER));
    usmanConfigurer.defaultLanguage("en");
    return usmanConfigurer.configure();
}
```

```java
@Bean
public UsmanUrlsConfigurer usmanUrlsConfigurer() {
    UsmanUrlsConfigurer usmanUrlsConfigurer = new UsmanUrlsConfigurer();
    usmanUrlsConfigurer.successUrl("/index.html").adminSuccessUrl("/usman/v1/admin");
    return usmanUrlsConfigurer.configure();
}
```

```java
@Bean
@Primary
public DataSource appDatasource() {
    return new EmbeddedDatabaseBuilder()
        .generateUniqueName(true)
        .setType(H2)
        .setScriptEncoding("UTF-8")
        .ignoreFailedDrops(true)
        .addScript("init.sql")
        .build();
}
```

```java
// this bean is used in usman-webstatic-starter autoconfiguration to create
// LocalContainerEntityManagerFactoryBean and set packages to scan
@Bean
public EntityScanPackages projectScanEntityPackages() {
    return () -> new String[] { "com.example.projectname.*" };
}
```


