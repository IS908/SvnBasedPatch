package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.TestBase;
import com.dcits.patchtools.svn.service.PatchService;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kevin
 * @date 2018-05-04 12:02.
 * @desc
 * @email chenkunh@dcits.com
 */
public class PatchServiceImplTest extends TestBase {

    @Test
    public void genPatchListAndReport() {
        PatchService patchService = context.getBean(PatchServiceImpl.class);
        patchService.genPatchListAndReport(0, -1, null);
    }

    @Test
    public void executePatch() {
    }
}