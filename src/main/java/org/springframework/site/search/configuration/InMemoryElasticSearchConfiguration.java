package org.springframework.site.search.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.site.search.SearchService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class InMemoryElasticSearchConfiguration {

	@Autowired
	private SearchService searchService;

	@Autowired
	private Client client;

	@Bean
	public Client elasticSearchClient() throws Exception {
		NodeBuilder nodeBuilder = nodeBuilder().local(false);
		nodeBuilder.getSettings().put("network.host", "127.0.0.1");
		Client client = nodeBuilder.node().client();
		return client;
	}

	@PostConstruct
	public void configureSearchService() {
		searchService.setUseRefresh(true);
	}

	@PreDestroy
	public void closeClient() throws Exception {
		client.close();
	}
}
