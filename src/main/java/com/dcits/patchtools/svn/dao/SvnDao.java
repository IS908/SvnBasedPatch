package com.dcits.patchtools.svn.dao;

import com.dcits.patchtools.svn.model.CommitModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
@Component
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

    @Deprecated //该部分逻辑处理及fileBlame转化放在service层
    public Map<String, List<CommitModel>> filterCommitHistory() throws Exception {
        // 过滤条件
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date begin = format.parse("2018-04-13");
        final Date end = format.parse("2018-05-14");
        final String author = "";

        final Map<String, List<CommitModel>> history = new HashMap<>();
//        List<String> history = new ArrayList<>();
        //String[] 为过滤的文件路径前缀，为空表示不进行过滤
        repository.log(new String[]{""},
                0, -1,
                true, true,
                new ISVNLogEntryHandler() {
                    @Override
                    public void handleLogEntry(SVNLogEntry svnlogentry)
                            throws SVNException {
                        //依据提交时间进行过滤
                        if (svnlogentry.getDate().after(begin)
                                && svnlogentry.getDate().before(end)) {
                            // 依据提交人过滤
                            if (!"".equals(author)) {
                                if (author.equals(svnlogentry.getAuthor())) {
                                    fillResult(svnlogentry);
                                }
                            } else {
                                fillResult(svnlogentry);
                            }
                        }
                    }

                    void fillResult(SVNLogEntry svnlogentry) {
                        //getChangedPaths为提交的历史记录MAP key为文件名，value为文件详情
                        /*CommitModel commit = new CommitModel();
                        commit.setAuthor(svnlogentry.getAuthor());
                        commit.setTimestamp(svnlogentry.getDate().toString());
                        commit.setDesc(svnlogentry.getMessage());


                        Map<String, SVNLogEntryPath> changedFilePath = svnlogentry.getChangedPaths();
                        Iterator<Map.Entry<String, SVNLogEntryPath>> iterator = changedFilePath.entrySet().iterator();
                        while (iterator.hasNext()) {
                            SVNLogEntryPath entry = iterator.next().getValue();
                            List<CommitModel> blameList = history.get(entry.getPath());
                            if (Objects.equals(null, blameList)) {
                                blameList = new ArrayList<>();
                                history.put(entry.getPath(), blameList);
                            }
                            CommitModel model = new CommitModel(commit);
                            model.setType(String.valueOf(entry.getType()));
                            blameList.add(model);
                        }*/
                    }
                });
        return history;
    }


    /*public static void main(String[] args) throws Exception {
        String url = "file:///D:/MyWorkSpace/chongqing/svn_repo";
        SvnDao svnDao = new SvnDao(url);
        Map<String, List<CommitModel>> history =svnDao.filterCommitHistory();
        Iterator<Map.Entry<String, List<CommitModel>>> entryIterator = history.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, List<CommitModel>> entry = entryIterator.next();
            logger.info("=============================Key:" + entry.getKey());
            logger.info("ValueList:" + entry.getValue());
        }
        logger.info("history:" + history.toString());
    }*/
}
