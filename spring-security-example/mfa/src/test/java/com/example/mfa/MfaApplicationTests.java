package com.example.mfa;

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
public class MfaApplicationTests implements Serializable {

	private static final String hexKey = "80ed266dd80bcd32564f0f4aaa8d9b149a2b1eaa";

	public static void main(String[] args) throws GeneralSecurityException, InterruptedException {
		int code = TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(hexKey);
		System.out.println(Instant.now());
		System.out.println("code = " + code);

		Thread.sleep(29_000);
		boolean flag = TimeBasedOneTimePasswordUtil.validateCurrentNumberHex(hexKey, code, 30_000);
		System.out.println(Instant.now());
		System.out.println("flag = " + flag);

		Thread.sleep(2_000);
		flag = TimeBasedOneTimePasswordUtil.validateCurrentNumberHex(hexKey, code, 1900);
		System.out.println(Instant.now());
		System.out.println("flag = " + flag);

	}

	@Autowired
	private MockMvc mockMvc;

	@Test
	void mfaWhenAllFactorsSucceedMatchesThenWorks() throws Exception {
		// @formatter:off
		MvcResult result = this.mockMvc.perform(formLogin()
						.user("user@example.com")
						.password("password"))
				.andExpect(redirectedUrl("/second-factor"))
				.andReturn();

		HttpSession session = result.getRequest().getSession();

		Integer code = TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(hexKey);
		this.mockMvc.perform(post("/second-factor")
						.session((MockHttpSession) session)
						.param("code", String.valueOf(code))
						.with(csrf()))
				.andExpect(redirectedUrl("/third-factor"));

		this.mockMvc.perform(post("/third-factor")
						.session((MockHttpSession) session)
						.param("answer", "smith")
						.with(csrf()))
				.andExpect(redirectedUrl("/"));
		// @formatter:on
	}

	@Test
	void mfaWhenBadCredsThenStillRequestsRemainingFactorsAndRedirects() throws Exception {
		// @formatter:off
		MvcResult result = this.mockMvc.perform(formLogin()
						.user("user@example.com")
						.password("wrongpassword"))
				.andExpect(redirectedUrl("/second-factor"))
				.andReturn();

		HttpSession session = result.getRequest().getSession();

		Integer code = TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(hexKey);
		this.mockMvc.perform(post("/second-factor")
						.session((MockHttpSession) session)
						.param("code", String.valueOf(code))
						.with(csrf()))
				.andExpect(redirectedUrl("/third-factor"));

		this.mockMvc.perform(post("/third-factor")
						.session((MockHttpSession) session)
						.param("answer", "smith")
						.with(csrf()))
				.andExpect(redirectedUrl("/login?error"));
		// @formatter:on
	}

	@Test
	void mfaWhenWrongCodeThenRedirects() throws Exception {
		// @formatter:off
		MvcResult result = this.mockMvc.perform(formLogin()
						.user("user@example.com")
						.password("password"))
				.andExpect(redirectedUrl("/second-factor"))
				.andReturn();

		HttpSession session = result.getRequest().getSession();

		Integer code = TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(hexKey) - 1;
		this.mockMvc.perform(post("/second-factor")
						.session((MockHttpSession) session)
						.param("code", String.valueOf(code))
						.with(csrf()))
				.andExpect(redirectedUrl("/third-factor"));

		this.mockMvc.perform(post("/third-factor")
						.session((MockHttpSession) session)
						.param("answer", "smith")
						.with(csrf()))
				.andExpect(redirectedUrl("/login?error"));
		// @formatter:on
	}

	@Test
	void mfaWhenWrongSecurityAnswerThenRedirects() throws Exception {
		// @formatter:off
		MvcResult result = this.mockMvc.perform(formLogin()
						.user("user@example.com")
						.password("password"))
				.andExpect(redirectedUrl("/second-factor"))
				.andReturn();

		HttpSession session = result.getRequest().getSession();

		Integer code = TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(hexKey);
		this.mockMvc.perform(post("/second-factor")
						.session((MockHttpSession) session)
						.param("code", String.valueOf(code))
						.with(csrf()))
				.andExpect(redirectedUrl("/third-factor"));

		this.mockMvc.perform(post("/third-factor")
						.session((MockHttpSession) session)
						.param("answer", "wilson")
						.with(csrf()))
				.andExpect(redirectedUrl("/login?error"));
		// @formatter:on
	}

	@Test
	void mfaWhenInProcessThenCantViewOtherPages() throws Exception {
		// @formatter:off
		MvcResult result = this.mockMvc.perform(formLogin()
						.user("user@example.com")
						.password("password"))
				.andExpect(redirectedUrl("/second-factor"))
				.andReturn();

		HttpSession session = result.getRequest().getSession();

		this.mockMvc.perform(get("/")
						.session((MockHttpSession) session))
				.andExpect(redirectedUrl("http://localhost/login"));

		result = this.mockMvc.perform(formLogin()
						.user("user@example.com")
						.password("password"))
				.andExpect(redirectedUrl("/second-factor"))
				.andReturn();

		session = result.getRequest().getSession();

		Integer code = TimeBasedOneTimePasswordUtil.generateCurrentNumberHex(hexKey);
		this.mockMvc.perform(post("/second-factor")
						.session((MockHttpSession) session)
						.param("code", String.valueOf(code))
						.with(csrf()))
				.andExpect(redirectedUrl("/third-factor"));

		this.mockMvc.perform(get("/")
						.session((MockHttpSession) session))
				.andExpect(redirectedUrl("http://localhost/login"));
		// @formatter:on
	}

}

