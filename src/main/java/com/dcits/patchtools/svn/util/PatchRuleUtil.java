package com.dcits.patchtools.svn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Kevin
 * @date 2018-04-29 15:03.
 * @desc
 * @email chenkunh@dcits.com
 */
@Component
public class PatchRuleUtil {
    private static final Logger logger = LoggerFactory.getLogger(PatchRuleUtil.class);


    public String excute(String path) {

        return null;
    }

    /**
     * 判定该文件是否要解析放入增量文件中
     * @param path
     * @return
     */
    public boolean check(String path) {

        return true;
    }
}
