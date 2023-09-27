package org.example.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.runners.Suite.SuiteClasses;

/**
 * 测试顺序为SuiteClasses的value的填充顺序
 */
@RunWith(value = Suite.class)
@SuiteClasses(value = {CaseOneTest.class, CaseTwoTest.class})
public class MasterSuiteTest {
}
