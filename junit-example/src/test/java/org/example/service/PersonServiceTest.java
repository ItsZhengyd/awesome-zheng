package org.example.service;

import org.example.entities.PersonRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepo personRepo;

    private PersonService personService;

    @BeforeEach
    void setUp() {
        this.personService = new PersonService(this.personRepo);
    }

    /**
     * 验证在执行 personService.getAllPerson(); 时 personRepo 的 findAll()有被调用到
     * 确保交互正确
     */
    @Test
    void getAllPerson() {
        personService.getAllPerson();
        Mockito.verify(personRepo).findAll();
    }
}
