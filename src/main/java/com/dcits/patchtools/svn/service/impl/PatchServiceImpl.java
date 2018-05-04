package com.dcits.patchtools.svn.service.impl;

import com.dcits.patchtools.svn.model.ConfModel;
import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.model.FileModel;
import com.dcits.patchtools.svn.model.LogInfo;
import com.dcits.patchtools.svn.service.PatchService;
import com.dcits.patchtools.svn.service.PathRuleService;
import com.dcits.patchtools.svn.service.SvnService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Setter @Getter
    private PathRuleService pathRuleService;
    @Resource
    @Setter @Getter
    private SvnService svnService;

    @Value("${patch.xml.dir}")
    @Setter @Getter
    private String xmlPath;

    @Override
    public boolean genPatchListAndReport() {
        // 用于生成增量描述文件
        List<FileBlame> fileBlameList = new ArrayList<>();
        // 用于生成增量清单
        Map<LogInfo, Set<String>> logInfoMap = new HashMap<>();
        LogInfo logInfo = new LogInfo();
        Map<String, List<FileModel>> logMap = svnService.getAllCommitHistory();
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
                Set<String> fileSet =  logInfoMap.get(logInfo);
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

            pathRuleService.pathConvert(fileBlame);

            fileBlameList.add(fileBlame);
        }

        // ===================== 测试输出 ===================
        System.out.println("增量清单：");
        for (FileBlame fb : fileBlameList) {
            System.out.println(fb.getSrcPath() + ", ");
            System.out.print(fb.getFileType() + ", ");
            System.out.print(fb.getPkgPath() + ", ");
            System.out.println(fb.getModelPath() + ", ");
            System.out.println(fb.getCommits());
        }
        System.out.println("送测清单：");
        Iterator<Map.Entry<LogInfo, Set<String>>> it = logInfoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LogInfo, Set<String>> entry = it.next();
            LogInfo tmp = entry.getKey();
            Set<String> set = entry.getValue();
            System.out.println("提交注释：" + tmp.getCommitInfo());
            System.out.println("提交人：" + tmp.getAuthor() + ", 时间：" + tmp.getTimestamp());
            System.out.println("文件清单：" + set);
        }
        // ===================== 测试输出 ===================
        return true;
    }

    @Override
    public boolean executePatch(String xmlFile) {

        return true;
    }

    public static void main(String[] args) {
        String test = "abcd.efgh.ijklm";
        System.out.println(test.substring(test.lastIndexOf('.')));
        String[] strs = test.split("\\.");
        System.out.println(strs[strs.length - 1]);
    }
}
