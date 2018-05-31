package com.hand.sxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author spilledyear
 * <p>
 * springboot2 和 非springboot2 有很大的区别。在 1.5版本中，使用授权码的形式可以访问成功，但是在 2.0.2中，根本不行
 */
@SpringBootApplication
public class Oauth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServerApplication.class, args);
    }
}
