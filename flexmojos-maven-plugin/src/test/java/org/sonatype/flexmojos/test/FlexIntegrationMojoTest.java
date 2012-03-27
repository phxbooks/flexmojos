package org.sonatype.flexmojos.test;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class FlexIntegrationMojoTest {

  @Test
  public void test_initial_state(){
    FlexIntegrationMojo mojo = new FlexIntegrationMojo();

    assertNotNull(mojo.getBrowserCommand());
    assertNull(mojo.getTestTargetURL());
    assertFalse(mojo.isTestCommandExitsWhenTestCompletes());
  }

  @Test
  public void test_isIntegrationTestConfigured_when_Configured(){
    FlexIntegrationMojo mojo = new FlexIntegrationMojo();

    mojo.setBrowserCommand(anyString());
    mojo.setTestTargetURL(anyString());

    assertTrue(mojo.isIntegrationTestConfigured());
  }

  @Test
  public void test_isIntegrationTestConfigured_when_notConfigured(){
    FlexIntegrationMojo mojo = new FlexIntegrationMojo();

    mojo.setBrowserCommand(null);
    mojo.setTestTargetURL(null);

    assertFalse(mojo.isIntegrationTestConfigured());

    mojo.setBrowserCommand(anyString());
    mojo.setTestTargetURL(null);

    assertFalse(mojo.isIntegrationTestConfigured());

    mojo.setBrowserCommand(null);
    mojo.setTestTargetURL(anyString());

    assertFalse(mojo.isIntegrationTestConfigured());
  }
  
  private String anyString(){
    return String.valueOf(Math.random());
  }
}
