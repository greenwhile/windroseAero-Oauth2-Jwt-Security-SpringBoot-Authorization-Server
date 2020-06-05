package ua.uhmc.springsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients
//                .inMemory()
//                .withClient("oAuth2JwtClientID_2")
//                .secret(passwordEncoder().encode("abcdef"))
//                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
//                .scopes("windmap", "read")
//                .accessTokenValiditySeconds(3600)
//                .refreshTokenValiditySeconds(2592000)
//                .redirectUris("http://localhost:8080/movie/", "http://localhost:8080/movie/index")
//
//                .and()

                .inMemory()
                .withClient("oAuth2JwtClientID")
                .secret(passwordEncoder().encode("abcdef"))
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("windmap", "read")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(2592000)
                .redirectUris("http://localhost:8080/windmap", "http://localhost:8080/windmap/index," +
                        "http://localhost:8080/windmap/up", "http://localhost:8080/windmap/up/index",
                        "http://localhost:8080/windmap/down", "http://localhost:8080/windmap/down/index",
                        "http://localhost:8080/windmap/right", "http://localhost:8080/windmap/right/index",
                        "http://localhost:8080/windmap/left", "http://localhost:8080/windmap/left/index");

//                .and()
//
//                .inMemory()
//                .withClient("oAuth2JwtClientID")
//                .secret(passwordEncoder().encode("abcdef"))
//                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
//                .scopes("windmap", "read")
//                .accessTokenValiditySeconds(3600)
//                .refreshTokenValiditySeconds(2592000)
//                .redirectUris("http://localhost:8080/current", "http://localhost:8080/current/index");;

    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain).authenticationManager(authenticationManager);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("abcdef");
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
