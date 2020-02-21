package com.jxau.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.jxau.store.order.mapper")
public class StoreOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreOrderServiceApplication.class, args);
    }

}
