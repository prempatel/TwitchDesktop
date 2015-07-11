package com.TwitchTest;

import static org.junit.Assert.*;

import org.junit.Test;

public class TwitchTests {

	@Test
	public void testIfCorrectString() {
		RandomTest test = new RandomTest();
		String url = test.getStreamLinksFromPlaylist("best", "tsm_santorin");
		assertEquals(url.contains("chunked"), url);
	}
}
