package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.service.AbstractPathRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Kevin
 * @date 2018-04-29 17:09.
 * @desc
 * @email chenkunh@dcits.com
 */
public class DefaultPathRuleService extends AbstractPathRuleService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPathRuleService.class);

    @Override
    protected boolean pathExcludeFilter(String path) {
        Object excludeObj = map.get("pathExclued");
        if (excludeObj instanceof Map) {
            Map<String, Object> excludeMap = (Map<String, Object>) excludeObj;
        }
        // todo: 规则过滤，true：保留；false：不保留
        return false;
    }

    @Override
    protected boolean pathListOnlyFilter(String path) {
        Object excludeObj = map.get("listOnly");
        if (excludeObj instanceof Map) {
            Map<String, Object> excludeMap = (Map<String, Object>) excludeObj;
        }
        // todo: 仅在增量清单中体现，不进入增量部署包
        return false;
    }

    @Override
    protected void pathConvert(FileBlame fileBlame) {
        Object excludeObj = map.get("pathConvert");
        if (excludeObj instanceof Map) {
            Map<String, Object> excludeMap = (Map<String, Object>) excludeObj;
        }
        // todo: 进行路径转化，填充model、pkgPath相关信息
    }
}
