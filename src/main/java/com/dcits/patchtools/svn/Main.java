package com.dcits.patchtools.svn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ApplicationContext context;
    private String basePath;

    private Main(String[] args) {
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        this.basePath = basePath;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Main main = new Main(args);
    }
}
