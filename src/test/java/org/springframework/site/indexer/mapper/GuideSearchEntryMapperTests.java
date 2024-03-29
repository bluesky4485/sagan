package org.springframework.site.indexer.mapper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.guides.Guide;
import org.springframework.site.search.SearchEntry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class GuideSearchEntryMapperTests {

	private Guide guide = new Guide("guide-repo",
			"guide-id",
			"Guide Title","Guide Subtitle",
			"Some Guide Content",
			"Some Sidebar Content");

	private GuideSearchEntryMapper guideMapper = new GuideSearchEntryMapper();
	private SearchEntry searchEntry;

	@Before
	public void setUp() throws Exception {
		searchEntry = guideMapper.map(guide);
	}

	@Test
	public void mapsRawContent() throws Exception {
		assertThat(searchEntry.getRawContent(), equalTo("Some Guide Content"));
	}

	@Test
	public void mapsTitle() throws Exception {
		assertThat(searchEntry.getTitle(), equalTo("Guide Title"));
	}

	@Test
	public void mapsSubTitle() throws Exception {
		assertThat(searchEntry.getSubTitle(), equalTo("Getting Started Guide"));
	}

}
