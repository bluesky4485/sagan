package org.springframework.site.indexer.mapper;

import org.jsoup.Jsoup;
import org.springframework.site.domain.understanding.UnderstandingGuide;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class UnderstandingGuideMapper implements SearchEntryMapper<UnderstandingGuide> {
	@Override
	public SearchEntry map(UnderstandingGuide guide) {
		SearchEntry entry = new SearchEntry();
		String rawContent = Jsoup.parse(guide.getContent()).text();
		entry.setRawContent(rawContent);
		entry.setTitle(guide.getSubject());
		entry.setPath("understanding/" + guide.getSubject());
		entry.setSummary(rawContent.substring(0, Math.min(500, rawContent.length())));
		entry.addFacetPaths("Guides", "Guides/Understanding");
		entry.setPublishAt(new Date(0L));
		entry.setSubTitle("Understanding Doc");
		return entry;
	}
}
