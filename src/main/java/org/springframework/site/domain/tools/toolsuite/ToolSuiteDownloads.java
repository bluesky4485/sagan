package org.springframework.site.domain.tools.toolsuite;

import java.util.*;

public class ToolSuiteDownloads {

	private Map<String, ToolSuitePlatform> platforms;
	private List<UpdateSiteArchive> archives;
	private String releaseName;

	public ToolSuiteDownloads(String releaseName, Map<String, ToolSuitePlatform> platforms, List<UpdateSiteArchive> archives) {
		this.releaseName = releaseName;
		this.platforms = platforms;
		this.archives = archives;
	}

	public Map<String, ToolSuitePlatform> getPlatforms() {
		return platforms;
	}

	public List<UpdateSiteArchive> getArchives() {
		return archives;
	}

	public Set<DownloadLink> getPreferredDownloadLinks() {
		Set<DownloadLink> links = new HashSet<>();
		addLinks(links, "windows", "exe");
		addLinks(links, "windows", "exe");
		addLinks(links, "mac", "dmg");
		addLinks(links, "mac", "dmg");
		addLinks(links, "linux", "tar.gz");
		addLinks(links, "linux", "tar.gz");
		return links;
	}

	private void addLinks(Set<DownloadLink> links, String platformString, String fileType) {
		ToolSuitePlatform platform = platforms.get(platformString);
		if (platform == null) return;

		EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
		for (Architecture architecture : eclipseVersion.getArchitectures()) {
			for (DownloadLink link : architecture.getDownloadLinks()) {
				if (link.getFileType().equals(fileType)) {
					links.add(link);
				}
			}
		}
	}

	public String getReleaseName() {
		return releaseName;
	}
}
