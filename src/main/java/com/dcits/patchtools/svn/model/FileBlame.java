package com.dcits.patchtools.svn.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Kevin
 * @desc 单文件提交历史记录
 * @email chenkunh@dcits.com
 * @date 2018-04-26 16:21.
 */
@Setter @Getter @ToString
public class FileBlame {
    private String srcPath;
    private String modelPath;
    private String pkgPath;
    private String fileType;
    private List<CommitModel> commits;
}
