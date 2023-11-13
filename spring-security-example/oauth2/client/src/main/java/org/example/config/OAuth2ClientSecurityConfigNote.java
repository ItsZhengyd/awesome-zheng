package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 本类主要用于说明各种配置如何使用，其中很多配置重复，实际用法参考{@link OAuth2ClientSecurityConfigNote}
 */
//@Configuration
//@EnableWebSecurity
public class OAuth2ClientSecurityConfigNote {

    /**
     * <pre>
     * 1. ClientRegistrationRepository
     * OAuth 2.0 / OpenID Connect 1.0 ClientRegistration （s） 的存储库
     *
     * 多个ClientRegistration组合成一个ClientRegistrationRepository
     * ClientRegistration是向 OAuth 2.0 或 OpenID Connect 1.0 提供程序注册的客户端的表示形式。
     * ClientRegistration可以在properties文件中以如下方式配置：
     * spring.security.oauth2.client.registration.[registrationId]
     * </pre>
     * ==============================================================
     * <pre>
     * OAuth2AuthorizedClient
     * OAuth2AuthorizedClient 是授权客户的代表。当最终用户（资源所有者）已授权客户端访问其受保护的资源时，客户端被视为已授权。
     * OAuth2AuthorizedClient 用于将（和可选 OAuth2RefreshToken ）与 ClientRegistration OAuth2AccessToken （客户端）和资源所有者相关联，后者是授予授权的 Principal 最终用户。
     * </pre>
     * ==============================================================
     * <pre>
     * 2. OAuth2AuthorizedClientRepository 和 3. OAuth2AuthorizedClientService
     * OAuth2AuthorizedClientRepository 在 Web 请求之间持久化 OAuth2AuthorizedClient
     * OAuth2AuthorizedClientService 在应用程序级别进行管理 OAuth2AuthorizedClient
     * 从开发人员的角度来看， OAuth2AuthorizedClientRepository or OAuth2AuthorizedClientService 提供了查找 OAuth2AccessToken 与客户端关联的功能，以便可以使用它来启动受保护的资源请求。
     * Spring Boot 2.x 自动配置了他们，但是可以覆盖并注册自定义的实现
     * OAuth2AuthorizedClientService 默认实现是 InMemoryOAuth2AuthorizedClientService ，它将对象存储在 OAuth2AuthorizedClient 内存中
     * 或者，您可以配置 JDBC 实现 JdbcOAuth2AuthorizedClientService 以在数据库中持久保存 OAuth2AuthorizedClient 实例。
     * </pre>
     * {@code
     *
     * @Controller public class OAuth2ClientController {
     * @Autowired private OAuth2AuthorizedClientService authorizedClientService;
     * @GetMapping("/") public String index(Authentication authentication) {
     * OAuth2AuthorizedClient authorizedClient =
     * this.authorizedClientService.loadAuthorizedClient("okta", authentication.getName());
     * <p>
     * OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
     * <p>
     * ...
     * <p>
     * return "index";
     * }
     * }
     * }
     * <br/>
     * ==============================================================
     * <pre>
     * 4. AuthorizationRequestRepository<OAuth2AuthorizationRequest>
     * 5. OAuth2AuthorizationRequestResolver
     * 6. OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
     *
     * 4-------------------------------------------------------------
     * AuthorizationRequestRepository 负责从发起授权请求到收到授权响应（回调）期间 OAuth2AuthorizationRequest 的持久性
     * OAuth2AuthorizationRequest 用于关联和验证授权响应。
     * AuthorizationRequestRepository 的默认实现是 HttpSessionOAuth2AuthorizationRequestRepository ，它将 OAuth2AuthorizationRequest 存储在 HttpSession 中。
     * 如果您有 AuthorizationRequestRepository 的自定义实现，则可以按如下方式配置它：
     * 5-------------------------------------------------------------
     * OAuth2AuthorizationRequestRedirectFilter 使用 OAuth2AuthorizationRequestResolver 解析 OAuth2AuthorizationRequest 并通过将最终用户的用户代理重定向到授权服务器的授权端点来启动授权代码授予流程。
     * OAuth2AuthorizationRequestResolver 的主要作用是从所提供的 Web 请求中解析 OAuth2AuthorizationRequest 。默认实现 DefaultOAuth2AuthorizationRequestResolver 匹配（默认）路径
     * /oauth2/authorization/{registrationId} ，提取 registrationId ，并使用它构建 OAuth2AuthorizationRequest 的关联 ClientRegistration 。
     * 6-------------------------------------------------------------
     * 授权代码授予的 OAuth2AccessTokenResponseClient 的默认实现是 DefaultAuthorizationCodeTokenResponseClient ，
     *      它使用 RestOperations 实例在授权服务器的令牌端点 将授权代码交换为访问令牌端点。
     *      它使用 RestOperations 实例在授权服务器的令牌端点 刷新访问令牌时
     * DefaultAuthorizationCodeTokenResponseClient 很灵活，因为它允许您自定义令牌请求的预处理和/或令牌响应的后处理。
     * 如果您需要自定义令牌请求的预处理，可以为 DefaultAuthorizationCodeTokenResponseClient.setRequestEntityConverter() 提供自定义 Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> 。
     * 默认实现 ( OAuth2AuthorizationCodeGrantRequestEntityConverter ) 构建标准 OAuth 2.0 访问令牌请求的 RequestEntity 表示形式。但是，提供自定义 Converter 可以让您扩展标准令牌请求并添加自定义参数。
     * </pre>
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .oauth2Client(oauth2 -> oauth2
                        .clientRegistrationRepository(this.clientRegistrationRepository())  // 1
                        .authorizedClientRepository(this.authorizedClientRepository())      // 2
                        .authorizedClientService(this.authorizedClientService())            // 3
                        .authorizationCodeGrant(codeGrant -> codeGrant
                                .authorizationRequestRepository(this.authorizationRequestRepository())  // 4. 用于配置授权请求的存储库，即存储和检索授权请求的存储库
                                .authorizationRequestResolver(this.authorizationRequestResolver())      // 5. 用于配置授权请求解析器，即用于解析和处理授权请求的解析器。
                                .accessTokenResponseClient(this.accessTokenResponseClient())            // 6.用于配置访问令牌响应客户端，即用于向令牌端点发送请求并处理响应的客户端。
                        )
                );
        return http.build();
    }

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(
                                        authorizationRequestResolver(this.clientRegistrationRepository)
                                )
                        )
                );
        return http.build();
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                authorizationRequestCustomizer());

        return authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer
                .additionalParameters(params -> params.put("prompt", "consent"));
    }

    /**
     * <pre>
     * OAuth2AuthorizedClientManager 对 OAuth2AuthorizedClient （s） 进行全面管理。
     * 主要职责包括：
     *      使用 OAuth2AuthorizedClientProvider 授权（或重新授权）OAuth 2.0 客户端。
     *      委派 的 OAuth2AuthorizedClient 持久性 ，通常使用 OAuth2AuthorizedClientService 或 OAuth2AuthorizedClientRepository
     *      委托给 OAuth 2.0 客户端已成功授权（或重新授权） OAuth2AuthorizationSuccessHandler 的时间。
     *      当 OAuth 2.0 客户端无法授权（或重新授权）时委派给 OAuth2AuthorizationFailureHandler
     *
     * `OAuth2AuthorizedClientProvider`实现了一种策略，用于对OAuth 2.0客户端进行授权（或重新授权）。实现通常涵盖授权授予类型，比如`authorization_code`、`client_credentials`等。
     *
     * `OAuth2AuthorizedClientManager`的默认实现是`DefaultOAuth2AuthorizedClientManager`，它与一个`OAuth2AuthorizedClientProvider`关联，
     * 该提供程序可以使用基于委托的组合支持多个授权授予类型。您可以使用`OAuth2AuthorizedClientProviderBuilder`来配置和构建基于委托的组合。
     *
     * 以下代码示例展示了如何配置和构建一个`OAuth2AuthorizedClientProvider`组合，提供对`authorization_code`、`refresh_token`、`client_credentials`和`password`授权授予类型的支持：
     *
     * 当授权尝试成功时，`DefaultOAuth2AuthorizedClientManager`会委托给`OAuth2AuthorizationSuccessHandler`，它（默认情况下）通过`OAuth2AuthorizedClientRepository`保存`OAuth2AuthorizedClient`。
     * 在重新授权失败的情况下（例如，刷新令牌不再有效），之前保存的`OAuth2AuthorizedClient`会通过`RemoveAuthorizedClientOAuth2AuthorizationFailureHandler`从`OAuth2AuthorizedClientRepository`中移除。
     * 您可以通过`setAuthorizationSuccessHandler(OAuth2AuthorizationSuccessHandler)`和`setAuthorizationFailureHandler(OAuth2AuthorizationFailureHandler)`来定制默认行为。
     *
     * `DefaultOAuth2AuthorizedClientManager`还与`contextAttributesMapper`相关联，其类型为`Function<OAuth2AuthorizeRequest, Map<String, Object>>`，负责将`OAuth2AuthorizeRequest`中的属性映射到
     * 要与`OAuth2AuthorizationContext`关联的属性映射。当您需要向`OAuth2AuthorizedClientProvider`提供所需（支持的）属性时，这将非常有用，例如，`PasswordOAuth2AuthorizedClientProvider`需要资源所有者的
     * 用户名和密码在`OAuth2AuthorizationContext.getAttributes()`中可用。
     *
     * DefaultOAuth2AuthorizedClientManager被设计用于HttpServletRequest的上下文中。在HttpServletRequest上下文之外操作时，请使用AuthorizedClientServiceOAuth2AuthorizedClientManager。
     *
     * 服务应用程序是使用AuthorizedClientServiceOAuth2AuthorizedClientManager的常见用例。服务应用程序通常在后台运行，没有任何用户交互，并且通常在系统级帐户而不是用户帐户下运行。
     * 使用client_credentials授权类型配置的OAuth 2.0客户端可以被视为服务应用程序的一种类型。
     *
     * 以下代码显示了如何配置 AuthorizedClientServiceOAuth2AuthorizedClientManager 为 client_credentials 授权类型提供支持的示例：
     *
     * </pre>
     * {@code
     *
     * @Bean public OAuth2AuthorizedClientManager authorizedClientManager(
     * ClientRegistrationRepository clientRegistrationRepository,
     * OAuth2AuthorizedClientService authorizedClientService) {
     * <p>
     * OAuth2AuthorizedClientProvider authorizedClientProvider =
     * OAuth2AuthorizedClientProviderBuilder.builder()
     * .clientCredentials()
     * .build();
     * <p>
     * AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
     * new AuthorizedClientServiceOAuth2AuthorizedClientManager(
     * clientRegistrationRepository, authorizedClientService);
     * authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
     * <p>
     * return authorizedClientManager;
     * }
     * }
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .clientCredentials()
                        .password()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        // 假设`username`和`password`作为`HttpServletRequest`参数提供，
        // 将`HttpServletRequest`参数映射到`OAuth2AuthorizationContext.getAttributes()`。
        authorizedClientManager.setContextAttributesMapper(contextAttributesMapper());

        return authorizedClientManager;
    }

    private Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper() {
        return authorizeRequest -> {
            Map<String, Object> contextAttributes = Collections.emptyMap();
            HttpServletRequest servletRequest = authorizeRequest.getAttribute(HttpServletRequest.class.getName());
            String username = servletRequest.getParameter(OAuth2ParameterNames.USERNAME);
            String password = servletRequest.getParameter(OAuth2ParameterNames.PASSWORD);
            if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
                contextAttributes = new HashMap<>();

                // `PasswordOAuth2AuthorizedClientProvider` requires both attributes
                contextAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username);
                contextAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password);
            }
            return contextAttributes;
        };
    }


    /**
     * 如果您需要自定义令牌请求的预处理，可以为 DefaultAuthorizationCodeTokenResponseClient.setRequestEntityConverter() 提供自定义 Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> 。
     * 默认实现 ( OAuth2AuthorizationCodeGrantRequestEntityConverter ) 构建标准 OAuth 2.0 访问令牌请求的 RequestEntity 表示形式。但是，提供自定义 Converter 可以让您扩展标准令牌请求并添加自定义参数。
     */
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        // 默认实现 可直接返回
        DefaultAuthorizationCodeTokenResponseClient defaultAuthorizationCodeTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();


        // 自定义Converter，扩展标准令牌请求并添加自定义参数
        OAuth2AuthorizationCodeGrantRequestEntityConverter requestEntityConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        requestEntityConverter.setParametersConverter(source -> {   // 覆盖
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("a", "b");
            return map;
        });
        requestEntityConverter.addParametersConverter(source -> {   // 添加
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("a", "b");
            return map;
        });
        defaultAuthorizationCodeTokenResponseClient.setRequestEntityConverter(requestEntityConverter);

        // 自定义访问令牌响应
        defaultAuthorizationCodeTokenResponseClient.setRestOperations(this.restOperations());

        return defaultAuthorizationCodeTokenResponseClient;
    }

    private RestTemplate restOperations(){
        RestTemplate restTemplate = new RestTemplate(
                Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        return restTemplate;
    }


    private OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        return new DefaultOAuth2AuthorizationRequestResolver(this.clientRegistrationRepository(), "");
    }

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        // return new CustomOAuth2AuthorizationRequestRepository();     // 自定义实现
        return new HttpSessionOAuth2AuthorizationRequestRepository();   // 默认唯一实现
    }

    private OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(this.clientRegistrationRepository());
    }

    private OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(this.authorizedClientService());
    }

    private ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository();
    }


}
