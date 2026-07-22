package com.example.appointments.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProviderSearchRepository extends ElasticsearchRepository<ProviderDocument, Long> {
    @Query("""
            {"bool":{"must":[{"multi_match":{"query":"?0","fields":["treatedSymptoms^4","specialty^3","name^2","bio"],"fuzziness":"AUTO"}}],"filter":[{"term":{"acceptingNewPatients":true}}]}}
            """)
    Page<ProviderDocument> searchAcceptingProviders(String query, Pageable pageable);
}
