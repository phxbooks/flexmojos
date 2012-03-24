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
package org.sonatype.flexmojos.test;

import java.io.File;

public class TestRequest
{

    private int firstConnectionTimeout;

    private File fileUnderTest;

    private int testControlPort;

    private int testPort;

    private int testTimeout;

    private String testCommand;

    private boolean allowHeadlessMode = true;

    /**
     * @return the firstConnectionTimeout
     */
    public int getFirstConnectionTimeout()
    {
        return firstConnectionTimeout;
    }

    public File getFileUnderTest()
    {
        return fileUnderTest;
    }

    /**
     * @return the testControlPort
     */
    public int getTestControlPort()
    {
        return testControlPort;
    }

    /**
     * @return the testPort
     */
    public int getTestPort()
    {
        return testPort;
    }

    /**
     * @return the testTimeout
     */
    public int getTestTimeout()
    {
        return testTimeout;
    }

    /**
     * @param firstConnectionTimeout the firstConnectionTimeout to set
     */
    public void setFirstConnectionTimeout( int firstConnectionTimeout )
    {
        this.firstConnectionTimeout = firstConnectionTimeout;
    }

    public void setFileUnderTest(File fileUnderTest)
    {
        this.fileUnderTest = fileUnderTest;
    }

    /**
     * @param testControlPort the testControlPort to set
     */
    public void setTestControlPort( int testControlPort )
    {
        this.testControlPort = testControlPort;
    }

    /**
     * @param testPort the testPort to set
     */
    public void setTestPort( int testPort )
    {
        this.testPort = testPort;
    }

    /**
     * @param testTimeout the testTimeout to set
     */
    public void setTestTimeout( int testTimeout )
    {
        this.testTimeout = testTimeout;
    }

    public String getTestCommand()
    {
        return this.testCommand;
    }

    public void setTestCommand(String testCommand)
    {
        this.testCommand = testCommand;
    }

    public boolean getAllowHeadlessMode()
    {
        return this.allowHeadlessMode;
    }

    public void setAllowHeadlessMode( boolean allowHeadlessMode )
    {
        this.allowHeadlessMode = allowHeadlessMode;
    }
}
