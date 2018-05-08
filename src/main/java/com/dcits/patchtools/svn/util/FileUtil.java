package com.dcits.patchtools.svn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Kevin
 * @date 2018-05-05 10:21.
 * @desc
 * @email chenkunh@dcits.com
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 复制一个目录及其子目录到另外一个目录
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if (!src.isDirectory()) return;
        if (!dest.exists()) {
            dest.mkdir();
        }
        String files[] = src.list();
        for (String file : files) {
            File srcFile = new File(src, file);
            File destFile = new File(dest, file);
            // 递归复制
            copyFolder(srcFile, destFile);
        }
    }

    /**
     * @param baseDir
     * @param matchSet
     * @return
     */
    public static void mvMatchFiles(String baseDir, String targetSrc,
                                    String targetDest, Set<String> matchSet) {
        StringBuffer bufferSrc = new StringBuffer();
        StringBuffer bufferDest = new StringBuffer();
        for (String matchStr : matchSet) {
            String[] tmpStr = matchStr.split("/");
            bufferSrc.setLength(0);
            bufferSrc.append(baseDir).append(targetSrc);

            bufferDest.setLength(0);
            bufferDest.append(baseDir).append(targetDest);

            for (int i = 0; i < tmpStr.length - 1; ++i) {
                bufferSrc.append("/").append(tmpStr[i]);
                bufferDest.append("/").append(tmpStr[i]);
            }

            bufferSrc.append("/");
            bufferDest.append("/");

            File srcFolder = new File(bufferSrc.toString());
            if (!srcFolder.exists() || srcFolder.isFile()) continue;

            String matchTmp = tmpStr[tmpStr.length - 1];
            matchTmp = matchTmp.substring(0, matchTmp.length() - 1);

            File[] listFiles = srcFolder.listFiles();
            if (Objects.equals(null, listFiles) || listFiles.length < 1) continue;
            if (matchTmp.length() < 1) {
                for (File file : listFiles) {
                    logger.info("抽取文件：" + file.getName());
                    file.renameTo(new File(bufferDest.toString() + file.getName()));
                }
                continue;
            }

            for (File file : listFiles) {
                if (file.getName().contains(matchTmp)) {
                    logger.info("抽取文件：" + file.getName());
                    file.renameTo(new File(bufferDest.toString() + file.getName()));
                }
            }
        }
    }

    /**
     * 定义一个内部类，实现FilenameFilter接口，这是一个文件过滤器接口
     * 用于传递给list方法，对dir目录下的文件过滤用的
     * list方法对每个文件回调accept方法判定dir目录下的name文件是否满
     * 足我的用regex编译的pattern模式。如果满足返回true。将这个文件加
     * 入到list方法的返回string列表中。
     *
     * @param regex
     * @return
     */
    @Deprecated
    public static FilenameFilter filter(final String regex) {

        return new FilenameFilter() {
            private Pattern pattern = Pattern.compile(regex);

            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        };
    }

    /**
     * 复制给定文件列表中的文件到指定目录
     *
     * @param src
     * @param dest
     * @param set  待移动文件相对路径
     */
    public static void mvFile(String src, String dest, Set<String> set) {
        src += src.endsWith(File.separator) ? "" : File.separator;
        dest += dest.endsWith(File.separator) ? "" : File.separator;
        logger.info("源目录：" + src);
        logger.info("目标目录：" + dest);
        File srcFile;
        for (String file : set) {
            srcFile = new File(src + file);
            if (!srcFile.exists()) continue;
            logger.info("抽取文件：" + srcFile.getName());
            srcFile.renameTo(new File(dest + file));
        }
    }

    /**
     * 移动指定文件夹下所有文件到目标文件夹下（不递归子目录）
     *
     * @param baseDir
     * @param srcForder
     * @param destFolder
     */
    public static void mvFile(String baseDir, String srcForder, String destFolder) {
        srcForder = baseDir + srcForder + (srcForder.endsWith(File.separator) ? "" : File.separator);
        destFolder = baseDir + destFolder + (destFolder.endsWith(File.separator) ? "" : File.separator);
        File srcFile = new File(srcForder);
        if (!srcFile.exists() || !srcFile.isDirectory()) return;

        File[] files = srcFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) continue;
            String fileName = file.getName();
            file.renameTo(new File(destFolder + fileName));
        }
    }

    /**
     * 将指定文件移动到目标目录下
     *
     * @param srcFiles 源文件句柄集合
     * @param dest     目标目录
     */
    public static void mvFile(Set<File> srcFiles, String dest) {
        for (File f : srcFiles) {
            if (!f.exists()) continue;
            logger.info("抽取文件：" + f.getName());
            f.renameTo(new File(dest + f.getName()));
        }
    }

    /**
     * 将set内容逐行写入文件
     *
     * @param writeFile
     * @param content
     */
    public static void writeFile(String writeFile, Set<String> content) {
        File file = new File(writeFile);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        /* 输出数据 */
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8"));
            for (String line : content) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            logger.info(e.getMessage() + " : " + e.getCause());
        }
    }
}
