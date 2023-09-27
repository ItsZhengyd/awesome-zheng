## JUnit assert method sample
- assertArrayEquals("message", A, B)
- assertEquals("message", A, B)
- assertSame("message", A, B)
- assertTrue("message", A)
- assertNotNull("message", A)

测试demo
- 简单方法测试 `SimpleFunctionTest.java`
- 参数化测试 `ParameterizedTest.java`
- 套件测试 `MasterSuiteTest.java`
- service 测试 `PersonServiceTest.java`
- controller 单元测试 `PersonControllerTest.java`
- controller 集成测试 `PersonControllerIntegrationTest.java` (使用`org.springframework.test.web.servlet.MockMvc`)
- controller 集成测试 `PersonControllerIntegrationTest2.java` (使用`org.springframework.boot.test.web.client.TestRestTemplate`)

测试 demo 参考：
- https://www.geeksforgeeks.org/unit-testing-in-spring-boot-project-using-mockito-and-junit/
- https://www.springboottutorial.com/spring-boot-unit-testing-and-mocking-with-mockito-and-junit