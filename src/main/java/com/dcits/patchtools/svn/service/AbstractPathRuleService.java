package com.dcits.patchtools.svn.service;

import com.dcits.patchtools.svn.model.FileBlame;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Kevin
 * @date 2018-04-29 17:09.
 * @desc
 * @email chenkunh@dcits.com
 */
@Component
public abstract class AbstractPathRuleService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPathRuleService.class);

    @Resource
    @Setter protected Map<String, Object> map;

    public boolean excute(FileBlame fileBlame) {
        String path = fileBlame.getSrcPath();
        if (!this.pathExcludeFilter(path)) {
            return false;
        }
        if (!this.pathListOnlyFilter(path)) {
            return false;
        }
        this.pathConvert(fileBlame);
        return true;
    }

    /**
     * 对应pathRule.yml文件的pathExclued节点的信息
     * 进行规则过滤
     *
     * @param path 需过滤检测的路径
     * @return true：保留；false：不保留
     */
    protected abstract boolean pathExcludeFilter(String path);

    /**
     * 仅在增量清单中体现，不进入增量部署包
     *
     * @param path 需规则判定的路径
     * @return true：进入增量清单和增量部署包；false：仅进入增量清单，不进入增量部署包
     */
    protected abstract boolean pathListOnlyFilter(String path);

    /**
     * 进行路径转化，填充model、pkgPath相关信息
     * 对应pathRule.yml文件的pathConvert节点的信息
     *
     * @param fileBlame
     * @return
     */
    protected abstract void pathConvert(FileBlame fileBlame);


}
