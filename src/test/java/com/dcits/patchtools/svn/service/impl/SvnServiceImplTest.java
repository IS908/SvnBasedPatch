package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.TestBase;
import com.dcits.patchtools.svn.model.CommitModel;
import com.dcits.patchtools.svn.service.SvnService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin
 * @date 2018-05-03 09:34.
 * @desc
 * @email chenkunh@dcits.com
 */
public class SvnServiceImplTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(SvnServiceImplTest.class);

    @Test
    public void getAllCommitHistory() {
        SvnService svnService = context.getBean(SvnServiceImpl.class);
        final Map<String, List<CommitModel>> historyMap = svnService.getAllCommitHistory();
        Iterator<Map.Entry<String, List<CommitModel>>> iterator = historyMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<CommitModel>> entry = iterator.next();
            String key = entry.getKey();
            List<CommitModel> value = entry.getValue();
            logger.info(key + "======" + value.toString());
        }
    }
}