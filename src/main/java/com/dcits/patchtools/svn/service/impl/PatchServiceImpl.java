package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.model.FileModel;
import com.dcits.patchtools.svn.model.LogInfo;
import com.dcits.patchtools.svn.service.PatchService;
import com.dcits.patchtools.svn.service.PathRuleService;
import com.dcits.patchtools.svn.service.SvnService;
import com.dcits.patchtools.svn.util.ExcelUtil;
import com.dcits.patchtools.svn.util.XmlUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author Kevin
 * @date 2018-05-04 09:36.
 * @desc
 * @email chenkunh@dcits.com
 */
@Service("patchService")
public class PatchServiceImpl implements PatchService {
    private static final Logger logger = LoggerFactory.getLogger(PatchServiceImpl.class);

    @Resource
    @Setter
    @Getter
    private PathRuleService pathRuleService;
    @Resource
    @Setter
    @Getter
    private SvnService svnService;

    @Value("${patch.xml.dir}")
    @Setter
    @Getter
    private String xmlDir;

    @Value("${patch.excel.dir}")
    @Setter @Getter
    private String excelDir;

    private static final String SRC_MAIN = "/src/main/";
    private static final String RESOURCES = SRC_MAIN + "resources/";
    private static final String JAVA = SRC_MAIN + "java/";

    @Override
    public boolean genPatchListAndReport() {
        logger.debug("XML_DIR:" + xmlDir);
        logger.debug("EXCEL_DIR:" + excelDir);

        // 用于生成增量描述文件
        List<FileBlame> fileBlameList = new ArrayList<>();
        // 用于生成增量清单
        Map<LogInfo, Set<String>> logInfoMap = new HashMap<>();
        // 用于存放pom到jar包的映射关系，减少与SVN服务端的交互
        Map<String, String> moduleMap = new HashMap<>();
        LogInfo logInfo = new LogInfo();
        Map<String, List<FileModel>> logMap = svnService.getAllCommitHistory(true);
        Iterator<Map.Entry<String, List<FileModel>>> iterator = logMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<FileModel>> entry = iterator.next();
            String srcPath = entry.getKey();
            List<FileModel> fileModels = entry.getValue();
            if (pathRuleService.pathExcludeFilter(srcPath)) continue;

            // 送测清单的处理
            for (FileModel fileModel : fileModels) {
                logInfo.setCommitInfo(fileModel.getDesc());
                logInfo.setAuthor(fileModel.getAuthor());
                logInfo.setTimestamp(fileModel.getTimestamp());
                Set<String> fileSet = logInfoMap.get(logInfo);
                if (Objects.equals(null, fileSet)) {
                    fileSet = new HashSet<>();
                    LogInfo tmp = new LogInfo(logInfo);
                    logInfoMap.put(tmp, fileSet);
                }
                fileSet.add(srcPath);
            }

            if (pathRuleService.pathListOnlyFilter(srcPath)) continue;

            // 增量描述文件的处理
            FileBlame fileBlame = new FileBlame();
            fileBlame.setSrcPath(srcPath);
            fileBlame.setCommits(entry.getValue());
            String[] strs = srcPath.split("\\.");
            fileBlame.setFileType(strs[strs.length - 1]);

            // 第一次填充module和pkgPath
            fillPomContent(fileBlame.getSrcPath(), moduleMap, fileBlame);
            if (fileBlame.getSrcPath().contains(RESOURCES)) {
                String[] paths = fileBlame.getSrcPath().split(RESOURCES);
                fileBlame.setPkgPath(paths[paths.length - 1]);
            } else if (fileBlame.getSrcPath().contains(JAVA)) {
                String[] paths = fileBlame.getSrcPath().split(JAVA);
                fileBlame.setPkgPath(paths[paths.length - 1]);
            }

            pathRuleService.pathConvert(fileBlame);

            fileBlameList.add(fileBlame);
        }

        // ===================== 测试输出 ===================
        String path = System.getProperty("user.dir");
        logger.info("开始生成增量清单...");
        XmlUtil.entity2XmlFile(fileBlameList, path);
        logger.info("开始生成送测清单...");
        ExcelUtil.genExcel(logInfoMap, path);
        return true;
    }

    /**
     * 获取并解析POM文件，填充module和pkgPath值
     *
     * @param srcPath
     * @param moduleMap
     * @param fileBlame
     */
    private void fillPomContent(String srcPath,
                                Map<String, String> moduleMap,
                                FileBlame fileBlame) {
        if (!srcPath.contains(SRC_MAIN)) return;
        String[] pathSplit = srcPath.split(SRC_MAIN);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < pathSplit.length - 1; i++) {
            sb.append(pathSplit[i]);
            sb.append(SRC_MAIN);
        }
        String pomPath = sb.toString();
        pomPath = pomPath.substring(0, pomPath.length() - SRC_MAIN.length()) + "/pom.xml";
        String moduleName = moduleMap.get(pomPath);
        if (!Objects.equals(null, moduleName)) {
            fileBlame.setModule(moduleName);
            return;
        }
        try {
            ByteArrayOutputStream baos = svnService.getFileFromSVN(pomPath);
            moduleName = XmlUtil.pom2PackageName(baos);
            fileBlame.setModule(moduleName);
            moduleMap.put(pomPath, moduleName);
        } catch (SVNException e) {
            logger.warn(e.getErrorMessage().getFullMessage());
            fillPomContent(pomPath, moduleMap, fileBlame);
        }
    }

    @Override
    public boolean executePatch(String xmlFile) {
        // todo: 进行增量抽取
        return true;
    }
}
