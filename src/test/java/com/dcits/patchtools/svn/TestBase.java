package com.dcits.patchtools.svn;

import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Kevin
 * @date 2018-05-03 09:35.
 * @desc
 * @email chenkunh@dcits.com
 */
public class TestBase {
    protected ApplicationContext context;
    @Before
    public void loadSpringApplicationContext() {
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
    }
}
