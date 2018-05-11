package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.model.FileModel;
import com.dcits.patchtools.svn.model.LogInfo;
import com.dcits.patchtools.svn.service.PatchService;
import com.dcits.patchtools.svn.service.PathRuleService;
import com.dcits.patchtools.svn.service.SvnService;
import com.dcits.patchtools.svn.util.DateUtil;
import com.dcits.patchtools.svn.util.ExcelUtil;
import com.dcits.patchtools.svn.util.XmlUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

/**
 * @author Kevin
 * @date 2018-05-04 09:36.
 * @desc
 * @email chenkunh@dcits.com
 */
public class PatchServiceImpl implements PatchService {
    private static final Logger logger = LoggerFactory.getLogger(PatchServiceImpl.class);

    @Setter
    @Getter
    private PathRuleService pathRuleService;

    @Setter
    @Getter
    private SvnService svnService;

    private String xmlDir;
    private String xmlModuleSurfix;
    private String excelDir;
    private boolean snapshotTimestamp;

    private static final String SRC_MAIN = "/src/main/";
    private static final String RESOURCES = SRC_MAIN + "resources/";
    private static final String JAVA = SRC_MAIN + "java/";
    private static final String WEBAPP = SRC_MAIN + "webapp/";

    @Override
    public boolean genPatchListAndReport(String baseDir, long versionFrom, long versionTo) {
        logger.debug("XML_DIR:" + xmlDir);
        logger.debug("EXCEL_DIR:" + excelDir);

        // 用于生成增量描述文件
        List<FileBlame> fileBlameList = new ArrayList<>();
        // 用于生成增量清单
        Map<LogInfo, Set<String>> logInfoMap = new HashMap<>();
        // 用于存放pom到jar包的映射关系，减少与SVN服务端的交互
        Map<String, String> moduleMap = new HashMap<>();
        LogInfo logInfo = new LogInfo();
        Map<String, List<FileModel>> logMap = svnService.getAllCommitHistory(
                versionFrom, versionTo, true);
        Iterator<Map.Entry<String, List<FileModel>>> iterator = logMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<FileModel>> entry = iterator.next();
            String srcPath = entry.getKey();
            List<FileModel> fileModels = entry.getValue();

            // 规则：忽略文件规则执行
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

            // 规则：仅在送测清单，不在增量包规则执行
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

            // 规则：module值转换规则执行
            pathRuleService.pathConvert(fileBlame);
            if (srcPath.contains(WEBAPP)) {
                String[] tmpStrs = srcPath.split(WEBAPP);
                String moduleName = tmpStrs[tmpStrs.length - 1];
                fileBlame.setModule(WEBAPP + moduleName);
                fileBlame.setPkgPath(WEBAPP + moduleName);
            }

            fileBlameList.add(fileBlame);
        }
        String fileName = this.genFileFullPath(baseDir + xmlDir,
                versionFrom, versionTo, xmlModuleSurfix);

        logger.info("开始生成增量清单...");
        String patchFlag = versionFrom + "-" + (versionTo == -1 ? "E" : versionTo);
        XmlUtil.entity2XmlFile(fileBlameList, fileName);
        logger.info("开始生成送测清单...");
        ExcelUtil.genExcel(logInfoMap, baseDir + excelDir, patchFlag);
        return true;
    }

    @Override
    public Set<String> executePatch(String baseDir, long versionFrom, long versionTo) {
        String fileName = this.genFileFullPath(baseDir + xmlDir,
                versionFrom, versionTo, xmlModuleSurfix);
        return XmlUtil.patchFileReader(fileName);
    }

    /**
     * 生成XML文件全路径
     *
     * @param baseDir
     * @param versionFrom
     * @param versionTo
     * @return
     */
    private String genFileFullPath(String baseDir, long versionFrom, long versionTo, String xmlSurfix) {
        baseDir = baseDir.endsWith(File.separator) ? baseDir : baseDir + (File.separator);
        String runDate = DateUtil.getRunDate();
        String patchFlag = versionFrom + "-" + (versionTo == -1 ? "E" : versionTo);
        String fileFullName = baseDir + runDate + "_" + patchFlag + "_" + xmlSurfix + ".xml";
        return fileFullName;
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
            moduleName = XmlUtil.pom2PackageName(baos, snapshotTimestamp);
            fileBlame.setModule(moduleName);
            moduleMap.put(pomPath, moduleName);
        } catch (SVNException e) {
            logger.warn(e.getErrorMessage().getFullMessage());
            fillPomContent(pomPath, moduleMap, fileBlame);
        }
    }


    public String getXmlDir() {
        return xmlDir;
    }

    public void setXmlDir(String xmlDir) {
        this.xmlDir = Objects.equals("@", xmlDir) ? "" : xmlDir;
    }

    public String getExcelDir() {
        return excelDir;
    }

    public void setExcelDir(String excelDir) {
        this.excelDir = Objects.equals("@", excelDir) ? "" : excelDir;
    }

    public String getXmlModuleSurfix() {
        return xmlModuleSurfix;
    }

    public void setXmlModuleSurfix(String xmlModuleSurfix) {
        this.xmlModuleSurfix = xmlModuleSurfix;
    }

    public boolean isSnapshotTimestamp() {
        return snapshotTimestamp;
    }

    public void setSnapshotTimestamp(boolean snapshotTimestamp) {
        this.snapshotTimestamp = snapshotTimestamp;
    }
}
