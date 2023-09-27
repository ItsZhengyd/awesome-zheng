package org.example.service;

import org.example.entities.Person;
import org.example.entities.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private PersonRepo repo;

    public List<Person> getAllPerson() {
        return this.repo.findAll();
    }

    public Person savePerson(Person person) {
        return this.repo.save(person);
    }

    @Autowired
    public PersonService(PersonRepo repo) {
        this.repo = repo;
    }
}
