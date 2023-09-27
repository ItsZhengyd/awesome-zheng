package org.example.controller;

import org.example.entities.Person;
import org.example.entities.PersonRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

// 集成测试
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepo repo;

    @BeforeEach
    public void setUp(){
        Person person = new Person();
        person.setPersonName("wang");
        repo.save(person);
    }

    @Test
    public void getAllPersons() throws Exception {

        // 发起请求
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/persons"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn();

        System.out.println("mvcResult.getResponse().getContentAsString() = " + mvcResult.getResponse().getContentAsString());
    }
}
