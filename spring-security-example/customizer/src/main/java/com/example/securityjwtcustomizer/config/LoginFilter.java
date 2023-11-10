package com.example.securityjwtcustomizer.config;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);


    private final Cache<String, AtomicInteger> cacheLoginTimes;

    public LoginFilter(Cache<String, AtomicInteger> cacheLoginTimes) {
        this.cacheLoginTimes = cacheLoginTimes;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 检查请求是否为登录请求
        if (isLoginRequest(request)) {

            // 检查用户的登录次数，如果已经达到限制，则拒绝登录
            if (isLoginLimitExceeded(request)) {
                throw new RuntimeException("登录失败次数过多，请稍后再试");
            }

        }

        // 从请求中获取JWT令牌
        String jwtToken = extractJwtTokenFromRequest(request);

        // 验证JWT令牌的有效性
        if (isValidJwtToken(jwtToken)) {
            // 如果令牌有效，建立安全上下文
            Authentication authentication = createAuthenticationFromJwt(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isLoginLimitExceeded(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        AtomicInteger times = cacheLoginTimes.get(remoteAddr + remoteHost, key -> new AtomicInteger(0));
        return times.incrementAndGet() >= 3;
    }

    private Authentication createAuthenticationFromJwt(String jwtToken) {
        return null;
    }

    private boolean isValidJwtToken(String jwtToken) {
        return false;
    }

    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        return null;
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod());
    }
}
