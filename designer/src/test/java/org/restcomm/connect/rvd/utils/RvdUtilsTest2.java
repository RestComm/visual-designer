package org.restcomm.connect.rvd.utils;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RvdUtilsTest2 {

    @Test
    public void testPathTrailingSlashTampering() {
        // test removeTrailingSlashIfPresent()
        Assert.assertEquals("/bin/rvd", RvdUtils.removeTrailingSlashIfPresent("/bin/rvd/"));
        Assert.assertEquals("/bin/rvd/a", RvdUtils.removeTrailingSlashIfPresent("/bin/rvd/a"));
        Assert.assertEquals("", RvdUtils.removeTrailingSlashIfPresent(""));
        Assert.assertEquals(null, RvdUtils.removeTrailingSlashIfPresent(null));
        Assert.assertEquals("/", RvdUtils.removeTrailingSlashIfPresent("/"));
        Assert.assertEquals("http://test.com/asdf", RvdUtils.removeTrailingSlashIfPresent("http://test.com/asdf/"));
    }
}
