/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.web;

import jakarta.annotation.Resource;
import org.example.entity.CustomUser;
import org.example.entity.Orders;
import org.example.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * OAuth2 Log in controller.
 *
 * @author Joe Grandja
 * @author Rob Winch
 */
@Controller
public class OAuth2LoginController {

    @Resource
    private OrdersService ordersService;

    public static String  privateKeyStr = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDWKQ+zKhzAT3/d\n" +
            "V3SKl+qtYBXTGlds4sqAQifTqGEDPXqSdu60qKS53sRogwuMAXhaYEyCvpE7gXZ+\n" +
            "VEMa/bOHD8YurFgXbu0H5M/SntjsSkamgrfc2QlnLwVe9ss47RsIqM7a3DTOnb1+\n" +
            "p/C1w8g+WGnoKqbqCY7UViYgoEPhtYQ3Udp5WFQzPUC84LaRlhkjHo/qVNVnEIf/\n" +
            "cun2SfdvWlu+hEZsT/GW0w8CxcEJ2Y/gBYgBCCIEHsokSjlnUU9k9E7AIz+UyJis\n" +
            "y/TVGE+V6NHKESnpsGhFnS411Gaugtq/JRMxYmi73R7qYbtKMM3tXb4HvR+4rYbI\n" +
            "i1qAX8I3AgMBAAECggEBAL+zccJG6AWdJC/VoRSOeBtJ7z9QTQHC4NF+ZngoOO7V\n" +
            "kWqZ24MLkqhuqBGFfEiO6FS97r76JrbFZHQgqRD4GEPF8vjEfPY87SAryXwXhMW+\n" +
            "lw9l439lJ+2dQYmIENGcLJ7LvJ5cj0iEGqPNYMLTxeNI8URJKbBFbfzUQLzjuN/I\n" +
            "H7drurcqIdY11MDOd3+KdhAyNS+4njTlg6J/Sm3HepoPoXk+ZrcQhO0EPE+T5PNP\n" +
            "esVUGCeFXyqPEqHnQU5xBEdjsNJFsHLOH6dbh2m81q0VyBp3+65yikQmUDmL3cyJ\n" +
            "pCrkPXYPnUX4k1NGsfUPv6Z4KdYTM1evc1awcdb2VkECgYEA+mePYBGVw6R3giH9\n" +
            "wtV9CpxxOQn9b4LS4+X7+fqhrAN1ePMpBaosuA2p8qMEj7kxM3ILJI+V+HNF2mRU\n" +
            "5k36+1KjMiEblLjCm26k9YghHEUsn2bzwqUq1bHne39XYbXl0Is0/7UGJs5ZkADq\n" +
            "huLThU34I/fCIo9s4iFWDQYEAYUCgYEA2vIqpGzV+Hw8f2wyaE9pkjuDY12uKPu4\n" +
            "LNg0Kq5AidXEt77k1gwmzZpL8iaAEICj+H6H24UleCZplW6A4ixImL00ag/eJZF+\n" +
            "zeo2kqK4/OGYUiegbmVzRrjISVFwYnXOOdYPqzKSbKJTYDg3HurUn2sZ++PL42Js\n" +
            "jDBbEt2S44sCgYEArVmoPj+uSITBX0uc25bkO8ZV88DgvKP6z17V9Bb4eZbjaloc\n" +
            "GhnXX4vGDX2hmMYCM7VN1X+5uQhEYY534AA4MmjhJcEZ0Pmfb+9HL9uP4HxbCfdB\n" +
            "5YxmfQ3uTOa5XaGJebgFdsihe5f7FOAtfDfnay+xC2Vn9nkITfv6EIYLm+kCgYAZ\n" +
            "GXPr/5IT92IUFXo93QS0P+BTDtU9W4YElhB86Bb79iakDd078I6uOUcFjoZV3flu\n" +
            "LksyzjO6b2ThPZbG1t7Hq8ELe6Ay3FgWEQiKjN76Fn6YxHQu07CAZgSH6y8gCnNG\n" +
            "zBRlwtloXL+EI02mXLNdRzDmYHnqKklZVN3L7ty8+wKBgQDnMEqD2oK79C4s4OLp\n" +
            "UVR3LEgEZC/coLS3iZorrjmyEd5eelFWsUg6kTsO8NND0FhqtJzB7DPhU78sYzqj\n" +
            "fEoai9hvw9+ma66tBZh5+F0lgi0MapVFfhq7yQZDXwH78LvYVnLer/9pJIFACnsv\n" +
            "p3GhB78svPMT1g6E0xvEI0mOnw==\n" +
            "-----END PRIVATE KEY-----";
    @GetMapping("/")
    public String index(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                        @AuthenticationPrincipal OAuth2User oauth2User) {
        model.addAttribute("userName", oauth2User.getName());
        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
        model.addAttribute("userAttributes", oauth2User.getAttributes());
        model.addAttribute("accessToken", authorizedClient.getAccessToken().getTokenValue());
        model.addAttribute("refreshToken", authorizedClient.getRefreshToken().getTokenValue());
        return "index";
    }

    @GetMapping("/orders")
    @ResponseBody
    public List<Orders> getOrders(){
        String userName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
            userName = oAuth2User.getName();
        }

        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken)authentication;
            CustomUser customUser = (CustomUser)usernamePasswordAuthenticationToken.getPrincipal();
            userName = customUser.getName();
        }

        return ordersService.findAllByUserName(userName);

    }

}
