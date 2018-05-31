package com.hand.sxy.config;


import com.hand.sxy.oauth.CustomClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author spilledyear
 */
@Configuration
public class OAuth2Config {

    private static final String API_RESOURCE_ID = "api-resource";


    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        @Qualifier("authenticationManagerBean")
        AuthenticationManager authenticationManager;

//        @Autowired
//        RedisConnectionFactory redisConnectionFactory;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        UserDetailsService userDetailsService;


        @Override
        public void configure(ClientDetailsServiceConfigurer clientConfigure) throws Exception {

            clientConfigure.withClientDetails(new CustomClientDetailsService());

//            clientConfigure.inMemory()
//
//                     http://localhost:7070/oauth/token?grant_type=client_credentials&scope=read&client_id=client3&client_secret=secret
//                    .withClient("client3")
//                    .secret("secret")
//                    .authorizedGrantTypes("client_credentials", "refresh_token")
//                    .resourceIds(API_RESOURCE_ID)
//                    .scopes("read")
//                    .authorities("USER")
//
//
////            http://localhost:7070/oauth/token?username=user1&password=123456&grant_type=password&scope=read&client_id=client2&client_secret=secret
////            http://localhost:7070/order/1?access_token=e7d73ebe-d6c6-4bc6-8384-a1a67fbf3de9
//                    .and()
//                    .withClient("client2")
//                    .secret("secret")
//                    .authorizedGrantTypes("password", "refresh_token")
//                    .resourceIds(API_RESOURCE_ID)
//                    .scopes("read")
//                    .authorities("USER")
//
//
//                    .and()
//                    .withClient("client_3")
//                    .secret(finalSecret)
//                    .authorizedGrantTypes("implicit", "refresh_token")
//                    .resourceIds(API_RESOURCE_ID)
//                    .scopes("select")
//                    .authorities("USER")
//
//
////            http://localhost:7070/oauth/authorize?response_type=code&client_id=client&scope=read&&client_secret=secret&redirect_uri=http://localhost:7071/aiqiyi/qq/redirect
////            http://localhost:7070/aiqiyi/qq/redirect?code=niB1UT
//                    .and()
//                    .withClient("client")
//                    .secret("secret")
//                    .authorizedGrantTypes("authorization_code", "implicit", "refresh_token")
//                    .resourceIds(API_RESOURCE_ID)
//                    .scopes("read")
//                    .authorities("USER")
//                    .redirectUris("http://localhost:7071/aiqiyi/qq/redirect");
        }

        /**
         * 使用jwt方式存储token
         *
         * @return
         */
        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(jwtAccessTokenConverter());
        }


        /**
         * 使用 JWT 令牌
         *
         * @return
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
                @Override
                public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                    String userName = authentication.getUserAuthentication().getName();

                    /** 自定义一些token属性 ***/
                    final Map<String, Object> additionalInformation = new HashMap<>(5);
                    additionalInformation.put("userName", userName);
                    ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                    OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
                    return enhancedToken;
                }
            };

            // 用于加密的 密钥
            converter.setSigningKey("123");
            return converter;
        }

        @Bean
        @Primary
        public AuthorizationServerTokenServices defaultTokenServices() {
            final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter());
            defaultTokenServices.setTokenStore(tokenStore());
            return defaultTokenServices;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

            /**
             * 使用RedisTokenStore 存储 access_token
             */
//            TokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);


            /**
             * JWT方式存储token
             */
            TokenStore tokenStore = tokenStore();
            endpoints.tokenStore(tokenStore)
                    .tokenServices(defaultTokenServices())
                    .accessTokenConverter(jwtAccessTokenConverter())
                    .authenticationManager(authenticationManager)
                    .userDetailsService(userDetailsService)
                    // 允许 GET、POST 请求获取 token，即访问端点：oauth/token
                    .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        }


        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
            // 允许表单认证
            oauthServer.realm(API_RESOURCE_ID).allowFormAuthenticationForClients();

//            oauthServer.realm(API_RESOURCE_ID)
//                    //url:/oauth/token_key,exposes public key for token verification if using JWT tokens
//                    .tokenKeyAccess("permitAll()")
//                    //url:/oauth/check_token allow check token
//                    .checkTokenAccess("isAuthenticated()")
//                    .checkTokenAccess("hasAuthority('ME')")
//
//                    //允许表单认证
//                    .allowFormAuthenticationForClients();
        }

    }


    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(API_RESOURCE_ID).stateless(true);

            // 注冊 tokenService
            resources.tokenServices(defaultTokenServices());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

                    // 配置order访问控制，必须认证过后才可以访问
                    .and().anonymous()
                    .and().requestMatchers().anyRequest()
                    .and().authorizeRequests().antMatchers("/order/**").authenticated();
        }


        /** ================================== 以下代码与 认证服务器 一致 ===============================*/


        /**
         * 使用 JWT 令牌
         *
         * @return
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
                @Override
                public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                    String userName = authentication.getUserAuthentication() == null ? "123456" : authentication.getUserAuthentication().getName();

                    /** 自定义一些token属性 ***/
                    final Map<String, Object> additionalInformation = new HashMap<>(5);
                    additionalInformation.put("userName", userName);
                    ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                    OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
                    return enhancedToken;
                }
            };

            // 用于加密的 密钥
            converter.setSigningKey("123");
            return converter;
        }


        /**
         * token存储,这里使用jwt方式存储
         *
         * @return
         */
        @Bean
        public TokenStore tokenStore() {
            TokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
            return tokenStore;
        }


        /**
         * 创建一个默认的资源服务token
         *
         * @return
         */
        @Bean
        @Primary
        public ResourceServerTokenServices defaultTokenServices() {
            final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter());
            defaultTokenServices.setTokenStore(tokenStore());
            return defaultTokenServices;
        }
    }
}
