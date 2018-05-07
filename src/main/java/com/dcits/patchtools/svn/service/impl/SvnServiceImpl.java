package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.dao.SvnDao;
import com.dcits.patchtools.svn.model.FileModel;
import com.dcits.patchtools.svn.service.SvnService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Kevin
 * @date 2018-04-29 16:14.
 * @desc
 * @email chenkunh@dcits.com
 */
@Component("svnService")
public class SvnServiceImpl implements SvnService {
    private static final Logger logger = LoggerFactory.getLogger(SvnServiceImpl.class);

    @Resource
    @Setter
    @Getter
    private SvnDao svnDao;

    @Override
    public Map<String, List<FileModel>> getAllCommitHistory() {
        final Map<String, List<FileModel>> historyMap = new HashMap<>();
        List<SVNLogEntry> historyList = this.svnDao.getAllCommitHistory();
        this.svnLogEntry2FileBlame(historyMap, historyList);
        return historyMap;
    }

    /**
     * 实体类的转化
     *
     * @param historyMap
     * @param historyList
     */
    private void svnLogEntry2FileBlame(final Map<String,
            List<FileModel>> historyMap, List<SVNLogEntry> historyList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        for (SVNLogEntry logEntry : historyList) {
            FileModel commit = new FileModel();
            commit.setAuthor(logEntry.getAuthor());
            commit.setTimestamp(sdf.format(logEntry.getDate()));
            commit.setDesc(logEntry.getMessage());

            Map<String, SVNLogEntryPath> changedFilePath = logEntry.getChangedPaths();
            Iterator<Map.Entry<String, SVNLogEntryPath>> iterator =
                    changedFilePath.entrySet().iterator();
            while (iterator.hasNext()) {
                SVNLogEntryPath entry = iterator.next().getValue();
                List<FileModel> blameList = historyMap.get(entry.getPath());
                if (Objects.equals(null, blameList)) {
                    blameList = new ArrayList<>();
                    historyMap.put(entry.getPath(), blameList);
                }
                FileModel model = new FileModel(commit);
                model.setType(String.valueOf(entry.getType()));
                blameList.add(model);
            }
        }
    }
}
