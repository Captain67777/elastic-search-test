package com.lilium.elasticsearch.repository;

import com.lilium.elasticsearch.document.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// used for crud tasks on Peron Document.
// ElasticsearchRepository contains out-of-the-box crud tasks,
// Custom crud tasks will be added to PersonRepository
public interface PersonRepository extends ElasticsearchRepository<Person, String> {
}