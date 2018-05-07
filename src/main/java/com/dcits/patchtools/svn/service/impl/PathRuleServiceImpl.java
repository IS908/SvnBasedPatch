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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Kevin
 * @date 2018-04-29 17:09.
 * @desc
 * @email chenkunh@dcits.com
 */
@Component("pathRuleService")
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
        Object obj = pathConvertMap.get("contains");
        if (obj instanceof Map) {
            Map<String, String> containsMap = (Map) obj;
            Iterator<Map.Entry<String, String>> iterator = containsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                if (fileBlame.getSrcPath().contains(key)) {
                    String module = fileBlame.getModule();
                    fileBlame.setModule(entry.getValue() +
                            (Objects.equals(null, module) ? "" : module));
                    return;
                }
            }
        }
        obj = pathConvertMap.get("surfix");
        if (obj instanceof Map) {
            Map<String, String> surfixMap = (Map) obj;
            Iterator<Map.Entry<String, String>> iterator = surfixMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                if (fileBlame.getSrcPath().endsWith(key)) {
                    String module = fileBlame.getModule();
                    fileBlame.setModule(entry.getValue() +
                            (Objects.equals(null, module) ? "" : module));
                    return;
                }
            }
        }
        obj = pathConvertMap.get("prefix");
        if (obj instanceof Map) {
            Map<String, String> prefixMap = (Map) obj;
            Iterator<Map.Entry<String, String>> iterator = prefixMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                if (fileBlame.getSrcPath().startsWith(key)) {
                    String module = fileBlame.getModule();
                    fileBlame.setModule(entry.getValue() +
                            (Objects.equals(null, module) ? "" : module));
                    return;
                }
            }
        }
        obj = pathConvertMap.get("specific");
        if (obj instanceof Map) {
            Map<String, String> specificMap = (Map) obj;
            Iterator<Map.Entry<String, String>> iterator = specificMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                if (fileBlame.getSrcPath().equals(key)) {
                    String module = fileBlame.getModule();
                    fileBlame.setModule(entry.getValue() +
                            (Objects.equals(null, module) ? "" : module));
                    return;
                }
            }
        }
    }

    private void fillModuleAndPkgPath() {

    }

    /**
     * 路径规则过滤
     *
     * @param path
     * @param map
     * @return
     */
    private boolean pathFilter(String path, Map<String, Object> map) {
        if (Objects.equals(null, map)) return false;
        Object obj = map.get("contains");
        if (obj instanceof List) {
            List<String> containsList = (ArrayList) obj;
            for (String match : containsList) {
                if (path.contains(match)) return true;
            }
        }
        obj = map.get("surfix");
        if (obj instanceof List) {
            List<String> surfixList = (ArrayList) obj;
            for (String surfix : surfixList) {
                if (path.endsWith(surfix)) return true;
            }
        }
        obj = map.get("prefix");
        if (obj instanceof List) {
            List<String> prefixList = (ArrayList) obj;
            for (String prefix : prefixList) {
                if (path.startsWith(prefix)) return true;
            }
        }
        obj = map.get("specific");
        if (obj instanceof List) {
            List<String> specificList = (ArrayList) obj;
            for (String specific : specificList) {
                if (path.equals(specific)) return true;
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
        logger.info(PathRuleServiceImpl.class.getName() + " destroy ! ");
    }
}
