package org.restcomm.connect.rvd.utils;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ValidationUtilsTest {

    @Test
    public void testTemplateIdValidation() {
        Assert.assertTrue(ValidationUtils.validateTemplateId("TL1234"));
        Assert.assertTrue(ValidationUtils.validateTemplateId("TL81cf45088cba4abcac1261385916d582"));
        Assert.assertFalse(ValidationUtils.validateTemplateId("TL1234/"));
        Assert.assertFalse(ValidationUtils.validateTemplateId(null));
    }
}
