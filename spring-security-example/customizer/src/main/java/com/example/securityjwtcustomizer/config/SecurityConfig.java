package com.example.securityjwtcustomizer.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    RSAPrivateKey privateKey;

    private final LoginFilter loginFilter;

    public SecurityConfig(LoginFilter loginFilter) {
        this.loginFilter = loginFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        List<String> permitAllList = List.of(
                "/register",
                "/login",
                "/logout",
                "/public/**",
                "/api/login",
                "/api/token",
                "/api/health",
                "/static/**",
                "/signup",
                "/about"
        );

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
                .formLogin(withDefaults()
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers(permitAllList.toArray(new String[0])).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/db/**").access(allOf(hasAuthority("db"), hasRole("ADMIN")))
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(configurer -> configurer.jwt(Customizer.withDefaults())); // spring-security6.1以前使用.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);spring-security6.1开始不推荐7将会移除

        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtDecoder jwtDecoder) {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);


        RememberMeAuthenticationProvider rememberMeAuthenticationProvider = new RememberMeAuthenticationProvider("remember-me");

        AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider("anonymous");

        // 注意：如果使用自定义JWTProvider，则需要在这里配置
        // 这里使用的是spring-boot-starter-oauth2-resource-server的默认实现，只需要在SecurityFilterChain中添加
        // .oauth2ResourceServer(configurer -> configurer.jwt(Customizer.withDefaults()));即可启用，不用再在这里配置
        // 另外自定义两个JWT的编解码器就可以了：参考 JwtDecoder 和 JwtEncoder
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);

        return new ProviderManager(
                daoAuthenticationProvider,
                rememberMeAuthenticationProvider,
                anonymousAuthenticationProvider,
                jwtAuthenticationProvider
        );
    }

    /**
     *
     * DaoAuthenticationProvider 默认使用的加密方式就是 PasswordEncoderFactories.createDelegatingPasswordEncoder()，不用再特别设置
     * <p>
     * PasswordEncoderFactories.createDelegatingPasswordEncoder() 是一个工厂方法，用于创建一个委托密码编码器。
     * 这个委托编码器可以处理多种密码编码策略，包括 bcrypt、SHA-256、MD5等。
     */
    @Deprecated
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    /**
     *
     * 第一次请求jwt时，会生成一个公钥和私钥，公钥和私钥会存储到redis中，后续请求jwt时，会从redis中获取公钥和私钥，然后使用公钥和私钥进行加密和解密。
     * <br>
     * <br>
     * <p>
     * 但是这样存在一个问题，如果redis宕机了，那么jwt就无法使用了。
     * <p>
     *     * 解决方案：
     *     <li> 1. 定时任务，每隔一段时间，将公钥和私钥存储到数据库中。
     *     <li> 2. 利用redisson的分布式锁，在每次请求jwt时，先获取分布式锁，如果获取到锁，则将公钥和私钥存储到redis中，并释放锁。
     *     <li> 3. 保存在项目文件中，每次启动时，将公钥和私钥加载到内存中。但是这样就存在一个问题，如果项目文件被泄露了，那么公钥和私钥就泄露了。(不推荐)
     * </p>
     */
    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}