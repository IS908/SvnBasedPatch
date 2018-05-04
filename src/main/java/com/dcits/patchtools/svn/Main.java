package com.dcits.patchtools.svn;

import com.dcits.patchtools.svn.model.FileModel;
import com.dcits.patchtools.svn.service.PatchService;
import com.dcits.patchtools.svn.service.SvnService;
import com.dcits.patchtools.svn.service.impl.PatchServiceImpl;
import com.dcits.patchtools.svn.service.impl.SvnServiceImpl;
import com.dcits.patchtools.svn.util.SpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin
 * @date 2018-05-03 11:05.
 * @desc 主函数入口
 * @email chenkunh@dcits.com
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private String basePath;

    private Main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        SpringApplicationContext springApplicationContext = new SpringApplicationContext();
        springApplicationContext.setApplicationContext(context);
        this.basePath = basePath;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Main main = new Main(args);

        PatchService patchService = SpringApplicationContext.getContext().getBean(PatchServiceImpl.class);
        patchService.genPatchListAndReport();

    }

}