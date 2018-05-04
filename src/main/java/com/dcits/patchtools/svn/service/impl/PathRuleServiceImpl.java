package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.service.PathRuleService;
import com.dcits.patchtools.svn.util.PatchEnum;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kevin
 * @date 2018-04-29 17:09.
 * @desc
 * @email chenkunh@dcits.com
 */
@Service("pathRuleService")
public class PathRuleServiceImpl implements PathRuleService, InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(PathRuleServiceImpl.class);

    @Resource
    @Setter
    @Getter
    private Map<String, Object> yamlMap;

    private Map<String, Object> pathExcluedMap;
    private Map<String, Object> listOnlyMap;
    private Map<String, Object> pathConvertMap;

    @Override
    public PatchEnum excute(FileBlame fileBlame) {
        String path = fileBlame.getSrcPath();
        if (this.pathExcludeFilter(path)) {
            return PatchEnum.EXCLUDE;
        }
        if (this.pathListOnlyFilter(path)) {
            return PatchEnum.LIST_ONLY;
        }
        this.pathConvert(fileBlame);
        return PatchEnum.INCLUDE;
    }

    @Override
    public boolean pathExcludeFilter(String path) {
        // 规则过滤：true-不保留；false-保留
        return this.pathFilter(path, pathExcluedMap);
    }

    @Override
    public boolean pathListOnlyFilter(String path) {
        // 规则过滤：true-仅在送测清单中体现；false-送测清单和增量包中都保留
        return this.pathFilter(path, listOnlyMap);
    }

    @Override
    public void pathConvert(FileBlame fileBlame) {
        if (Objects.equals(null, pathConvertMap)) return;
        // todo: 进行路径转化，填充model、pkgPath相关信息
        Object obj = pathConvertMap.get("specific");
        if (!Objects.equals(null, obj)) {

        }
        obj = pathConvertMap.get("contains");
        if (!Objects.equals(null, obj)) {

        }
        obj = pathConvertMap.get("perfix");
        if (!Objects.equals(null, obj)) {

        }
        obj = pathConvertMap.get("surfix");
        if (!Objects.equals(null, obj)) {

        }
    }

    /**
     * 路径规则过滤
     * @param path
     * @param map
     * @return
     */
    private boolean pathFilter(String path, Map<String, Object> map) {
        if (Objects.equals(null, map)) return false;
        Object obj = map.get("specific");
        if (!Objects.equals(null, obj) && obj instanceof List) {
            List<String> specificList = (ArrayList) obj;
            for (String specific : specificList) {
                if (path.endsWith(specific)) return true;
            }
        }
        obj = map.get("contains");
        if (!Objects.equals(null, obj) && obj instanceof List) {
            List<String> containsList = (ArrayList) obj;
            for (String match : containsList) {
                if (path.contains(match)) return true;
            }
        }
        obj = map.get("perfix");
        if (!Objects.equals(null, obj) && obj instanceof List) {
            List<String> perfixList = (ArrayList) obj;
            for (String perfix : perfixList) {
                if (path.startsWith(perfix)) return true;
            }
        }
        obj = map.get("surfix");
        if (!Objects.equals(null, obj) && obj instanceof List) {
            List<String> surfixList = (ArrayList) obj;
            for (String surfix : surfixList) {
                if (path.endsWith(surfix)) return true;
            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Object excludeObj = yamlMap.get("pathExclued");
        if (excludeObj instanceof Map) {
            pathExcluedMap = (Map<String, Object>) excludeObj;
        }

        Object listOnlyObj = yamlMap.get("listOnly");
        if (listOnlyObj instanceof Map) {
            listOnlyMap = (Map<String, Object>) listOnlyObj;
        }

        Object pathConvertObj = yamlMap.get("pathConvert");
        if (pathConvertObj instanceof Map) {
            pathConvertMap = (Map<String, Object>) pathConvertObj;
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
