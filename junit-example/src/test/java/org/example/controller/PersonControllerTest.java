package org.example.controller;

import org.example.service.PersonService;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PersonService personService;  // 使用@Mock注解创建模拟对象

    @InjectMocks
    private PersonController personController;  // 使用@InjectMocks注解注入模拟对象

    /**
     * `MockitoAnnotations.initMocks(this)` 和 `MockitoAnnotations.openMocks(this).close()` 都是用于初始化模拟对象的方法，但它们的工作方式和用途略有不同。
     * <p>
     * 1. `MockitoAnnotations.initMocks(this)`：
     * - 这是 Mockito 1.x 版本中的初始化模拟对象的方法。
     * - 它会初始化标记了 `@Mock`、`@InjectMocks` 注解的模拟对象，并将它们注入到测试类中。
     * - 它不会管理模拟对象的生命周期，也就是说，它不会在测试结束后自动关闭模拟对象。
     * <p>
     * 2. `MockitoAnnotations.openMocks(this).close()`：
     * - 这是 Mockito 2.x 版本及更高版本中的初始化模拟对象的方法。
     * - 它不仅会初始化模拟对象，还会将它们注入到测试类中。
     * - 与 `initMocks` 不同，它会管理模拟对象的生命周期，包括在测试完成后自动关闭（释放）这些模拟对象，以避免可能的资源泄漏。
     * <p>
     * 如果你的项目正在使用 Mockito 2.x 或更高版本，建议使用 `openMocks(this).close()`，因为它提供了更好的资源管理，并且更安全。如果你的项目使用 Mockito 1.x，那么你需要继续使用 `initMocks(this)`。
     * <p>
     * 当将 `initMocks(this)` 替换为 `openMocks(this).close()` 时，需要注意以下事项：
     * - 确保你的项目已经使用了 Mockito 2.x 或更高版本。
     * - 由于 `openMocks` 会自动关闭模拟对象，因此不需要手动调用 `close` 方法来释放资源。
     * - 在模拟对象之间存在依赖关系时，`openMocks` 会更好地处理依赖注入，因此不再需要手动处理依赖关系。
     * <p>
     * 总之，使用 `openMocks(this).close()` 是一种更现代、更安全的方式来初始化和管理模拟对象的生命周期，特别是在 Mockito 2.x 或更高版本中。
     */
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);  // 初始化模拟对象
        this.mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
    }

    @Test
    public void getAllPersons() throws Exception {

//        when(personService.getAllPerson()).thenReturn(Arrays.asList(new Person(), new Person()));

        MockHttpServletRequestBuilder requestGet = get("/persons");

//        MockHttpServletRequestBuilder requestPost = post("/persons")
//                .param("param1", "value1")
//                .param("param2", "value2")
//                .contentType("application/json")
//                .cookie((Cookie) null);

        mockMvc.perform(requestGet)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        MockHttpServletResponse response = mockMvc.perform(requestGet).andReturn().getResponse();

        // 解析响应体
        JSONArray jsonArray = new JSONArray(response.getContentAsString());
        System.out.println("jsonArray.length() = " + jsonArray.length());

    }
}
