package com.dcits.patchtools.svn.dao;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Kevin
 * @desc 根据提交时间段获取文件提交记录
 * @email chenkunh@dcits.com
 * @date 2018-04-25 21:32.
 */
public class SvnDao {
    private static final Logger logger = LoggerFactory.getLogger(SvnDao.class);

    @Setter
    @Getter
    private String svnUrl;
    @Setter
    @Getter
    private String user;
    @Setter
    @Getter
    private String pwd;

    // todo：获取上一个版本号（读文件方式）

    /**
     * 获得所有的提交记录
     *
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getAllCommitHistory() {
        final long[] currentVersion = {-1L};
        final List<SVNLogEntry> logEntryList = new ArrayList<>();
        SVNRepository repository = openReopsitory();
        try {
            repository.log(new String[]{""}, 0, -1, true, true, new ISVNLogEntryHandler() {

                @Override
                public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                    currentVersion[0] = Math.max(currentVersion[0], logEntry.getRevision());
                    logEntryList.add(logEntry);
                }
            });
            // todo: 将最新的版本号写到md5(svnUrl)命名的文件中
        } catch (SVNException e) {
            logger.info(e.getErrorMessage().getFullMessage());
        } finally {
            repository.closeSession();
        }
        return logEntryList;
    }

    /**
     * 从SVN服务器读取指定文件到本机内存
     *
     * @param svnFile
     * @return
     * @throws SVNException
     */
    public ByteArrayOutputStream getFileFromSVN(String svnFile) throws SVNException {
        SVNRepository repository = openReopsitory();
//        SVNProperties svnProperties = new SVNProperties();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        long lastVersion = repository.getLatestRevision();
        repository.getFile(svnFile, lastVersion, null, os);
        return os;
    }

    /**
     * 获取SVN服务器连接句柄
     *
     * @return
     */
    private SVNRepository openReopsitory() {
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        } catch (SVNException e) {
            logger.error(String.valueOf(e.getErrorMessage()), e);
        }
        if (Objects.equals(null, user) && Objects.equals(null, pwd)) {
            return repository;
        }
        // 身份验证
        ISVNAuthenticationManager authManager =
                SVNWCUtil.createDefaultAuthenticationManager(user, pwd);
        repository.setAuthenticationManager(authManager);
        return repository;
    }

    public static void main(String[] args) throws SVNException {
        SvnDao svnDao = new SvnDao();
        svnDao.setSvnUrl("file:///D:/MyWorkSpace/chongqing/svn_repo");
        ByteArrayOutputStream byteArrayOutputStream = svnDao.getFileFromSVN("/log/2002-2502--20180325SmartEnsembleCheckList.xml");
        System.out.println(byteArrayOutputStream.toString());
    }

}
