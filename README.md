## springboot整合Shiro
#### 1、引入shiro-spring依赖
```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.4.0</version>
</dependency>
```

#### 2、新建ShiroConfig文件，配置shiroFilter
> 配置shiroFilter需要依赖SecurityManager，在SecurityManager当中我们需要配置shiro用于认证和授权的Realm，这里我们用Shiro内置的IniRealm进行配置

1）、编写ini配置文件

