# 使用说明 #

----------

1. pom.xml增加依赖
		
		<dependency>
			<groupId>com.skyroam</groupId>
			<artifactId>bsp-upgrade</artifactId>
			<version>${project.release.version}</version>
		</dependency>
2. 整合Spring配置
	    
		<!-- 需要一个数据源 -->
		<alias name="baseDataSource" alias="upgradeDataSource"/>
		<!-- 导入升级配置 -->
    	<import resource="classpath:auto-upgrade.xml"/>
3. 确保整合模块数据库中有升级表，建表参考bsp-upgrade模块classes/sql/1.0.0
4. 确保upgrade组件优先创建，即以下导入需要在本模块扫描包前面

        <!-- 导入升级配置 -->
    	<import resource="classpath:auto-upgrade.xml"/>
5. 配置文件需要两个配置项
    
    	## 模块名称，保存至数据库时会去掉-provider ##
    	dubbo.application.name=bsp-base-provider
		## 数据库升级脚本目录，缺省classes目录下的sql目录 ##
    	module.script.folder=sql
    
6. SQL脚本目录下面按版本号(版本号举例1.0.0)分子目录，版本子目录下包含两个sql文件（ddl和dml，ddl先执行完成再执行dml），如下所示：	
   - sql
     * 1.0.0
         * 1-ddl-xxx.sql
         * 2-dml-xxx.sql
     * 1.0.1
         * 1-ddl-xxx.sql
         * 2-dml-xxx.sql

7. ddl无事务，ddl脚本文件名必须包含-ddl-、dml脚本文件名必须包含-dml-
8. 程序启动自动扫描module.script.folder配置目录下版本号大于当前数据库版本号的子目录，并按先后顺序执行升级
