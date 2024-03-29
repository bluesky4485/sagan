package org.springframework.site.web.search;

import org.springframework.site.search.SearchEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchEntryBuilder {

	private String title = "A random title";
	private String rawContent = "A random post content";
	private String summary = "A random post summary";
	private Date publishAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
	private String path = "/some/path";
	private List<String> facetPaths = new ArrayList<>();
	private boolean current = true;
	private String type;
	private String version;
	private String projectId;

	private SearchEntryBuilder() {
	}

	public static SearchEntryBuilder entry() {
		return new SearchEntryBuilder();
	}

	public SearchEntryBuilder title(String title) {
		this.title = title;
		return this;
	}

	public SearchEntryBuilder rawContent(String rawContent) {
		this.rawContent = rawContent;
		return this;
	}

	public SearchEntryBuilder summary(String summary) {
		this.summary = summary;
		return this;
	}

	public SearchEntryBuilder publishAt(Date date) {
		this.publishAt = date;
		return this;
	}

	public SearchEntryBuilder publishAt(String dateString) throws ParseException {
		this.publishAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
		return this;
	}

	public SearchEntryBuilder path(String path) {
		this.path = path;
		return this;
	}

	public SearchEntryBuilder facetPath(String facetPath) {
		this.facetPaths.add(facetPath);
		return this;
	}

	public SearchEntryBuilder notCurrent() {
		this.current = false;
		return this;
	}

	public SearchEntry build() {
		SearchEntry searchEntry = new SearchEntry();

		searchEntry.setTitle(title);
		searchEntry.setRawContent(rawContent);
		searchEntry.setSummary(summary);
		searchEntry.setPublishAt(publishAt);
		searchEntry.setPath(path);
		searchEntry.setCurrent(current);
		searchEntry.setFacetPaths(facetPaths);
		searchEntry.setVersion(version);
		searchEntry.setProjectId(projectId);

		if (type != null) {
			searchEntry.setType(type);
		}
		return searchEntry;
	}

	public SearchEntryBuilder type(String type) {
		this.type = type;
		return this;
	}

	public SearchEntryBuilder version(String version) {
		this.version = version;
		return this;
	}

	public SearchEntryBuilder projectId(String projectId) {
		this.projectId = projectId;
		return this;
	}
}
