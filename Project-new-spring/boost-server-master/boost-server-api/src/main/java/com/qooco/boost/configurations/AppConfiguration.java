package com.qooco.boost.configurations;

import akka.actor.ActorSystem;
import com.google.common.base.Predicates;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.thread.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.qooco.boost.core.enumeration.ApplicationConfig.*;

@Configuration
@ComponentScan
@EnableSwagger2
public class AppConfiguration {
    @Value(ApplicationConstant.BOOST_PATA_SWAGGER_ENABLE)
    private boolean isEnable;
    @Value(ApplicationConstant.BOOST_PATA_SWAGGER_ENABLE_PARAM)
    private List<String> params;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("boost-actors");
        SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .initialize(applicationContext);
        return system;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(isEnable)
                .globalOperationParameters(getListParameter())
                .ignoredParameterTypes(Authentication.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build();
    }

    private List<Parameter> getListParameter() {

        List<Parameter> aParameters = new ArrayList<>();
        if (params.contains(BOOST_CORE_SECURITY_TOKEN_NAME.value())) {
            aParameters.add(initParam(BOOST_CORE_SECURITY_TOKEN_NAME.value()));
        }
        if (params.contains(BOOST_CORE_SECURITY_APP_VERSION_CODE.value())) {
            aParameters.add(initParam(BOOST_CORE_SECURITY_APP_VERSION_CODE.value()));
        }
        if (params.contains(BOOST_CORE_SECURITY_APP_LOCALE.value())) {
            aParameters.add(initParam(BOOST_CORE_SECURITY_APP_LOCALE.value()));
        }
        if (params.contains(BOOST_CORE_SECURITY_TIMEZONE.value())) {
            aParameters.add(initParam(BOOST_CORE_SECURITY_TIMEZONE.value()));
        }
        if (params.contains(BOOST_CORE_SECURITY_APP_ID.value())) {
            aParameters.add(initParam(BOOST_CORE_SECURITY_APP_ID.value()));
        }
        return aParameters;
    }

    private Parameter initParam(String name) {
        String TYPE = "header";
        String MODEL_TYPE = "string";
        return (new ParameterBuilder())
                .name(name)                 // name of header
                .modelRef(new ModelRef(MODEL_TYPE))
                .parameterType(TYPE)               // type - header
                .required(false)                // for compulsory
                .build();
    }
}
