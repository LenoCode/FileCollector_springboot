package com.leno.example.domain.repository.documentCollector;

import com.leno.example.domain.dao.documentCollector.DocumentDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentRepositoryBase extends PagingAndSortingRepository<DocumentDao,Integer>, JpaRepository<DocumentDao,Integer> {
}
