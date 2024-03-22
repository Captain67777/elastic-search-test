package com.lilium.elasticsearch.service;

import com.lilium.elasticsearch.document.Person;
import com.lilium.elasticsearch.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // a specialization of @Component annotation. Spring Service annotation can be applied only to classes.
public class PersonService {

    private final PersonRepository repository;

    @Autowired
    // inject (implicit instantiating) dependency PersonRepository
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public void save(final Person person) {
        repository.save(person);
    }

    public Person findById(final String id) {
        return repository.findById(id).orElse(null);
    }
}