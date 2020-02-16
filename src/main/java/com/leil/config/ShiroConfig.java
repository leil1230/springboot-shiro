package com.leil.config;

import com.leil.cache.RedisCacheManager;
import com.leil.filter.RoleOrFilter;
import com.leil.session.CustomSessionManager;
import com.leil.session.RedisSessionDAO;
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

import javax.servlet.Filter;
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
    public DefaultWebSecurityManager webSecurityManager(Realm realm,
                                                        RememberMeManager rememberMeManager,
                                                        CustomSessionManager customSessionManager,
                                                        RedisCacheManager redisCacheManager) {
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setRealm(realm);
        webSecurityManager.setRememberMeManager(rememberMeManager);
        webSecurityManager.setSessionManager(customSessionManager);
        webSecurityManager.setCacheManager(redisCacheManager);
        return webSecurityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setUnauthorizedUrl("/unauthorized");

        // 配置自定义Filter
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("roleOr", roleOrFilter());
        shiroFilter.setFilters(filters);

        // 配置对应地址所对应要处理的Filter
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/unauthorized", "anon");
        // 必须配置user过滤器才能使rememberMe生效
        filterChainDefinitionMap.put("/", "user,roleOr[admin]");
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


    // 自定义Filter

    @Bean
    public RoleOrFilter roleOrFilter() {
        return new RoleOrFilter();
    }

    // 配置redis Session

    @Bean
    public CustomSessionManager customSessionManager(RedisSessionDAO redisSessionDAO) {
        CustomSessionManager customSessionManager = new CustomSessionManager();
        customSessionManager.setSessionDAO(redisSessionDAO);
        return customSessionManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        return new RedisSessionDAO();
    }

    // 配置redis缓存授权信息
    @Bean
    public RedisCacheManager redisCacheManager() {
        return new RedisCacheManager();
    }

}
