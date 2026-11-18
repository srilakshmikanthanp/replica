package com.srilakshmikanthanp.replica.integration;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.srilakshmikanthanp.replica.integration")
@IncludeClassNamePatterns(".*Test")
public class IntegrationTestSuite {
}
