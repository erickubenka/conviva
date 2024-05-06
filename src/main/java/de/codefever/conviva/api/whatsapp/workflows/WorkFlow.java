package de.codefever.conviva.api.whatsapp.workflows;

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;

public interface WorkFlow<T> extends WebDriverManagerProvider, PageFactoryProvider, PropertyManagerProvider, Loggable {

    T run();
}
