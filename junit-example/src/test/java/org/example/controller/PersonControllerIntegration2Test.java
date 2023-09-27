package org.example.controller;

import org.example.entities.Person;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerIntegration2Test {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

//    @Sql({"classpath:person.sql"})
    @Test
    @Order(2)
    public void testAllEmployees() {
        Person[] persons = this.restTemplate.getForObject("http://localhost:" + port + "/persons", Person[].class);
        assertTrue(persons != null && persons.length == 1);
    }

    @Test
    @Order(1)
    public void testAddEmployee() {
        Person person = new Person(1,"zheng","xianyang");
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity("http://localhost:" + port + "/persons", person, String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }
}

