package org.sonatype.flexmojos.test;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.flexmojos.AbstractIrvinMojo;
import org.sonatype.flexmojos.test.report.TestCaseReport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;

/**
 * FlexTestMojo is responsible for
 *
 * @author skuenzli
 */
public abstract class FlexTestMojo extends AbstractIrvinMojo {
    protected static final String TEST_INFO = "Tests run: {0}, Failures: {1}, Errors: {2}, Time Elapsed: {3} sec";
    /**
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    protected boolean skip;

    /**
     * @parameter default-value="false" expression="${skipTests}"
     */
    protected boolean skipTest;

    /**
     * @parameter default-value="false" expression="${maven.test.failure.ignore}"
     */
    protected boolean testFailureIgnore;
    protected boolean failures = false;
    
    /**
     * Place where all test reports are saved
     */
    protected File reportPath;
    protected int numTests;
    protected int numFailures;
    protected int numErrors;

    /**
     * @parameter expression="${project.build.testOutputDirectory}"
     * @readonly
     */
    protected File testOutputDirectory;
    protected Throwable executionError;
    private int time;
    
    /**
     * Socket connect port for flex/java communication to transfer tests results
     *
     * @parameter default-value="13539" expression="${testPort}"
     */
    protected int testPort;
    /**
     * Socket connect port for flex/java communication to control if flashplayer is alive
     *
     * @parameter default-value="13540" expression="${testControlPort}"
     */
    protected int testControlPort;
    /**
     * Test timeout to wait for socket responding
     *
     * @parameter default-value="2000" expression="${testTimeout}"
     */
    protected int testTimeout;

    /**
     * Write a test report to disk.
     *
     * @param reportString the report to write.
     * @throws org.apache.maven.plugin.MojoExecutionException
     */
    protected void writeTestReport(final String reportString)
        throws MojoExecutionException
    {
        // Parse the report.
        TestCaseReport report;
        try
        {
            report = new TestCaseReport( Xpp3DomBuilder.build(new StringReader(reportString)) );
        }
        catch ( XmlPullParserException e )
        {
            // should never happen
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            // should never happen
            throw new MojoExecutionException( e.getMessage(), e );
        }

        // Get the test attributes.
        final String name = report.getName();
        final int numFailures = report.getFailures();
        final int numErrors = report.getErrors();
        final int totalProblems = numFailures + numErrors;

        getLog().debug( "[MOJO] Test report of " + name );
        getLog().debug( reportString );

        // Get the output file name.
        final File file = new File( reportPath, "TEST-" + name.replace( "::", "." ) + ".xml" );

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( file );
            IOUtil.copy(reportString, writer);
            writer.flush();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to save test result report", e );
        }
        finally
        {
            IOUtil.close( writer );
        }

        // First write the report, then fail the build if the test failed.
        if ( totalProblems > 0 )
        {
            failures = true;

            getLog().warn( "Unit test " + name + " failed." );

        }

        this.numTests += report.getTests();
        this.numErrors += report.getErrors();
        this.numFailures += report.getFailures();

    }

    @Override
    protected void setUp()
        throws MojoExecutionException, MojoFailureException
    {
        reportPath = new File( build.getDirectory(), "surefire-reports" ); // I'm not surefire, but ok

        try {
            if(reportPath.exists()) {
                getLog().debug("Test result report directory already exists: " + reportPath.getCanonicalPath());
            } else if (reportPath.mkdirs()) {
                getLog().debug("Successfully created test result report directory: " + reportPath.getCanonicalPath());
            } else {
                String msg = "Failed to create test result report directory: " + reportPath.getCanonicalPath();
                getLog().error(msg);
                throw new MojoExecutionException(msg);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create/verify test result report directory", e);
        }
    }

    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        setUp();

        if ( skip || skipTest )
        {
            getLog().info( "Skipping test phase." );
        }
        else if ( testOutputDirectory == null || !testOutputDirectory.isDirectory() )
        {
            getLog().warn( "Skipping test run. Runner not found: " + testOutputDirectory );
        }
        else
        {
            run();
            tearDown();
        }
    }

    @Override
    protected void tearDown()
        throws MojoExecutionException, MojoFailureException
    {

        getLog().info( "------------------------------------------------------------------------" );
        getLog().info(
                       MessageFormat.format(TEST_INFO, new Object[]{new Integer(numTests),
                           new Integer(numErrors), new Integer(numFailures), new Integer(time)}) );

        if ( !testFailureIgnore )
        {
            if ( executionError != null )
            {
                throw new MojoExecutionException( executionError.getMessage(), executionError );
            }

            if ( failures )
            {
                throw new MojoExecutionException( "Some tests fail" );
            }
        }
        else
        {
            if ( executionError != null )
            {
                getLog().error( executionError.getMessage(), executionError );
            }

            if ( failures )
            {
                getLog().error( "Some tests fail" );
            }
        }

    }
}
