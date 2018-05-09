# 基于SVN的自动化增量抽取工具

> 简要说明
- 流程分为生成增量描述文件、根据增量描述文件抽取生成增量部署包两个阶段；
- 单一工程单个SVN地址配置文件简化；
- 复合工程多模块对应多个SVN地址支持；
- 通用pom解析jar包抽取和个性化配置文件过滤规则；
- maven依赖的三方包的抽取支持；
- Excel送测清单自动生成；

> TODO列表
- 1、svn的url与jenkins本地路径转换 ---- DONE
    * pom文件直接从svn服务器端获取并解析出jar包名称
- 2、根据pom自动生成增量清单       ---- DONE
- 3、增量包抽取生成                ---- DONE

### 使用说明
生成增量描述文件和增量部署包相关命令如下：

````bash
# 生成增量描述文件和送测清单
java -jar svn-patchtool.jar xml [yml_prefix] [workspace] [versionFrom] [versionTo]

# 读取增量描述文件，抽取生成增量部署包
java -jar svn-patchtool.jar zip [yml_prefix] [workspace] [versionFrom] [versionTo]
````

### 配置说明

配置svn.properties文件：
````properties
######################################################################
##### 以下相对路径均为相对本次工程路径，既${workspace}/[相对路径] ######
######################################################################

######################## 增量抽取多套配置部分 #########################

# svn相关配置信息
svn.username=
svn.password=
# SVN项目的服务器地址
svn.url=

# 增量送测清单存放相对路径（其中@代表项目跟路径）:${workspace}/${patch.excel.dir}
patch.excel.dir=
# 增量描述文件存放路径（其中@代表项目跟路径）:${workspace}/${patch.xml.dir}
patch.xml.dir=
# 增量描述文件模块后缀
patch.xml.surfix=

########################### 以下为通用配置 #############################

# 三方增量包读取相对路径（其中@代表项目跟路径）:${workspace}/${patch.yml.dir}
patch.yml.dir=
# 三方增量包读取后缀
patch.yml.surfix=

# 抽取增量的源（全量目标码路径）:${workspace}/${patch.src.dir}
patch.src.dir=
# 生成的增量包存放路径：${workspace}/${patch.target.dir}
patch.target.dir=
# 增量部署包名称
patch.zip.name=
# 增量包生成压缩包前的外层文件夹名
patch.folder.name=
# 创建临时文件夹，用于中转增量包
patch.tmp.folder.name=
# 增量部署包附带的增量列表名称
deleteFile.name=deleteList.txt
````
confContext.xml配置，以下为一套配置
````xml
    <bean id="svnDao" class="com.dcits.patchtools.svn.dao.SvnDao">
        <property name="svnUrl" value="${svn.url}"/>
        <property name="user" value="${svn.username}"/>
        <property name="pwd" value="${svn.password}"/>
    </bean>
    <bean id="svnService" class="com.dcits.patchtools.svn.service.impl.SvnServiceImpl">
        <property name="svnDao" ref="svnDao"/>
    </bean>
    <bean id="patchService" class="com.dcits.patchtools.svn.service.impl.PatchServiceImpl">
        <property name="svnService" ref="svnService"/>
        <property name="pathRuleService" ref="pathRuleService"/>
        <property name="xmlDir" value="${patch.xml.dir}"/>
        <property name="xmlModuleSurfix" value="${patch.xml.surfix}"/>
        <property name="excelDir" value="${patch.excel.dir}"/>
    </bean>
````

applicationContext.xml配置，当配置多套时需将每一套的PatchService注入到PatchHandler：

````xml
    <property name="patchServices">
        <list>
            <!-- 当有多套svn地址，每一套PatchService的id在此注册 -->
            <ref bean="patchService"/>
        </list>
    </property>
````

### 实现机制

#### 生成增量描述文件

#### 抽取增量生成增量部署包

