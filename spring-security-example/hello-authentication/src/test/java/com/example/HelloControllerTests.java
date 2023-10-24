package com.example;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Josh Cummings
 */
@WebMvcTest({ LoginController.class, HomeController.class })
@Import(SecurityConfig.class)
public class HelloControllerTests {

    @Autowired
    MockMvc mvc;

    @Test
    void rootWhenAuthenticatedThenSaysHelloUser() throws Exception {
        // @formatter:off
        MvcResult result = this.mvc.perform(post("/api/token")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();

        this.mvc.perform(get("/api/hello")
                        .header("Authorization", "Bearer " + token))
                .andExpect(content().string("hello"));
        // @formatter:on
    }

    @Test
    void rootWhenUnauthenticatedThen401() throws Exception {
        // @formatter:off
        this.mvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
        // @formatter:on
    }

    @Test
    void tokenWhenBadCredentialsThen401() throws Exception {
        // @formatter:off
        this.mvc.perform(post("/token"))
                .andExpect(status().isUnauthorized());
        // @formatter:on
    }

}
