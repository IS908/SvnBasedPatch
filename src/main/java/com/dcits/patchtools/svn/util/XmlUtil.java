package com.dcits.patchtools.svn.util;

import com.dcits.patchtools.svn.model.FileBlame;
import com.dcits.patchtools.svn.model.FileModel;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @author Kevin
 * @date 2018-04-26 17:31.
 * @desc
 * @email chenkunh@dcits.com
 */
public class XmlUtil {
    private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

    public static boolean writer(Map<String, List<FileBlame>> map, String path) {

        return false;
    }

    /**
     * 读取增量描述文件，筛选出确认上版本的增量文件列表
     *
     * @return
     */
    public Set<String> getExtractFiles(String xmlFilePath) {
        Set<String> set = new HashSet<>();

        String fileName = xmlFilePath;
        logger.info("增量文件路径：" + fileName);
        Document document = xmlReader(fileName);
        Element rootElement = document.getRootElement();

        Element fileElement;
        for (Iterator file = rootElement.elementIterator("file"); file.hasNext(); ) {
            fileElement = (Element) file.next();
            String packageName = fileElement.attributeValue("module");
            set.add(packageName);
        }
        logger.info(String.valueOf(set.size()));
        return set;
    }

    /**
     * 生成XML增量描述文件
     *
     * @param list FileBlame
     */
    public static void entity2XmlFile(List<FileBlame> list, String path) {
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("files");
        Element descElement = rootElement.addElement("description");
        descElement.addAttribute("desc", "增量描述清单");
        for (FileBlame model : list) {
            Element fileElement = rootElement.addElement("file");
            fileElement.addAttribute("srcPath", model.getSrcPath());
            fileElement.addAttribute("module", model.getModule());
            fileElement.addAttribute("pkgPath", model.getPkgPath());
            fileElement.addAttribute("type", model.getFileType());
            Element commits = fileElement.addElement("commits");
            for (FileModel commit : model.getCommits()) {
                Element commitEle = commits.addElement("commit");
                commitEle.addAttribute("author", commit.getAuthor());
                commitEle.addElement("desc").setText(commit.getDesc());
                commitEle.addElement("timestamp").setText(commit.getTimestamp());
                commitEle.addElement("type").setText(commit.getType());
            }
        }
        xmlWriter(document, true, path);
    }

    /**
     * Xml写入文件
     *
     * @param rootElement 带有信息XML的Document
     * @param format      格式化标志
     */
    private static void xmlWriter(Document rootElement, boolean format, String path) {
        // 输入格式化 XML
        OutputFormat formater = new OutputFormat();
        formater.setIndent(format);
        formater.setNewlines(format);
        formater.setEncoding("utf-8");

        // 生成文件路径及文件名
        String runDate = DateUtil.getRunDate();
        path = path.endsWith(File.separator) ? path : path + File.separator;
        String fileName = path + runDate + "patchList.xml";
        logger.info(fileName);

        // 开始写入到文件
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            XMLWriter xmlWriter = new XMLWriter(fos, formater);
            xmlWriter.write(rootElement);
            xmlWriter.flush();
            xmlWriter.close();
            logger.info("增量清单生成成功！");
        } catch (IOException e) {
            logger.info("增量清单生成失败！");
            e.printStackTrace();
        }

    }

    /**
     * 读取pom文件获得该pom文件对应的打包后的文件名
     *
     * @param pomPath pom文件路径
     * @return 打包后的包名
     */
    public static String pom2PackageName(String pomPath) {
        Document document = xmlReader(pomPath);
        return XmlUtil.pom2PackageName(document);
    }

    /**
     * 读取pom文件获得该pom文件对应的打包后的文件名
     *
     * @param baos pom文件内容
     * @return 打包后的包名
     */
    public static String pom2PackageName(ByteArrayOutputStream baos) {
        String pkgName = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SAXReader reader = new SAXReader();
        reader.setEncoding("utf-8");
        try {
            Document document = reader.read(bais);
            pkgName = pom2PackageName(document);
            baos.close();
        } catch (DocumentException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return pkgName;
    }

    /**
     * pom文件解析
     *
     * @param document
     * @return
     */
    private static String pom2PackageName(Document document) {
        Element root = document.getRootElement();

        Element packaging = root.element("packaging");
        String pkgType = Objects.equals(null, packaging) ? "jar" : packaging.getText();
        Element artifactId = root.element("artifactId");
        Element version = root.element("version");

        if (Objects.equals(null, version) && !Objects.equals(null, root.element("parent"))) {
            Element parent = root.element("parent");
            version = parent.element("version");
        }

        return (artifactId.getText() + "-" + version.getText() + "." + pkgType);
    }

    /**
     * 读取XML文件为Document
     *
     * @param filePath
     * @return
     */
    private static Document xmlReader(String filePath) {
        Document document = null;
        try {
            File file = new File(filePath);
            SAXReader reader = new SAXReader();
            reader.setEncoding("utf-8");
            document = reader.read(file);
        } catch (DocumentException e) {
            logger.error(e.getMessage());
//            e.printStackTrace();
        }
        return document;
    }

}
