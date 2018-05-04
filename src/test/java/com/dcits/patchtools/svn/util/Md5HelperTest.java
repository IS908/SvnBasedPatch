package com.dcits.patchtools.svn.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * @author Kevin
 * @date 2018-05-03 11:13.
 * @desc
 * @email chenkunh@dcits.com
 */
public class Md5HelperTest {

    @Test
    public void encrypt32() {
        String encry = Md5Helper.encrypt32("abc");

        Assert.assertTrue(Objects.equals("900150983cd24fb0d6963f7d28e17f72", encry));
    }

    @Test
    public void encrypt16() {
        String encry = Md5Helper.encrypt16("abc");
        Assert.assertTrue(Objects.equals("3cd24fb0d6963f7d", encry));
    }
}