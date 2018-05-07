package com.dcits.patchtools.svn.util;

import com.dcits.patchtools.svn.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlMapFactoryBean;

/**
 * @author Kevin
 * @date 2018-05-03 16:32.
 * @desc
 * @email chenkunh@dcits.com
 */
public class YamlTest extends TestBase {
    @Test
    public void yamlTest() {
        YamlMapFactoryBean yamlMapFactoryBean = context.getBean(YamlMapFactoryBean.class);
        logger.info(String.valueOf(yamlMapFactoryBean.getObjectType()));
    }
}
