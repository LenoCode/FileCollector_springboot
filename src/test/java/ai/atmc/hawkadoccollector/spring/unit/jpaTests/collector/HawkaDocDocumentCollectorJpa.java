package ai.atmc.hawkadoccollector.spring.unit.jpaTests.collector;

import ai.atmc.hawkadoccollector.domain.dao.documentCollector.HawkadocDocumentDao;
import ai.atmc.hawkadoccollector.domain.repository.documentCollector.HawkadocDocumentRepositoryBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class HawkaDocDocumentCollectorJpa {

    private int NUMBER_OF_TOTAL_ELEMENTS;

    @Autowired
    HawkadocDocumentRepositoryBase hawkadocDocumentRepositoryBase;

    @BeforeEach
    void setup(){


        List<HawkadocDocumentDao> list = List.of(new HawkadocDocumentDao[]{

                new HawkadocDocumentDao()
        });

        hawkadocDocumentRepositoryBase.saveAll(list);

        NUMBER_OF_TOTAL_ELEMENTS = list.size();

        Page<HawkadocDocumentDao> daos = hawkadocDocumentRepositoryBase.findAll(PageRequest.of(0,NUMBER_OF_TOTAL_ELEMENTS));
    }

}
