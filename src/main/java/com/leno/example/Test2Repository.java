package com.leno.example;

import com.leno.example.domain.dao.documentCollector.DocumentDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;


@Component
public interface Test2Repository extends PagingAndSortingRepository<DocumentDao,Integer>, JpaRepository<DocumentDao,Integer> {
}
