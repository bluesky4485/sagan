package org.springframework.site.domain.tools.toolsuite.parser;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.tools.toolsuite.Architecture;
import org.springframework.site.domain.tools.toolsuite.ToolSuiteDownloads;
import org.springframework.site.domain.tools.toolsuite.xml.Download;
import org.springframework.site.domain.tools.toolsuite.xml.Release;
import org.springframework.site.domain.tools.toolsuite.xml.ToolSuiteXml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolXmlConverter_TwoDifferentToolSuitesTests {
	private ToolSuiteDownloads toolSuite;
	private ToolXmlConverter toolXmlConverter;


	@Before
	public void setUp() throws Exception {
		ToolSuiteXml toolSuiteXml = new ToolSuiteXml();
		List<Release> releases = new ArrayList<Release>();

		Download download = new Download();
		download.setDescription("Mac OS X (Cocoa)");
		download.setOs("mac");
		download.setFile("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg");
		download.setBucket("http://dist.springsource.com/");
		download.setEclipseVersion("4.3");
		download.setSize("373MB");
		download.setVersion("3.3.0.RELEASE");

		Release stsRelease = new Release();
		stsRelease.setDownloads(Collections.singletonList(download));
		stsRelease.setName("Spring Tool Suite 3.3.0.RELEASE - based on Eclipse Kepler 4.3");
		releases.add(stsRelease);

		download = new Download();
		download.setDescription("Mac OS X (Cocoa)");
		download.setOs("mac");
		download.setFile("release/STS/3.3.0/dist/e3.8/groovy-grails-tool-suite-3.3.0.RELEASE-e3.8.2-macosx-cocoa-installer.dmg");
		download.setBucket("http://dist.springsource.com/");
		download.setEclipseVersion("4.3");
		download.setSize("463MB");
		download.setVersion("3.3.0.RELEASE");

		Release ggtsRelease = new Release();
		ggtsRelease.setDownloads(Collections.singletonList(download));
		ggtsRelease.setName("Groovy/Grails Tool Suite 3.3.0.RELEASE - based on Eclipse Kepler 4.3");
		releases.add(ggtsRelease);

		toolSuiteXml.setReleases(releases);

		toolXmlConverter = new ToolXmlConverter();
		toolSuite = toolXmlConverter.convert(toolSuiteXml, "Spring Tool Suite");
	}

	@Test
	public void addsADownloadLinkForStsOnly() throws Exception {
		Architecture macArchitecture = toolSuite.getPlatforms().get("mac").getEclipseVersions().get(0).getArchitectures().get(0);
		assertThat(macArchitecture.getDownloadLinks().size(), equalTo(1));
		assertThat(macArchitecture.getDownloadLinks().get(0).getUrl(), equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
	}
}
