package com.example.appointments.service;

import com.example.appointments.dto.ProviderSearchResult;
import com.example.appointments.dto.SearchPage;
import com.example.appointments.exception.ResourceNotFoundException;
import com.example.appointments.model.Provider;
import com.example.appointments.repository.ProviderRepository;
import com.example.appointments.search.ProviderChangedEvent;
import com.example.appointments.search.ProviderDocument;
import com.example.appointments.search.ProviderSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class ProviderSearchService {
    private static final Logger log = LoggerFactory.getLogger(ProviderSearchService.class);

    private final ProviderSearchRepository searchRepository;
    private final ProviderRepository providerRepository;

    public ProviderSearchService(ProviderSearchRepository searchRepository, ProviderRepository providerRepository) {
        this.searchRepository = searchRepository;
        this.providerRepository = providerRepository;
    }

    public SearchPage<ProviderSearchResult> search(String query, int page, int size) {
        Page<ProviderSearchResult> results = searchRepository.searchAcceptingProviders(query,
                        PageRequest.of(page, size, Sort.by("_score").descending()))
                .map(ProviderSearchResult::from);
        return SearchPage.from(results);
    }

    public long rebuildIndex() {
        searchRepository.deleteAll();
        long count = 0;
        for (Provider provider : providerRepository.findAll()) {
            searchRepository.save(ProviderDocument.from(provider));
            count++;
        }
        return count;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void indexAfterCommit(ProviderChangedEvent event) {
        try {
            Provider provider = providerRepository.findById(event.providerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider " + event.providerId() + " was not found"));
            searchRepository.save(ProviderDocument.from(provider));
        } catch (RuntimeException exception) {
            log.error("Unable to index provider {}. Run the reindex endpoint after Elasticsearch recovers.",
                    event.providerId(), exception);
        }
    }
}
