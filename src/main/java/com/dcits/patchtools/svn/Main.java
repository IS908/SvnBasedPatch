package com.dcits.patchtools.svn;

import com.dcits.patchtools.svn.util.DateUtil;
import com.dcits.patchtools.svn.util.SpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * @author Kevin
 * @date 2018-05-03 11:05.
 * @desc 主函数入口
 * @email chenkunh@dcits.com
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private Main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        SpringApplicationContext springApplicationContext = new SpringApplicationContext();
        springApplicationContext.setApplicationContext(context);
    }

    /**
     * 程序入口
     *
     * @param args [0]：xml-生成增量清单和送测清单；zip-生成增量部署包
     * @param args [1]：ymlPrefix：非版本控制文件的抽取清单
     * @param args [2]：baseDir：本地文件根目录
     * @param args [3]：versionFrom：增量抽取起始版本号
     * @param args [4]：versionTo：增量抽取截止版本号
     */
    public static void main(String[] args) {
        int argsCount = args.length;
        if (argsCount < 4 || argsCount > 5) {
            logger.error("不识别的参数个数：");
            System.out.println("args [0]：xml-生成增量清单和送测清单；zip-生成增量部署包");
            System.out.println("args [1]：ymlPrefix：非版本控制文件的抽取清单");
            System.out.println("args [2]：baseDir：本地文件根目录");
            System.out.println("args [3]：versionFrom：增量抽取起始版本号");
            System.out.println("args [4]：versionTo：【选填】增量抽取截止版本号(为空则取当前最新版本)");
            return;
        }
        String execType = args[0];
        String ymlPrefix = args[1];
        String baseDir = args[2];
        baseDir += baseDir.endsWith(File.separator) ? "" : File.separator;
        long versionFrom = Long.valueOf(args[3]);
        long versionTo = -1;
        if (argsCount == 5) versionTo = Long.valueOf(args[4]);

        new Main(args);

        PatchHandler patchHandler = SpringApplicationContext
                .getContext().getBean(PatchHandler.class);
        switch (execType) {
            case "xml":
                patchHandler.patchListAndReport(baseDir, versionFrom, versionTo);
                logger.info("送测清单、增量描述文件生成完成！");
                break;
            case "zip":
                patchHandler.patchExecute(baseDir, versionFrom, versionTo, ymlPrefix);
                logger.info("增量部署包生成完成！");
                break;
            default:
                break;
        }


        /*System.out.println();
        Properties properties = System.getProperties();
        System.out.println(properties.size());
        for (Object o : properties.keySet()) {
            System.out.println("" + o + "=" + properties.get(o));
        }*/
    }

}
