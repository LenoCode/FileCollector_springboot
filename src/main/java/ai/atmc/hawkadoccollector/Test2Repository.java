package ai.atmc.hawkadoccollector;

import ai.atmc.hawkadoccollector.domain.dao.documentCollector.HawkadocDocumentDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;


@Component
public interface Test2Repository extends PagingAndSortingRepository<HawkadocDocumentDao,Integer>, JpaRepository<HawkadocDocumentDao,Integer> {
}
