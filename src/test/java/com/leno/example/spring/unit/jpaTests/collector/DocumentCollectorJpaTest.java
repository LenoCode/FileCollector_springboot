/*
package ai.atmc.hawkadoccollector.spring.unit.jpaTests.collector;

import ai.atmc.hawkadoccollector.Test2Repository;
import ai.atmc.hawkadoccollector.TestRepository;
import ai.atmc.hawkadoccollector.domain.dao.documentCollector.DocumentCollectorDao;
import ai.atmc.hawkadoccollector.domain.dao.documentCollector.HawkadocDocumentDao;
import ai.atmc.hawkadoccollector.domain.repository.documentCollector.DocumentCollectorRepositoryBase;
import ai.atmc.hawkadoccollector.domain.repository.documentCollector.HawkadocDocumentRepositoryBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@DataJpaTest
@ExtendWith(SpringExtension.class)
public class DocumentCollectorJpaTest{
    private int NUMBER_OF_TOTAL_ELEMENTS;


    @Autowired
    DocumentCollectorRepositoryBase documentCollectorRepositoryBase;

    @Autowired
    HawkadocDocumentRepositoryBase hawkadocDocumentRepositoryBase;


    */
/**
     * Fill data before testing
     *//*

    @BeforeEach
    void setup(){
        HashMap<String, Object> sftpCollector = new HashMap<>(Map.of("name","sftp_collector"));
        HashMap<String, Object> uploaderCollector = new HashMap<>(Map.of("name","uploader_collector"));

        List<DocumentCollectorDao> list = List.of(new DocumentCollectorDao[]{

                new DocumentCollectorDao(sftpCollector,"sftp_collector"),
                new DocumentCollectorDao(uploaderCollector,"uploader_collector")
        });

        documentCollectorRepositoryBase.saveAll(list);

        NUMBER_OF_TOTAL_ELEMENTS = list.size();

        Page<DocumentCollectorDao> daos = documentCollectorRepositoryBase.findAll(PageRequest.of(0,NUMBER_OF_TOTAL_ELEMENTS));

        Object dao = daos.get().toArray()[0];


        List<HawkadocDocumentDao> listDocuments = List.of(new HawkadocDocumentDao[]{
                new HawkadocDocumentDao((DocumentCollectorDao) dao),
                new HawkadocDocumentDao((DocumentCollectorDao) dao)
        });

        hawkadocDocumentRepositoryBase.saveAllAndFlush(listDocuments);

    }


    */
/**
     * Test basic paging and sorting
     *//*

    @ParameterizedTest
    @DisplayName(value = "Test that on first page with get total number of elements")
    @ValueSource(ints = {0,2})
    public void testBasicPagingAndSorting(int pageNumber){
        Page<DocumentCollectorDao> result =  documentCollectorRepositoryBase.findAll(PageRequest.of(pageNumber,NUMBER_OF_TOTAL_ELEMENTS));

        if(pageNumber == 1){
            assertEquals(0,result.getTotalElements());
        }else{
            assertEquals(NUMBER_OF_TOTAL_ELEMENTS,result.getTotalElements());

        }
    }

    @DisplayName(value = "Test that many to one relationship between DocumentCollectorDao and HawkadocDocumentDao are working")
    @Test
    public void testManyToOneRelationshipBetweenDocumentCollectorDaoAndHawkadocDocumentDao(){
        Page<HawkadocDocumentDao> result =  hawkadocDocumentRepositoryBase.findAll(PageRequest.of(0,NUMBER_OF_TOTAL_ELEMENTS));

        result.stream().forEach(hawkadocDocumentDao -> {
            assertEquals(hawkadocDocumentDao.getDocumentCollector().getCollectorType(),"sftp_collector");
        });


    }
}
*/
