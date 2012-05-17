/**
 *   Copyright 2008 Marvin Herman Froeder
 * -->
 * <!--
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * -->
 *
 * <!--
 *     http://www.apache.org/licenses/LICENSE-2.0
 * -->
 *
 * <!--
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonatype.flexmojos.test.monitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.flexmojos.test.ControlledThread;

import static org.sonatype.flexmojos.test.monitor.CommConstraints.*;

/**
 * Create a server socket for receiving the test reports from FlexUnit. We read the test reports inside of a Thread.
 */
@Component( role = ResultHandler.class, instantiationStrategy = "per-lookup" )
public class ResultHandler
    extends AbstractSocketThread
    implements ControlledThread
{
    public static final String ROLE = ResultHandler.class.getName();

    private int testReportPort;

    protected List<String> testReportData;

    public List<String> getTestReportData()
    {
        return testReportData;
    }

    protected void handleRequest()
        throws IOException
    {
        StringBuffer buffer = new StringBuffer();
        int bite = -1;

        while ( ( bite = in.read() ) != -1 )
        {
            final char chr = (char) bite;

            if ( chr == NULL_BYTE )
            {
                final String data = buffer.toString();
                getLogger().debug( "[RESULT] Received data: " + data );
                buffer = new StringBuffer();

                if ( data.endsWith( END_OF_TEST_SUITE ) )
                {
                    getLogger().debug( "[RESULT] End test suite" );

                    this.testReportData.add( data );
                }
                else if ( data.equals( END_OF_TEST_RUN ) )
                {
                    getLogger().debug( "[RESULT] End test run - sending ACK: " + ACK_OF_TEST_RESULT );

                    // Sending the acknowledgement to testrunner

                    BufferedWriter out = new BufferedWriter( new OutputStreamWriter( super.out ) );
                    out.write( ACK_OF_TEST_RESULT + NULL_BYTE );
                    out.flush();
                    break;
                }
            }
            else
            {
                buffer.append( chr );
            }
        }

        getLogger().debug( "[RESULT] Socket buffer " + buffer );
    }

    public void start(int testPort)
    {
        reset();

        testReportPort = testPort;
        testReportData = new ArrayList<String>();

        launch();
    }

    @Override
    protected void reset()
    {
        super.reset();

        testReportData = null;
    }

    @Override
    protected int getTestPort()
    {
        return testReportPort;
    }

    @Override
    protected int getFirstConnectionTimeout()
    {
        return 0; // no timeout
    }
}
