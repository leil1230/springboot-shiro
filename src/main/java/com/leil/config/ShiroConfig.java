package com.leil.config;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    // 配置shiroFilter

    @Bean
    public IniRealm realm() {
        IniRealm iniRealm = new IniRealm("classpath:shiro-ini/users.ini");
        return iniRealm;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager webSecurityManager(Realm realm, RememberMeManager rememberMeManager) {
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setRealm(realm);
        webSecurityManager.setRememberMeManager(rememberMeManager);
        return webSecurityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setUnauthorizedUrl("/unauthorized");

        // 配置对应地址所对应要处理的Filter
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/unauthorized", "anon");
        // 必须配置user过滤器才能使rememberMe生效
        filterChainDefinitionMap.put("/", "user");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilter;
    }

    // 配置rememberMe

    @Bean
    public SimpleCookie cookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        //如果httyOnly设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24);
        return cookie;
    }

    @Bean
    public RememberMeManager rememberMeManager(SimpleCookie cookie) {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        byte[] cipherKey = Base64.decode("wGiHplamyXlVB11UXWol8g==");
        rememberMeManager.setCipherKey(cipherKey);
        rememberMeManager.setCookie(cookie);
        return rememberMeManager;
    }

}
