package com.td.db;

import com.td.models.TechnicalDebtItem;
import com.td.models.TechnicalDebtItem.CompositeKey;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TDReferenceRepository extends MongoRepository<TechnicalDebtItem, String> {

    @Cacheable("tditems")
    TechnicalDebtItem findTechnicalDebtItemById(CompositeKey id);

}