/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.site.web.configuration;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.sql.DataSource;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator;
import org.postgresql.ds.PGSimpleDataSource;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.site.domain.projects.ProjectMetadataYamlParser;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.web.SiteUrl;
import org.springframework.site.web.blog.feed.BlogPostAtomViewer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "org.springframework.site.web",
		"org.springframework.site.domain", "org.springframework.site.search" })
public class ApplicationConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfiguration.class, args);
	}

	@Configuration
	@Profile({ "development", "staging", "production" })
	protected static class CloudFoundryDataSourceConfiguration {
		@Bean
		public DataSource dataSource() {
			CloudEnvironment cloudEnvironment = new CloudEnvironment();
			RdbmsServiceInfo serviceInfo = cloudEnvironment.getServiceInfo("sagan-db",
					RdbmsServiceInfo.class);
			RdbmsServiceCreator serviceCreator = new RdbmsServiceCreator();
			return serviceCreator.createService(serviceInfo);
		}
	}

	@Configuration
	@Profile({ "local_postgres" })
	protected static class PostgresConfiguration {
		@Bean
		public DataSource dataSource() {
			PGSimpleDataSource dataSource = new PGSimpleDataSource();
			dataSource.setPortNumber(5432);
			dataSource.setDatabaseName("blog_import");
			dataSource.setServerName("localhost");
			return dataSource;
		}
	}

	@Bean
	public BlogPostAtomViewer blogPostAtomViewer(SiteUrl siteUrl, DateService dateService) {
		return new BlogPostAtomViewer(siteUrl, dateService);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Serializer simpleXmlSerializer() {
		return new Persister();
	}

	@Bean
	public ProjectMetadataService projectMetadataService() throws IOException {
		InputStream yaml = new ClassPathResource("/project-metadata.yml", getClass())
				.getInputStream();
		return new ProjectMetadataService(new ProjectMetadataYamlParser().parse(yaml));
	}

	// http://urlrewritefilter.googlecode.com/svn/trunk/src/doc/manual/4.0/index.html#filterparams
	@Bean
	public FilterRegistrationBean rewriteFilterConfig() {
		FilterRegistrationBean reg = new FilterRegistrationBean();
		reg.setFilter(rewriteFilter());
		reg.addInitParameter("confReloadCheckInterval", "0");
		reg.addInitParameter("confPath", "urlrewrite.xml");
		return reg;
	}

	@Bean
	public Filter rewriteFilter() {
		return new UrlRewriteFilter();
	}

	@Bean
	public DispatcherServlet dispatcherServlet() {
		return new DispatcherServlet();
	}

}
