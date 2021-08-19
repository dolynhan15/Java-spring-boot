package com.example.hellospringboot;

import com.example.hellospringboot.other_package.Bikini;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
public class HelloSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSpringBootApplication.class, args);
//        ApplicationContext context= SpringApplication.run(HelloSpringBootApplication.class, args);
//        Dress dress = context.getBean(Dress.class);
//        System.out.println("Dress: "+ dress);
//
//        Bikini bikini = context.getBean(Bikini.class);
//        System.out.println("Bikini: "+ bikini);
//
//        GirlFriend girlFriend = context.getBean(GirlFriend.class);
//
//        System.out.println("Instance girlFriend: "+ girlFriend);
//        System.out.println("Outfit of girlFriend: "+ girlFriend.outfit);
//        girlFriend.outfit.wear();

//        Dress dress1 = context.getBean(Dress.class);
//        Dress dress2 = context.getBean(Dress.class);
//
//        System.out.println(dress1);
//        System.out.println(dress2);
    }

}
