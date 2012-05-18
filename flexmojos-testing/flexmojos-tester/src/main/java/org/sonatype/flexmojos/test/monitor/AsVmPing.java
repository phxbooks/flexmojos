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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.flexmojos.test.ThreadStatus;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.sonatype.flexmojos.test.monitor.CommConstraints.*;

/**
 * This class will ping Action Script virtual machine to make sure if the application still running
 * 
 * @author velo
 */
@Component( role = AsVmPing.class, instantiationStrategy = "per-lookup" )
public class AsVmPing
    extends AbstractSocketThread
{

    private int testControlPort;

    private int firstConnectionTimeout;

    private int testTimeout;

    private Document testSpecs;


    @Override
    protected void handleRequest()
        throws IOException
    {
        getLogger().debug( "[CONTROL] AsVmControl handleRequest" );

        clientSocket.setSoTimeout( testTimeout );

        int errorCount = 0;

        while ( true )
        {
            try
            {
                getLogger().debug( "[CONTROL] query status" );
                IOUtil.copy( STATUS + EOL, out );

                getLogger().debug( "[CONTROL] received status" );
                BufferedReader in = new BufferedReader( new InputStreamReader( super.in ) );
                String result = in.readLine();
                getLogger().debug( "[CONTROL] status is: '" + result + "'");
                if ( !OK.equals( result )
                    && !FINISHED.equals( result )
                    && !TEST_SPEC_LIST.equals( result ))
                {
                    getLogger().debug("encountered error status: " + result);
                    errorCount++;
                    if ( errorCount >= 3 )
                    {
                        status = ThreadStatus.ERROR;
                        error = new Error( "Invalid virtual machine status: " + result );

                        return;
                    }
                }
                else if (TEST_SPEC_LIST.equals(result))
                {
                    getLogger().debug( "Reading test suites" );

                    StringBuilder testListBuilder = new StringBuilder();
                    String line;
                    do
                    {
                        line = in.readLine();
                        getLogger().debug("read: " + line);
                        testListBuilder.append(line);
                    } while (!line.endsWith(END_OF_TEST_SPECS));

                    String testSuitesXmlStr = testListBuilder.toString();
                    getLogger().debug( "testSuitesXmlStr: " + testSuitesXmlStr );
                    testSpecs = convertToDocument(testListBuilder.toString());
                    getLogger().debug("testSuites.toString(): " + testSpecs.toString());

                    status = ThreadStatus.DONE;
                    return;
                }

                else if ( FINISHED.equals( result ) )
                {
                    getLogger().debug( "[CONTROL] FINISHED received, terminating the thread" );
                    return;
                }
                else
                {
                    errorCount = 0;

                    try
                    {
                        Thread.sleep( 2000 );
                    }
                    catch ( InterruptedException e )
                    {
                        // wake up call, no problem
                    }
                }
            }
            catch ( SocketTimeoutException e )
            {
                errorCount++;
                if ( errorCount >= 3 )
                {
                    status = ThreadStatus.ERROR;
                    error = e;

                    return;
                }
            }
            catch ( SocketException e )
            {
                if ( !e.getMessage().contains( "Broken pipe" ) )
                {
                    throw e;
                }
                else
                {
                    // Broken pipe is not a real error :D
                    return;
                }
            }
        }
    }

    private Document convertToDocument(String xml) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start(int testControlPort, int firstConnectionTimeout, int testTimeout)
    {
        reset();
        this.testControlPort = testControlPort;
        this.firstConnectionTimeout = firstConnectionTimeout;
        this.testTimeout = testTimeout;

        getLogger().debug("AsVmPing configuration:"
            + " testControlPort: " + testControlPort
            + " firstConnectionTimeout: " + firstConnectionTimeout
            + " testTimeout: " + testTimeout);

        launch();
    }

    @Override
    protected int getTestPort()
    {
        return testControlPort;
    }

    @Override
    protected int getFirstConnectionTimeout()
    {
        return firstConnectionTimeout;
    }

    public Document getTestSpecs() {
        return testSpecs;
    }

    public void clearTestSpecs() {
        testSpecs = null;
    }
}
