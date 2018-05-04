package com.dcits.patchtools.svn.model;


import lombok.EqualsAndHashCode;
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
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "srcPath")
public class FileBlame {
    /**
     * 源文件路径
     */
    private String srcPath;
    /**
     * 源文件打包后所在包路径
     */
    private String modelPath;
    /**
     * 源文件打包后的包内路径
     */
    private String pkgPath;
    /**
     * 源文件类型
     */
    private String fileType;
    /**
     * 源文件的变更记录
     */
    private List<FileModel> commits;
}
