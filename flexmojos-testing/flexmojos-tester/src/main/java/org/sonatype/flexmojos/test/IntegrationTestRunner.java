package org.sonatype.flexmojos.test;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.flexmojos.test.launcher.LaunchFlashPlayerException;

import java.util.List;

@Component( role = IntegrationTestRunner.class )
public class IntegrationTestRunner extends DefaultTestRunner {

    @Override
    public List<String> run(TestRequest testRequest)
        throws TestRunnerException, LaunchFlashPlayerException {

        getLogger().info(getClass() + " is running");

        disableAutomaticTearDownOfSocketThreads();

        return super.run(testRequest);
    }
}
