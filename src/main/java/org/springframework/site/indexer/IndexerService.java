package org.springframework.site.indexer;

import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Service;

@Service
public class IndexerService {

	private final ExecutorService executorService;
	private final CounterService countersService;

	private static final Log logger = LogFactory.getLog(IndexerService.class);

	@Autowired
	public IndexerService(ExecutorService executorService, CounterService countersService) {
		this.executorService = executorService;
		this.countersService = countersService;
	}

	public <T> void index(final Indexer<T> indexer) {
		logger.info("Indexing " + indexer.counterName());
		for (final T indexable : indexer.indexableItems()) {
			this.executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						indexer.indexItem(indexable);
						IndexerService.this.countersService.increment("search.indexes."
								+ indexer.counterName() + ".processed");
					} catch (Exception e) {
						logger.warn("Unable to load project: " + indexer.getId(indexable)
								+ "(" + e.getClass().getName() + ", " + e.getMessage()
								+ ")");
						IndexerService.this.countersService.increment("search.indexes."
								+ indexer.counterName() + ".errors.count");
					}
				}
			});
		}
		this.countersService.increment("search.indexes." + indexer.counterName()
				+ ".refresh.count");
	}
}