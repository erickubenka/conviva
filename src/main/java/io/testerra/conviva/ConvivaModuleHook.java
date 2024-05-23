package io.testerra.conviva;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverFactory;

public class ConvivaModuleHook extends AbstractModule implements ModuleHook {

    @Override
    protected void configure() {
        Multibinder<WebDriverFactory> webDriverFactoryBinder = Multibinder.newSetBinder(binder(), WebDriverFactory.class);
        webDriverFactoryBinder.addBinding().to(DesktopWebDriverFactory.class).in(Scopes.SINGLETON);
    }

    @Override
    public void init() {

    }

    @Override
    public void terminate() {

    }
}
