package com.qooco.boost.message.config;

import akka.actor.ActorSystem;
import com.google.common.base.Predicates;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.message.constant.ApplicationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan
public class BoostConfig {
    @Value(ApplicationConstant.BOOST_PATA_SWAGGER_ENABLE)
    private boolean isEnable;
    @Autowired
    private ApplicationContext context;

    @Bean
    public Docket api() {


        return new Docket(DocumentationType.SWAGGER_2)
                .enable(isEnable)
                .ignoredParameterTypes(Authentication.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build();
    }

    /**
     * AKKA initiation actor bean name
     */
    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("boost-server-message-actors");
        SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).initialize(context);
        return system;
    }
}