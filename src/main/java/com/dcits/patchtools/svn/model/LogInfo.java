package com.dcits.patchtools.svn.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Kevin
 * @date 2018-05-04 11:27.
 * @desc
 * @email chenkunh@dcits.com
 */
@Setter @Getter @ToString @EqualsAndHashCode
public class LogInfo {
    public LogInfo() {
    }

    public LogInfo(LogInfo logInfo) {
        this.commitInfo = logInfo.getCommitInfo();
        this.author = logInfo.getAuthor();
        this.timestamp = logInfo.getTimestamp();
    }

    private String commitInfo;
    private String author;
    private String timestamp;

}
