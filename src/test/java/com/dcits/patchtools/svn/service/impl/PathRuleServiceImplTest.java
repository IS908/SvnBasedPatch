package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.TestBase;
import com.dcits.patchtools.svn.service.PathRuleService;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kevin
 * @date 2018-05-04 19:13.
 * @desc
 * @email chenkunh@dcits.com
 */
public class PathRuleServiceImplTest extends TestBase {

    @Test
    public void pathExcludeFilter() {
        PathRuleService pathRuleService = context.getBean(PathRuleServiceImpl.class);
        boolean res = pathRuleService.pathExcludeFilter("abc.xls");
        System.out.println(res);
    }

    @Test
    public void pathListOnlyFilter() {
    }

    @Test
    public void pathConvert() {
    }
}