package com.jxau.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.jxau.store.cart.mapper")
public class StoreCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreCartServiceApplication.class, args);
    }

}
