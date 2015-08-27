package com.skyroam.bsp.upgrade.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/13
 * @version 1.0
 */
public abstract class AbstractDao<T> {

	@Resource(name = "upgradeJdbcTemplate")
	protected NamedParameterJdbcTemplate jdbcTemplate;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	abstract String getTableName();

	abstract RowMapper<T> getRowMapper();

	abstract void saveOrUpdate(T t);

	/**
	 * 按ID查询
	 *
	 * @param id
	 * @return
	 */
	public T get(Serializable id){
		String sql = "SELECT * FROM "+getTableName()+" WHERE id=?";
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		return jdbcTemplate.getJdbcOperations().queryForObject(sql, new Object[]{id}, getRowMapper());
	}

	protected T findOne(String sql, Map<String, Object> paramMap) {
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		try {
			return jdbcTemplate.queryForObject(sql, paramMap, getRowMapper());
		} catch (EmptyResultDataAccessException e){
			return null;
		}
	}

	protected List<T> find(String sql, Map<String, Object> paramMap) {
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		return jdbcTemplate.query(sql, paramMap, getRowMapper());
	}

	public int delete(Serializable id) {
		String sql = "DELETE FROM "+getTableName()+" WHERE id=?";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("\n%1$s\n", sql));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return jdbcTemplate.execute(sql, map, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		});
	}

	public List<T> findAll(){
		String sql = "SELECT * FROM "+getTableName();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("\n%1$s\n", sql));
		}
		return jdbcTemplate.query(sql, getRowMapper());
	}
}
