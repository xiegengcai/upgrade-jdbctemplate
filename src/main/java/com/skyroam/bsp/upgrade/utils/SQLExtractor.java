package com.skyroam.bsp.upgrade.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * SQL脚本解析帮助类
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/20
 * @version 1.0
 */
public final class SQLExtractor {

	private static class Holder {
		private static SQLExtractor INSTANCE = new SQLExtractor();
	}

	private SQLExtractor() {

	}

	public static SQLExtractor getInstance() {
		return Holder.INSTANCE;
	}
	/**
	 * 读取文件编码方式，缺省：UTF-8
	 */
	private String encoding = "UTF-8";

	/**
	 * 是否保持原始格式
	 */
	private boolean keepFormat = false;

	/**
	 * delimiters must match in case and whitespace is significant.
	 */
	private boolean strictDelimiterMatching = true;

	/**
	 * SQL语句分隔符
	 */
	private String delimiter = ";";

	/**
	 * 是否保留语句分隔符
	 */
	private boolean keepDelimiter = true;

	/**
	 * The delimiter type indicating whether the delimiter will only be recognized on a line by itself
	 */
	private String delimiterType = DelimiterType.NORMAL;

	/**
	 * delimiters we support, "normal" and "row"
	 */
	public static class DelimiterType  {
		/** The enumerated strings */
		public static final String NORMAL = "normal", ROW = "row";

		/** @return the enumerated strings */
		public String[] getValues() {
			return new String[] { NORMAL, ROW };
		}
	}

	/**
	 * 解析脚本文件
	 * @param file
	 * @param fileContent 文件原始内容
	 * @return sql语句列表
	 * @throws IOException
	 */
	public List<String> extract(File file, StringBuilder fileContent) throws IOException {
		FileInputStream inputStream = null;
		List<String> sqlList = new ArrayList<String>();
		Scanner sc = null;
		StringBuilder sql = new StringBuilder();
		try {
			inputStream = new FileInputStream(file);
			sc = new Scanner(inputStream, encoding);
			while (sc.hasNextLine()) {

				String line = sc.nextLine();
				fileContent.append(line);
				if (!keepFormat) {
					line = line.trim();
				}
				if (!keepFormat) {
					// 去掉注释
					if (line.startsWith("//")) {
						continue;
					}
					// 去掉注释
					if (line.startsWith("--")) {
						continue;
					}
					StringTokenizer st = new StringTokenizer(line);
					if (st.hasMoreTokens()) {
						String token = st.nextToken();
						if ("REM".equalsIgnoreCase(token)) {
							continue;
						}
					}
				}

				sql.append(keepFormat ? "\n" : " ").append(line);

				// SQL defines "--" as a comment to EOL
				// and in Oracle it may contain a hint
				// so we cannot just remove it, instead we must end it
				if (!keepFormat && line.indexOf("--") >= 0) {
					sql.append("\n");
				}

				int lastDelimPos = lastDelimiterPosition(sql, line);
				if (lastDelimPos > -1) {
					String sqlStr = keepDelimiter ? sql.toString() : eliminateLastColon(sql.toString());
					sqlList.add(sqlStr);
					sql.replace(0, sql.length(), "");
				}
			}
			// note that Scanner suppresses exceptions
			if (sc.ioException() != null) {
				throw sc.ioException();
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}
		return sqlList;
	}

	private String eliminateLastColon(String sql) {
		sql = sql.trim();
		if (StringUtils.endsWith(sql, ";")) {
			return sql.substring(0, sql.length() - 1);
		}
		return sql;
	}

	private int lastDelimiterPosition(StringBuilder sb, String currentLine) {
		if (strictDelimiterMatching) {
			if ((delimiterType.equals(DelimiterType.NORMAL) && StringUtils.endsWith(sb, delimiter))
					|| (delimiterType.equals(DelimiterType.ROW) && currentLine.equals(delimiter))) {
				return sb.length() - delimiter.length();
			}
			// no match
			return -1;
		} else {
			String d = delimiter.trim().toLowerCase(Locale.ENGLISH);
			if (delimiterType.equals(DelimiterType.NORMAL)) {
				// still trying to avoid wasteful copying, see
				// StringUtils.endsWith
				int endIndex = delimiter.length() - 1;
				int bufferIndex = sb.length() - 1;
				while (bufferIndex >= 0 && Character.isWhitespace(sb.charAt(bufferIndex))) {
					--bufferIndex;
				}
				if (bufferIndex < endIndex) {
					return -1;
				}
				while (endIndex >= 0) {
					if (sb.substring(bufferIndex, bufferIndex + 1).toLowerCase(Locale.ENGLISH).charAt(0) != d.charAt(endIndex)) {
						return -1;
					}
					bufferIndex--;
					endIndex--;
				}
				return bufferIndex + 1;
			} else {
				return currentLine.trim().toLowerCase(Locale.ENGLISH).equals(d) ? sb.length() - currentLine.length() : -1;
			}
		}
	}

	// --------------------------------------------------------------------------------
	//
	// getter and setter
	//
	// --------------------------------------------------------------------------------


	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setDelimiterType(String delimiterType) {
		this.delimiterType = delimiterType;
	}

	public void setKeepFormat(boolean keepFormat) {
		this.keepFormat = keepFormat;
	}

	public void setStrictDelimiterMatching(boolean strictDelimiterMatching) {
		this.strictDelimiterMatching = strictDelimiterMatching;
	}

	public void setKeepDelimiter(boolean keepDelimiter) {
		this.keepDelimiter = keepDelimiter;
	}
}
