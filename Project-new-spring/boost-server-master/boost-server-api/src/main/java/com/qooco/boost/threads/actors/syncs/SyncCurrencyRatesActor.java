package com.qooco.boost.threads.actors.syncs;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.business.BusinessCurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SyncCurrencyRatesActor extends UntypedAbstractActor {

    protected Logger logger = LogManager.getLogger(SyncCurrencyRatesActor.class);
    public static final String ACTOR_NAME = "syncCurrencyRatesActor";
    public static final String SYNC_CURRENCY_RATE = "SYNC_CURRENCY_RATE";

    private final BusinessCurrencyExchangeService currencyExchangeService;
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof String && SYNC_CURRENCY_RATE.equals(message)) {
            currencyExchangeService.syncCurrencyExchangeRates();
        }
    }
}
