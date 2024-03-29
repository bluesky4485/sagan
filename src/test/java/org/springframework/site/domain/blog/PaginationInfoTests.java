package org.springframework.site.domain.blog;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.web.PaginationInfo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PaginationInfoTests {

	List<String> content = new ArrayList<String>();

	@Test
	public void givenOnePage_controlsAreNotVisible() {
		PageRequest pageRequest = new PageRequest(0, 10);
		int itemCount = 8;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<String>(
				this.content, pageRequest, itemCount));

		assertThat(paginationInfo.isVisible(), is(false));
		assertThat(paginationInfo.isPreviousVisible(), is(false));
		assertThat(paginationInfo.isNextVisible(), is(false));
	}

	@Test
	public void givenOnFirstPageOfThree_nextIsVisible() {
		PageRequest pageRequest = new PageRequest(0, 10);
		int itemCount = 23;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<String>(
				this.content, pageRequest, itemCount));

		assertThat(paginationInfo.isVisible(), is(true));
		assertThat(paginationInfo.isPreviousVisible(), is(false));
		assertThat(paginationInfo.isNextVisible(), is(true));
	}

	@Test
	public void givenOnSecondPageOfThree_nextAndPreviousAreVisible() {
		PageRequest pageRequest = new PageRequest(1, 10);
		int itemCount = 23;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<String>(
				this.content, pageRequest, itemCount));

		assertThat(paginationInfo.isVisible(), is(true));
		assertThat(paginationInfo.isPreviousVisible(), is(true));
		assertThat(paginationInfo.isNextVisible(), is(true));
	}

	@Test
	public void givenOnThirdPageOfThree_previousIsVisible() {
		PageRequest pageRequest = new PageRequest(2, 10);
		int itemCount = 23;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<String>(
				this.content, pageRequest, itemCount));

		assertThat(paginationInfo.isVisible(), is(true));
		assertThat(paginationInfo.isPreviousVisible(), is(true));
		assertThat(paginationInfo.isNextVisible(), is(false));
	}

	@Test
	public void givenOnPageTwo_nextPageIsThree() {
		PageRequest pageRequest = new PageRequest(1, 10);
		int itemCount = 23;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<String>(
				this.content, pageRequest, itemCount));

		assertThat(paginationInfo.getNextPageNumber(), is(equalTo(3L)));
	}

	@Test
	public void givenOnPageTwo_previousPageIsOne() {
		PageRequest pageRequest = new PageRequest(1, 10);
		int itemCount = 23;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<String>(
				this.content, pageRequest, itemCount));

		assertThat(paginationInfo.getPreviousPageNumber(), is(equalTo(1L)));
	}

	@Test
	public void pagesAreNotZeroIndexed() {
		int itemCount = 101;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<String>(
				this.content, new PageRequest(0, 10), itemCount));
		assertThat(paginationInfo.getCurrentPage(), is(Matchers.equalTo(1L)));
		assertThat(paginationInfo.getTotalPages(), is(Matchers.equalTo(11L)));
	}
}
