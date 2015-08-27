package com.skyroam.bsp.upgrade.dao;

import com.skyroam.bsp.upgrade.bean.ModuleVersion;
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
public class ModuleVersionDao extends AbstractDao<ModuleVersion> {

	private final RowMapper<ModuleVersion> rowMapper = new RowMapper<ModuleVersion>() {
		@Override
		public ModuleVersion mapRow(ResultSet rs, int i) throws SQLException {
			ModuleVersion t = new ModuleVersion();
			t.setId(rs.getLong("id"));
			t.setModuleName(rs.getString("module_name"));
			t.setUpdatedOn(rs.getTimestamp("updated_on"));
			t.setVersion(rs.getString("version"));
			return t;
		}
	};

	@Override
	public String getTableName() {
		return "sys_module_version";
	}

	@Override
	public RowMapper<ModuleVersion> getRowMapper() {
		return rowMapper;
	}

	public ModuleVersion findByModuleName(String moduleName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("moduleName", moduleName);
		return findOne("SELECT * FROM "+getTableName()+" WHERE module_name=:moduleName", paramMap);
	}

	@Override
	public void saveOrUpdate(ModuleVersion t) {
		StringBuilder sql = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("moduleName", t.getModuleName());
		paramMap.put("version", t.getVersion());
		paramMap.put("updatedOn", Calendar.getInstance().getTime());
		if (t.getId() == null) {
			sql.append("INSERT INTO ").append(getTableName()).append("(module_name,version,updated_on) VALUE(:moduleName,:version,:updatedOn)");
		} else {
			sql.append("UPDATE ").append(getTableName()).append(" SET module_name=:moduleName, version=:version, updated_on=:updatedOn WHERE id=:id");
			paramMap.put("id", t.getId());
		}
		if (logger.isDebugEnabled()) {
			logger.debug(sql.toString());
		}
		this.jdbcTemplate.execute(sql.toString(), paramMap, new PreparedStatementCallback<Integer>()
		{
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
			{
				return ps.executeUpdate();
			}
		});
	}
}
