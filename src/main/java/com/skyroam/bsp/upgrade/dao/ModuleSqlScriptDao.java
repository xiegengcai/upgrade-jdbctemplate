package com.skyroam.bsp.upgrade.dao;

import com.skyroam.bsp.upgrade.bean.ModuleSqlScript;
import jdk.nashorn.internal.runtime.Version;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/13
 * @version 1.0
 */
@Repository
public class ModuleSqlScriptDao extends AbstractDao<ModuleSqlScript> {

	private final RowMapper<ModuleSqlScript> mapper = new RowMapper<ModuleSqlScript>() {
		@Override
		public ModuleSqlScript mapRow(ResultSet rs, int i) throws SQLException {
			ModuleSqlScript t = new ModuleSqlScript();
			t.setId(rs.getLong("id"));
			t.setModuleName(rs.getString("module_name"));
			t.setDdlScript(rs.getString("ddl_script"));
			t.setDmlScript(rs.getString("dml_script"));
			t.setVersion(rs.getString("version"));
			t.setUpdatedOn(rs.getDate("updated_on"));
			return t;
		}
	};

	@Override
	protected String getTableName() {
		return "sys_module_script";
	}

	@Override
	protected RowMapper<ModuleSqlScript> getRowMapper() {
		return mapper;
	}

	@Override
	public void saveOrUpdate(ModuleSqlScript t) {
		StringBuilder sql = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("updatedOn", Calendar.getInstance().getTime());
		paramMap.put("moduleName", t.getModuleName());
		paramMap.put("ddlScript", t.getDdlScript());
		paramMap.put("dmlScript", t.getDmlScript());
		paramMap.put("version", t.getVersion());
		if (t.getId() == null) {
			sql.append("INSERT INTO ").append(getTableName()).append("(module_name,ddl_script, dml_script, version, updated_on) value(:moduleName,:ddlScript, :dmlScript,:version, :updatedOn)");

		} else {
			sql.append("UPDATE ").append(getTableName()).append(" SET module_name=:moduleName, ddl_script=:ddlScript, dml_script=:dmlScript, version=:version, updated_on=:updatedOn WHERE id=:id");
			paramMap.put("id", t.getId());
		}

		if (logger.isDebugEnabled()) {
			logger.debug(sql.toString());
		}
		this.jdbcTemplate.execute(sql.toString(), paramMap, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		});
	}

	public ModuleSqlScript findByNameAndVersion(String moduleName, String version) {
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(getTableName()).append(" WHERE module_name=:moduleName AND version=:version");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("moduleName", moduleName);
		paramMap.put("version", version);
		return findOne(sql.toString(), paramMap);
	}
}
