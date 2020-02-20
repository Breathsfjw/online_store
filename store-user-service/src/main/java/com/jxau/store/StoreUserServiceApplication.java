package com.jxau.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.jxau.store.user.mapper")
public class StoreUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreUserServiceApplication.class, args);
    }

}
