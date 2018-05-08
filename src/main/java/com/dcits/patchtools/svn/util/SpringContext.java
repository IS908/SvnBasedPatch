package com.dcits.patchtools.svn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Kevin
 * @date 2018-05-03 16:44.
 * @desc 自定义存放ApplicationContext，便于随时获取bean
 * @email chenkunh@dcits.com
 */
public class SpringContext implements ApplicationContextAware, BeanFactoryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SpringContext.class);

    private static ApplicationContext context;

    public SpringContext() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
    }
}
