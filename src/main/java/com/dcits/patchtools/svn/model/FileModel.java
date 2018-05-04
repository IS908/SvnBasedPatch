package com.dcits.patchtools.svn.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Kevin
 * @desc 单个文件的单条提交信息
 * @email chenkunh@dcits.com
 * @date 2018-04-26 16:27.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class FileModel {
    public FileModel() {
    }

    public FileModel(FileModel fileModel) {
        this.setAuthor(fileModel.getAuthor());
        this.setTimestamp(fileModel.getTimestamp());
        this.setDesc(fileModel.getDesc());
        this.setType(Objects.equals(null, fileModel.getType()) ? "" : fileModel.getType());
    }

    /**
     * 记录提交者
     */
    private String author;
    /**
     * 记录提交时间
     */
    private String timestamp;
    /**
     * 提交注释
     */
    private String desc;
    /**
     * 文件变动类型
     */
    private String type;
}
