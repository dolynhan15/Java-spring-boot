package com.qooco.boost.core.thread;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;

public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt> {

    public static final SpringExtension SPRING_EXTENSION_PROVIDER = new SpringExtension();

    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    public static class SpringExt implements Extension {

        private volatile ApplicationContext context;

        public void initialize(ApplicationContext applicationContext) {
            this.context = applicationContext;
        }

        public Props props(String actorName) {
            return Props.create(SpringActorProducer.class, context, actorName);
        }
    }
}