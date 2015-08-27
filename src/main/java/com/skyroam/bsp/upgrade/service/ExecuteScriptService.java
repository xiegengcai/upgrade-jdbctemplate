package com.skyroam.bsp.upgrade.service;

import com.skyroam.bsp.upgrade.utils.SQLExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/13
 * @version 1.0
 */
@Service
public class ExecuteScriptService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Resource(name = "upgradeJdbcTemplate")
	protected NamedParameterJdbcTemplate jdbcTemplate;

	/**
	 * 执行脚本文件
	 * @param path 文件路径
	 * @param keepFormat 是否保留原始格式及注释
	 * @param isPrint 是否在控制台打印内容
	 */
	public String executeScriptFile(String path, boolean keepFormat,  boolean isPrint) {
		return executeScriptFile(new File(path), keepFormat, isPrint);
	}

	/**
	 * 执行脚本文件
	 * @param file 文件
	 * @param keepFormat 是否保留原始格式及注释
	 * @param isPrint 是否在控制台打印内容
	 */
	@Transactional("upgradeTransactionManager")
	public String executeScriptFile(final File file, final boolean keepFormat, final boolean isPrint) {
		SQLExtractor sqlExtractor = SQLExtractor.getInstance();
		sqlExtractor.setEncoding("UTF-8");
		sqlExtractor.setKeepFormat(keepFormat);
		StringBuilder fileContent = new StringBuilder();
		try {
			List<String> sqlList = sqlExtractor.extract(file, fileContent);
			for (String sql : sqlList) {
				if (isPrint) {
					logger.info(sql);
				}
				jdbcTemplate.getJdbcOperations().execute(sql);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			return null;
		}
		return fileContent.toString();
	}

}
