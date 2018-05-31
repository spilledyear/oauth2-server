package com.hand.sxy.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author spilledyear
 */
@RestController
public class TestEndpoints {
    private Logger logger = LoggerFactory.getLogger(TestEndpoints.class);

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "product id : " + id;
    }

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "order id : " + id;
    }

    @RequestMapping(value = "/api/system/login", method = RequestMethod.POST)
    public String login() {
        return "6666";
    }

    @RequestMapping("/aiqiyi/qq/redirect")
    public String getToken(@RequestParam String code) {
        logger.info("receive code {}", code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", "aiqiyi");
        params.add("client_secret", "secret");
        params.add("redirect_uri", "http://localhost:7070/aiqiyi/qq/redirect");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate().postForEntity("http://localhost:7070/oauth/token", requestEntity, String.class);
        String token = response.getBody();
        logger.info("token => {}", token);
        return token;
    }


}
