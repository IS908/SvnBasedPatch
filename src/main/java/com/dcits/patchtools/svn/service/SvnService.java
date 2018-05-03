package com.dcits.patchtools.svn.service;

import com.dcits.patchtools.svn.model.CommitModel;
import com.dcits.patchtools.svn.model.FileBlame;

import java.util.List;
import java.util.Map;

/**
 * @author Kevin
 * @date 2018-04-29 16:14.
 * @desc
 * @email chenkunh@dcits.com
 */
public interface SvnService {

    /**
     * 获取SVN提交记录
     * @return
     */
    Map<String, List<CommitModel>> getAllCommitHistory();
}
