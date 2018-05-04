package com.dcits.patchtools.svn.dao;

import com.dcits.patchtools.svn.model.FileModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Kevin
 * @desc 根据提交时间段获取文件提交记录
 * @email chenkunh@dcits.com
 * @date 2018-04-25 21:32.
 */
public class SvnDao {
    private static final Logger logger = LoggerFactory.getLogger(SvnDao.class);

    private SVNRepository repository = null;

    public SvnDao(String svnUrl) {
        this(svnUrl, null, null);
    }

    public SvnDao(String svnUrl, String user, String pwd) {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        } catch (SVNException e) {
            logger.error(String.valueOf(e.getErrorMessage()), e);
        }
        if (Objects.equals(null, user) && Objects.equals(null, pwd)) {
            return;
        }
        // 身份验证
        ISVNAuthenticationManager authManager =
                SVNWCUtil.createDefaultAuthenticationManager(user,pwd);
        repository.setAuthenticationManager(authManager);
    }

    /**
     * 获得所有的提交记录
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getAllCommitHistory() throws SVNException {
        final List<SVNLogEntry> logEntryList = new ArrayList<>();
        repository.log(new String[]{""}, 0, -1, true, true, new ISVNLogEntryHandler() {

            @Override
            public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                logEntryList.add(logEntry);
            }
        });
        return logEntryList;
    }

}
