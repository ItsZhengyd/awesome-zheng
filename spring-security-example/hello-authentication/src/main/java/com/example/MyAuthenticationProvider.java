package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * @author zhengyd
 */
public class MyAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final Log logger = LogFactory.getLog(MyAuthenticationProvider.class);

    /**
     * 这个方法在验证用户凭证之后，但在获取用户详细信息之前被调用。你可以在这里添加额外的认证检查，例如检查用户的账户是否被锁定、密码是否过期等。
     *
     * @param userDetails as retrieved from the
     * {@link #retrieveUser(String, UsernamePasswordAuthenticationToken)} or
     * <code>UserCache</code>
     * @param authentication the current request that needs to be authenticated
     * @throws AuthenticationException
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        logger.info("my additionalAuthenticationChecks");
    }

    /**
     * 这个方法在获取用户详细信息之前被调用。你可以在这个方法中根据用户名从数据库或其他数据源中检索用户详细信息。
     * 如果找不到用户，可以返回null或者抛出一个AuthenticationException异常。
     *
     * @param username The username to retrieve
     * @param authentication The authentication request, which subclasses <em>may</em>
     * need to perform a binding-based retrieval of the <code>UserDetails</code>
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        logger.info("my retrieveUser");
        return null;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info("my authenticate");
        return null;
    }

    @Override
    protected void doAfterPropertiesSet() {
        logger.info("my doAfterPropertiesSet");
    }
}
