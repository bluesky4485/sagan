package org.springframework.site.indexer.crawler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soulgalore.crawler.core.CrawlerConfiguration;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.PageURL;
import com.soulgalore.crawler.core.PageURLParser;
import com.soulgalore.crawler.core.impl.AhrefPageURLParser;
import com.soulgalore.crawler.core.impl.DefaultCrawler;
import com.soulgalore.crawler.core.impl.HTTPClientResponseFetcher;

@Component
public class CrawlerService {

	private final ExecutorService executorService;

	@Autowired
	public CrawlerService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	private HttpClient httpClient() {
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(1);
		HttpClient client = new DefaultHttpClient(connectionManager);

		client.getParams().setParameter("http.socket.timeout", 3000);
		client.getParams().setParameter("http.connection.timeout", 30000);

		return client;
	}

	public void crawl(String url, int linkDepth, DocumentProcessor processor) {
		CrawlerConfiguration apiConfig = new CrawlerConfiguration.Builder()
				.setStartUrl(url).setMaxLevels(linkDepth).build();
		DefaultCrawler crawler = new DefaultCrawler(new ResponseFetcher(processor),
				this.executorService, new CompositeURLParser(new FramePageURLParser(),
						new AhrefPageURLParser()));
		crawler.getUrls(apiConfig);
		crawler.shutdown();
	}

	private class ResponseFetcher extends HTTPClientResponseFetcher {

		private final DocumentProcessor processor;

		public ResponseFetcher(DocumentProcessor processor) {
			super(httpClient());
			this.processor = processor;
		}

		@Override
		public HTMLPageResponse get(PageURL url, boolean fetchBody,
				Map<String, String> requestHeaders) {
			HTMLPageResponse response = super.get(url, fetchBody, requestHeaders);
			if (response.getResponseCode() == 200
					&& response.getResponseType().startsWith("text")) {
				this.processor.process(response.getBody());
			}
			return response;
		}

	}

	private static class CompositeURLParser implements PageURLParser {

		private PageURLParser[] parsers;

		private CompositeURLParser(PageURLParser... parsers) {
			this.parsers = parsers;
		}

		@Override
		public Set<PageURL> get(HTMLPageResponse theResponse) {
			Set<PageURL> urls = new HashSet<PageURL>();
			for (PageURLParser parser : this.parsers) {
				urls.addAll(parser.get(theResponse));
			}
			return urls;
		}
	}

	private static class FramePageURLParser implements PageURLParser {
		private static final String FRAME = "frame[src]";
		private static final String ABS_SRC = "abs:src";

		public Set<PageURL> get(HTMLPageResponse theResponse) {
			String url = theResponse.getUrl();
			Set<PageURL> urls = new HashSet<PageURL>();
			// only populate if we have a valid response, else return empty set
			if (theResponse.getResponseCode() == HttpStatus.SC_OK) {
				urls = fetch(FRAME, ABS_SRC, theResponse.getBody(), url);
			}
			return urls;
		}

		private Set<PageURL> fetch(String query, String attributeKey, Document doc,
				String url) {
			Set<PageURL> urls = new HashSet<PageURL>();
			Elements elements = doc.select(query);
			for (Element src : elements) {
				if (src.attr(attributeKey).isEmpty())
					continue;
				urls.add(new PageURL(src.attr(attributeKey), url));

			}
			return urls;
		}

	}

}
