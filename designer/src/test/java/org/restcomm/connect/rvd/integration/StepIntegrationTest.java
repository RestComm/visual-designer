package org.restcomm.connect.rvd.integration;

import org.junit.Before;
import org.restcomm.connect.rvd.ApplicationContext;
import org.restcomm.connect.rvd.ApplicationContextBuilder;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class StepIntegrationTest {

    ApplicationContext appContext;

    @Before
    public void before() {
        appContext = buildDefaultApplicationContext();
    }

    private ApplicationContext buildDefaultApplicationContext() {
        return new ApplicationContextBuilder().build();
    }

}
