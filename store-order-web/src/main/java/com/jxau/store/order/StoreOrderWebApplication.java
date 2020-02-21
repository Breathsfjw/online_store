package com.jxau.store.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
public class StoreOrderWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreOrderWebApplication.class, args);
    }

}
