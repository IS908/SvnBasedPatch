package com.dcits.patchtools.svn.service;

import java.util.Set;

/**
 * @author Kevin
 * @date 2018-05-04 09:10.
 * @desc 根据规则生成增量描述文件、送测清单；根据增量描述文件进行增量包的抽取
 * @email chenkunh@dcits.com
 */
public interface PatchService {

    /**
     * 根据增量记录生成增量描述文件
     * @param versionFrom
     * @param versionTo
     * @param baseDir
     * @return
     */
    boolean genPatchListAndReport(String baseDir, long versionFrom, long versionTo);

    /**
     * 进行增量抽取生成增量部署包
     * @param versionFrom
     * @param versionTo
     * @param baseDir
     * @return
     */
    Set<String> executePatch(String baseDir, long versionFrom, long versionTo);
}
