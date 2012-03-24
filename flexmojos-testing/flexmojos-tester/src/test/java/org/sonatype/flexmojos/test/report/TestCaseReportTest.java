package org.sonatype.flexmojos.test.report;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.testng.Assert.*;

public class TestCaseReportTest {

    @Test
    public void test_errors_reported_correctly_when_no_errors_exist() throws Exception {
        int expectedTests = 0;
        int expectedFailures = 0;
        int expectedErrors = 0;

        TestCaseReport report = new TestCaseReport(createTestReport(expectedTests, expectedFailures, expectedErrors));
        assertEquals(report.getErrors(), expectedErrors);

        assertEquals(report.getProblems(), expectedErrors);
        assertFalse(report.hasProblems());
        
    }

    @Test
    public void test_errors_reported_correctly_when_errors_exist() throws Exception {
        int expectedTests = 0;
        int expectedFailures = 0;
        int expectedErrors = 2;

        TestCaseReport report = new TestCaseReport(createTestReport(expectedTests, expectedFailures, expectedErrors));
        assertEquals(report.getErrors(), expectedErrors);

        assertEquals(report.getProblems(), expectedErrors);
        assertTrue(report.hasProblems());

    }

    @Test
    public void test_failures_reported_correctly_when_no_failures_exist() throws Exception {
        int expectedTests = 0;
        int expectedFailures = 0;
        int expectedErrors = 0;

        TestCaseReport report = new TestCaseReport(createTestReport(expectedTests, expectedFailures, expectedErrors));
        assertEquals(report.getFailures(), expectedFailures);

        assertEquals(report.getProblems(), expectedFailures);
        assertFalse(report.hasProblems());

    }

    @Test
    public void test_failures_reported_correctly_when_failures_exist() throws Exception {
        int expectedTests = 0;
        int expectedFailures = 3;
        int expectedErrors = 0;

        TestCaseReport report = new TestCaseReport(createTestReport(expectedTests, expectedFailures, expectedErrors));
        assertEquals(report.getFailures(), expectedFailures);

        assertEquals(report.getProblems(), expectedFailures);
        assertTrue(report.hasProblems());
    }

    @Test
    public void test_num_tests_reported_correctly() throws Exception {
        int expectedTests = 42;
        int expectedFailures = 0;
        int expectedErrors = 0;

        TestCaseReport report = new TestCaseReport(createTestReport(expectedTests, expectedFailures, expectedErrors));
        assertEquals(report.getTests(), expectedTests);

        assertEquals(report.getProblems(), 0);
        assertFalse(report.hasProblems());
    }

    private Xpp3Dom createTestReport(int numTests, int numfailures, int numErrors) throws XmlPullParserException,
        IOException {
        return Xpp3DomBuilder.build(new StringReader(
            "<testsuite " +
                " tests='" + numTests + "'" +
                " errors='" + numErrors + "'" +
                " failures='" + numfailures + "'" +
                "/>")
        );
    }


}
