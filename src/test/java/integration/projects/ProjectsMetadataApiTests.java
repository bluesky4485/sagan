package integration.projects;

import integration.IntegrationTestBase;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.boot.config.JacksonJsonParser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectsMetadataApiTests extends IntegrationTestBase {

	@Test
	public void projectMetadata_respondsWithJavascript() throws Exception {
		this.mockMvc
				.perform(
						MockMvcRequestBuilders
								.get("/project_metadata/spring-framework?callback=a_function_name"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/javascript"));
	}

	@Test
	public void projectMetadata_respondsWithCallback_andData() throws Exception {
		List<Object> releases = getAndCheckProjectReleases("spring-framework",
				"Spring Framework");

		checkRelease(releases);
		checkMilestone(releases);
	}

	@Test
	public void projectMetadata_WithSnapshot() throws Exception {
		List<Object> releases = getAndCheckProjectReleases("spring-security-kerberos",
				"Spring Security Kerberos");

		checkSnapshot(releases);
	}

	public List<Object> getAndCheckProjectReleases(String projectId,
			String expectedProjectName) throws Exception {
		MvcResult result = this.mockMvc.perform(
				MockMvcRequestBuilders.get("/project_metadata/" + projectId
						+ "?callback=a_function_name")).andReturn();

		String content = result.getResponse().getContentAsString();

		String functionNameRegex = "^([^(]*)\\((.*)\\);$";
		Matcher matcher = Pattern.compile(functionNameRegex).matcher(content);
		if (matcher.find()) {
			assertThat(matcher.group(1), equalTo("a_function_name"));

			Map<String, Object> projectMetadata = new JacksonJsonParser()
					.parseMap(matcher.group(2));

			assertThat((String) projectMetadata.get("name"), equalTo(expectedProjectName));
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) projectMetadata.get("projectReleases");
			return list;
		} else {
			fail(String.format("no match found: %s", content));
			return null;
		}
	}

	private void checkRelease(List<Object> releases) {
		@SuppressWarnings("unchecked")
		Map<String, Object> release = (Map<String, Object>) releases.get(1);
		assertThat((String) release.get("version"), equalTo("3.2.4.RELEASE"));
		assertThat(
				(String) release.get("refDocUrl"),
				equalTo("http://docs.springframework.io/spring/docs/3.2.4.RELEASE/spring-framework-reference/html/"));
		assertThat(
				(String) release.get("apiDocUrl"),
				equalTo("http://docs.springframework.io/spring/docs/3.2.4.RELEASE/javadoc-api/"));
		assertThat((Boolean) release.get("preRelease"), equalTo(false));
		assertThat((Boolean) release.get("current"), equalTo(true));
		assertThat((Boolean) release.get("supported"), equalTo(false));

		Map<String, Object> repository = getRepository(release);
		assertThat(repository, nullValue());
	}

	private void checkMilestone(List<Object> releases) {
		@SuppressWarnings("unchecked")
		Map<String, Object> release = (Map<String, Object>) releases.get(0);
		assertThat((String) release.get("version"), equalTo("4.0.0.M2"));
		assertThat(
				(String) release.get("refDocUrl"),
				equalTo("http://docs.springframework.io/spring/docs/4.0.0.M2/spring-framework-reference/html/"));
		assertThat(
				(String) release.get("apiDocUrl"),
				equalTo("http://docs.springframework.io/spring/docs/4.0.0.M2/javadoc-api/"));
		assertThat((Boolean) release.get("preRelease"), equalTo(true));
		assertThat((Boolean) release.get("current"), equalTo(false));
		assertThat((Boolean) release.get("supported"), equalTo(false));

		Map<String, Object> repository = getRepository(release);

		assertThat((String) repository.get("id"), equalTo("spring-milestones"));
		assertThat((String) repository.get("name"), equalTo("Spring Milestones"));
		assertThat((String) repository.get("url"),
				equalTo("http://repo.springsource.org/milestone"));
		assertThat((Boolean) repository.get("snapshotsEnabled"), equalTo(false));

	}

	private void checkSnapshot(List<Object> releases) {
		@SuppressWarnings("unchecked")
		Map<String, Object> release = (Map<String, Object>) releases.get(0);
		assertThat((String) release.get("version"), equalTo("1.0.0.CI-SNAPSHOT"));
		assertThat(
				(String) release.get("refDocUrl"),
				equalTo("http://docs.springframework.io/spring-security-kerberos/docs/1.0.0.CI-SNAPSHOT/reference/html"));
		assertThat(
				(String) release.get("apiDocUrl"),
				equalTo("http://docs.springframework.io/spring-security-kerberos/docs/1.0.0.CI-SNAPSHOT/api/"));
		assertThat((Boolean) release.get("preRelease"), equalTo(false));
		assertThat((Boolean) release.get("current"), equalTo(true));
		assertThat((Boolean) release.get("supported"), equalTo(false));

		Map<String, Object> repository = getRepository(release);

		assertThat((String) repository.get("id"), equalTo("spring-snapshots"));
		assertThat((String) repository.get("name"), equalTo("Spring Snapshots"));
		assertThat((String) repository.get("url"),
				equalTo("http://repo.springsource.org/snapshot"));
		assertThat((Boolean) repository.get("snapshotsEnabled"), equalTo(true));
	}

	private Map<String, Object> getRepository(Map<String, Object> release) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) release.get("repository");
		return map;
	}
}
