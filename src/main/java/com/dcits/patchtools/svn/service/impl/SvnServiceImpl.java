package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.dao.SvnDao;
import com.dcits.patchtools.svn.model.CommitModel;
import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.service.AbstractPathRuleService;
import com.dcits.patchtools.svn.service.SvnService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Kevin
 * @date 2018-04-29 16:14.
 * @desc
 * @email chenkunh@dcits.com
 */
public class SvnServiceImpl implements SvnService {
    private static final Logger logger = LoggerFactory.getLogger(SvnServiceImpl.class);

    @Resource
    @Setter
    @Getter
    private SvnDao svnDao;
    @Resource
    @Setter
    @Getter
    private AbstractPathRuleService abstractPathRuleService;


    @Override
    public Map<String, List<CommitModel>> getAllCommitHistory() {
        final Map<String, List<CommitModel>> historyMap = new HashMap<>();
        try {
            List<SVNLogEntry> historyList = this.svnDao.getAllCommitHistory();
            this.svnLogEntry2FileBlame(historyMap, historyList);

        } catch (SVNException e) {
            e.printStackTrace();
        }
        return historyMap;
    }

    /**
     * 实体类的转化
     *
     * @param historyMap
     * @param historyList
     */
    private void svnLogEntry2FileBlame(final Map<String, List<CommitModel>> historyMap,
                                       List<SVNLogEntry> historyList) {
        for (SVNLogEntry logEntry : historyList) {
            CommitModel commit = new CommitModel();
            commit.setAuthor(logEntry.getAuthor());
            commit.setTimestamp(logEntry.getDate().toString());
            commit.setDesc(logEntry.getMessage());

            Map<String, SVNLogEntryPath> changedFilePath = logEntry.getChangedPaths();
            Iterator<Map.Entry<String, SVNLogEntryPath>> iterator =
                    changedFilePath.entrySet().iterator();
            while (iterator.hasNext()) {
                SVNLogEntryPath entry = iterator.next().getValue();
                List<CommitModel> blameList = historyMap.get(entry.getPath());
                if (Objects.equals(null, blameList)) {
                    blameList = new ArrayList<>();
                    historyMap.put(entry.getPath(), blameList);
                }
                CommitModel model = new CommitModel(commit);
                model.setType(String.valueOf(entry.getType()));
                blameList.add(model);
            }
        }
    }
}
