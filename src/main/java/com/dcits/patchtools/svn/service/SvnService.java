package com.dcits.patchtools.svn.service;

import com.dcits.patchtools.svn.model.FileModel;
import org.tmatesoft.svn.core.SVNException;

import java.io.ByteArrayOutputStream;
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
     *
     * @return
     */
    Map<String, List<FileModel>> getAllCommitHistory(boolean excludeDir);

    /**
     * 从SVN服务端获取指定文件的内容
     *
     * @param svnUrl
     * @return
     */
    ByteArrayOutputStream getFileFromSVN(String svnUrl) throws SVNException;
}
