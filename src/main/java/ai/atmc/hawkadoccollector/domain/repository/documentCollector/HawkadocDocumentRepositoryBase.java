package ai.atmc.hawkadoccollector.domain.repository.documentCollector;

import ai.atmc.hawkadoccollector.domain.dao.documentCollector.HawkadocDocumentDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HawkadocDocumentRepositoryBase extends PagingAndSortingRepository<HawkadocDocumentDao,Integer>, JpaRepository<HawkadocDocumentDao,Integer> {
}
