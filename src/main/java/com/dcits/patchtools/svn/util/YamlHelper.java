package com.dcits.patchtools.svn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Kevin
 * @date 2018-05-08 21:51.
 * @desc
 * @email chenkunh@dcits.com
 */
public class YamlHelper {
    private static final Logger logger = LoggerFactory.getLogger(YamlHelper.class);

    /**
     * yaml文件解析，解析提交文件列表
     *
     * @param filePath
     * @param matchPatchSet
     * @param deletePatchSet
     */
    public static void yamlPaser(String filePath, Set<String> specificFileSet,
                           Set<String> matchPatchSet, Set<String> deletePatchSet) {
        logger.info("Yml文件解析：" + filePath);
        Yaml yaml = new Yaml();
        File ymlFile = new File(filePath);
        if (ymlFile.exists()) {
            try {
                HashMap<String, List<String>> map = yaml.loadAs(new FileInputStream(ymlFile), HashMap.class);
                List<String> deleteFiles = map.get("deleteFile");
                List<String> matchFiles = map.get("matchFile");

                if (!Objects.equals(deleteFiles, null)) {
                    logger.info("deleteFile:" + deleteFiles.toString());
                    for (String tmp : deleteFiles) deletePatchSet.add(tmp);
                }
                if (!Objects.equals(matchFiles, null)) {
                    logger.info("matchFile:" + matchFiles.toString());
                    for (String tmp : matchFiles) {
                        if (!tmp.endsWith("*")) {
                            specificFileSet.add(tmp);
                        } else if (pathPattern(tmp)) {
                            matchPatchSet.add(tmp);
                            deletePatchSet.add(tmp);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            logger.warn("=================!!!!!未找到" + ymlFile.getName() +
                    "文件!!!!!=================");
        }
    }

    /**
     * 模糊路径匹配规则校验
     *
     * @param path 带校验路径
     * @return
     */
    private static boolean pathPattern(String path) {
        if (!path.endsWith("*")) return false;
        String[] paths = path.split("/");
        String str = paths[paths.length - 1];
        return !Objects.equals(str.substring(0, 1), "*");
    }
}
