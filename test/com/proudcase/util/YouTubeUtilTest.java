/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proudcase.util;

import com.proudcase.util.YouTubeUtil;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Michel
 */
public class YouTubeUtilTest {
    
    private static final String TEST_URL = "http://www.youtube.com/watch?v=m0uYa0AePUQ&feature=player_detailpage";
    private static final String TEST_URL2 = "http://www.youtube.com/watch?v=m0uYa0AePUQ";
    
    public YouTubeUtilTest() {
    }

    /**
     * Test of convertYouTubeLink method, of class YouTubeUtil.
     */
    @Test
    public void testConvertYouTubeLink() {
        String newUrl = YouTubeUtil.convertYouTubeLink(TEST_URL);
        assertEquals("URL Check", "http://www.youtube.com/v/m0uYa0AePUQ?version=3", newUrl);
        String newUrl2 = YouTubeUtil.convertYouTubeLink(TEST_URL2);
        assertEquals("URL Check 2", "http://www.youtube.com/v/m0uYa0AePUQ?version=3", newUrl2);
    }
}
