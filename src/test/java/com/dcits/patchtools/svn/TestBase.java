package com.dcits.patchtools.svn;

import com.dcits.patchtools.svn.util.SpringContext;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Kevin
 * @date 2018-05-03 09:35.
 * @desc
 * @email chenkunh@dcits.com
 */
public class TestBase {
    protected static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    protected ApplicationContext context;
    @Before
    public void loadSpringApplicationContext() {
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        SpringContext applicationContext = new SpringContext();
        applicationContext.setApplicationContext(context);
    }
}
