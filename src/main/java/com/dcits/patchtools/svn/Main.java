package com.dcits.patchtools.svn;

import com.dcits.patchtools.svn.model.CommitModel;
import com.dcits.patchtools.svn.service.SvnService;
import com.dcits.patchtools.svn.service.impl.SvnServiceImpl;
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

    private ApplicationContext context;
    private String basePath;

    private Main(String[] args) {
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        this.basePath = basePath;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Main main = new Main(args);
        SvnService svnService = main.context.getBean(SvnServiceImpl.class);
        final Map<String, List<CommitModel>> historyMap = svnService.getAllCommitHistory();
        Iterator<Map.Entry<String, List<CommitModel>>> iterator = historyMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<CommitModel>> entry = iterator.next();
            String key = entry.getKey();
            List<CommitModel> value = entry.getValue();
            logger.info(key + "======" + value.toString());
        }
    }
}
