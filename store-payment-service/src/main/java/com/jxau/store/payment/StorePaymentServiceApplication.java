package com.jxau.store.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.jxau.store.payment.mapper")
public class StorePaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorePaymentServiceApplication.class, args);
    }

}
