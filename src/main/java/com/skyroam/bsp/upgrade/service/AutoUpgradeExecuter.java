package com.skyroam.bsp.upgrade.service;

import com.skyroam.bsp.upgrade.bean.ModuleSqlScript;
import com.skyroam.bsp.upgrade.bean.ModuleVersion;
import com.skyroam.bsp.upgrade.bean.UpgradeScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/14
 * @version 1.0
 */
@Component("autoUpgradeExecuter")
public class AutoUpgradeExecuter implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${dubbo.application.name}")
	private String moduleNmae;
//	@Value("${module.version:1.0.0}")
//	private String version;
	@Value("${module.script.folder:sql}")
	private String scriptFolder;

	@Resource
	private ModuleVersionService moduleVersionService;
	@Resource
	private ModuleSqlScriptService moduleSqlScriptService;
	@Resource
	private ExecuteScriptService executeScriptService;

	private String getModuleNmae(){
		if (StringUtils.isEmpty(moduleNmae)) {
			return moduleNmae;
		}
		return moduleNmae.toLowerCase().replace("-provider", "");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String moduleName = getModuleNmae();
		// 获取版本
		ModuleVersion moduleVersion = moduleVersionService.findByModuleName(moduleName);
		if (moduleVersion == null) {
			moduleVersion = new ModuleVersion("0.0.0", moduleName);
		}
		String oldVersion = moduleVersion.getVersion();
		int currentVersion = Integer.parseInt(oldVersion.replace(".", ""));
		List<UpgradeScript> upgradeScripts = readUgradeFiles(scriptFolder, currentVersion);
		if (upgradeScripts.size() == 0) {
			return;
		}
		// 排序
		Collections.sort(upgradeScripts);
		String lastVersion = moduleVersion.getVersion();
		logger.info("当前{}模块数据库版本为{}", moduleName, lastVersion);
		for(UpgradeScript upgradeScript:upgradeScripts) {
			logger.info("开始从{}升级到{}", lastVersion, upgradeScript.getVersion());
//			boolean success = false;
			ModuleSqlScript moduleSqlScript=new ModuleSqlScript();
			// 执行DDL脚本
			if (upgradeScript.getDdlScript()!=null) {
//				success = executeScriptService.executeScriptFile(upgradeScript.getDdlScript(), false);
				String ddlScript = executeScriptService.executeScriptFile(upgradeScript.getDdlScript(), false, false);
				// 升级失败
				if (ddlScript == null) {
					logger.error("数据库DDL脚本升级失败，请检查{}目录下{}脚本是否有误！", upgradeScript.getVersion(), upgradeScript.getDdlScript().getName());
					throw new RuntimeException(String.format("数据库DDL脚本升级失败" ));
				}
				moduleSqlScript.setDdlScript(ddlScript);
			}
			if (upgradeScript.getDmlScript()!=null) {
				// 升级DML
				String dmlScript  = executeScriptService.executeScriptFile(upgradeScript.getDmlScript(), true, false);
				if (dmlScript == null) {
					logger.error("数据库DML脚本升级失败，请检查{}目录下{}脚本是否有误！", upgradeScript.getVersion(), upgradeScript.getDmlScript().getName());
					throw new RuntimeException(String.format("数据库DML脚本升级失败" ));
				}
				moduleSqlScript.setDmlScript(dmlScript);
			}
			// 更新版本信息
			if (moduleSqlScript.getDdlScript() != null || moduleSqlScript.getDmlScript() != null) {
				moduleSqlScript.setModuleName(moduleName);
				moduleSqlScript.setVersion(upgradeScript.getVersion());
				moduleSqlScriptService.saveOrUpdate(moduleSqlScript);
			}
			logger.info("完成从{}升级到{}", lastVersion, upgradeScript.getVersion());
			lastVersion = upgradeScript.getVersion();
		}
		// 更新完所有脚本，修改当前版本号，以便下次更新
		if (!lastVersion.equals(moduleVersion.getVersion())) {
			moduleVersion.setVersion(lastVersion);
			moduleVersionService.saveOrUpdate(moduleVersion);
		}
		logger.info("数据库已从{}顺利升级至{}", oldVersion, lastVersion);
	}

	private List<UpgradeScript> readUgradeFiles(String path, final int currentVersion) throws IOException {
		logger.info("开始扫描{}目录下的升级脚本文件。", path);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(path);
		File dir = new File(resource.getFile());
		List<UpgradeScript> upgradeScripts = new ArrayList<UpgradeScript>();
		if (dir.exists()) {
			File [] versionDirs = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					int dirVersion = Integer.parseInt(name.replace(".", ""));
					return dirVersion > currentVersion;
				}
			});
			for(File file : versionDirs) {
				logger.debug("扫描{}版本脚本文件", file.getName());
				upgradeScripts.add(buildScript(file));
			}
		}
		logger.info("完成扫描{}目录下的升级脚本文件。", path);
		return upgradeScripts;
	}

	private UpgradeScript buildScript(File directory) {

		File[] files = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".sql");
			}
		});
		if (files != null && files.length > 0) {
			UpgradeScript upgradeScript = new UpgradeScript();
			upgradeScript.setVersion(directory.getName());
			for (File file : files) {
				// ddl脚本
				if (file.getName().toLowerCase().indexOf("-ddl-")!=-1) {
					upgradeScript.setDdlScript(file);
				} else if(file.getName().toLowerCase().indexOf("-dml-")!=-1) {
					upgradeScript.setDmlScript(file);
				}
			}
			return upgradeScript;
		}
		return null;
	}

}
