package org.springframework.site.indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.indexer.mapper.LocalStaticPagesSearchEntryMapper;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ToolsIndexer implements Indexer<String> {

	@Value(value = "${search.indexer.base_url:http://localhost:8080}")
	private String baseUrl;

	private final CrawlerService crawlerService;
	private final CrawledWebDocumentProcessor documentProcessor;

	@Autowired
	public ToolsIndexer(CrawlerService crawlerService, SearchService searchService) {
		this.crawlerService = crawlerService;
		this.documentProcessor = new CrawledWebDocumentProcessor(searchService, new LocalStaticPagesSearchEntryMapper());
	}

	@Override
	public Iterable<String> indexableItems() {
		return buildItems("/tools", "/tools/eclipse", "/tools/sts", "/tools/ggts", "/tools/sts/all", "/tools/ggts/all");
	}

	private Iterable<String> buildItems(String ... paths) {
		ArrayList<String> items = new ArrayList<>();
		for (String path : paths) {
			items.add(baseUrl + path);
		}
		return items;
	}

	@Override
	public void indexItem(String path) {
		crawlerService.crawl(path, 0, documentProcessor);
	}

	@Override
	public String counterName() {
		return "tools";
	}

	@Override
	public String getId(String path) {
		return path;
	}
}
