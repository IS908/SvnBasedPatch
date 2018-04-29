package com.dcits.patchtools.svn.model;

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
@Setter @Getter @ToString
public class CommitModel {
    public CommitModel(){}

    public CommitModel(CommitModel commitModel) {
        this.setAuthor(commitModel.getAuthor());
        this.setTimestamp(commitModel.getTimestamp());
        this.setDesc(commitModel.getDesc());
        this.setType(Objects.equals(null, commitModel.getType())?"":commitModel.getType());
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
     * 本次提交文件类型
     */
    private String type;
}
