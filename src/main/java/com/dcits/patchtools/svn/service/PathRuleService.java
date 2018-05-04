package com.dcits.patchtools.svn.service;

import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.util.PatchEnum;

/**
 * @author Kevin
 * @date 2018-05-04 09:51.
 * @desc
 * @email chenkunh@dcits.com
 */
public interface PathRuleService {
    PatchEnum excute(FileBlame fileBlame);

    boolean pathExcludeFilter(String path);

    boolean pathListOnlyFilter(String path);

    void pathConvert(FileBlame fileBlame);
}
