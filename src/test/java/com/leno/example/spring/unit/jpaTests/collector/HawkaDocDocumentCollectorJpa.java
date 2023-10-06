package com.leno.example.spring.unit.jpaTests.collector;

import com.leno.example.domain.dao.documentCollector.DocumentDao;
import com.leno.example.domain.repository.documentCollector.DocumentRepositoryBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class HawkaDocDocumentCollectorJpa {

    private int NUMBER_OF_TOTAL_ELEMENTS;

    @Autowired
    DocumentRepositoryBase hawkadocDocumentRepositoryBase;

    @BeforeEach
    void setup(){


        List<DocumentDao> list = List.of(new DocumentDao[]{

                new DocumentDao()
        });

        hawkadocDocumentRepositoryBase.saveAll(list);

        NUMBER_OF_TOTAL_ELEMENTS = list.size();

        Page<DocumentDao> daos = hawkadocDocumentRepositoryBase.findAll(PageRequest.of(0,NUMBER_OF_TOTAL_ELEMENTS));
    }

}
