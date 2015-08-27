package com.skyroam.bsp.upgrade.service;

import com.skyroam.bsp.upgrade.bean.ModuleSqlScript;
import com.skyroam.bsp.upgrade.dao.ModuleSqlScriptDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/13
 * @version 1.0
 */
@Service
public class ModuleSqlScriptService {

	@Resource
	private ModuleSqlScriptDao moduleSqlScriptDao;

	@Transactional("upgradeTransactionManager")
	public void saveOrUpdate(ModuleSqlScript t) {
		if (t.getDdlScript() == null && t.getDmlScript() == null) {
			return;
		}
		ModuleSqlScript old = moduleSqlScriptDao.findByNameAndVersion(t.getModuleName(), t.getVersion());
		if (old != null) {
			t.setId(old.getId());
		}
		moduleSqlScriptDao.saveOrUpdate(t);
	}


	public ModuleSqlScript get(Serializable id) {
		return moduleSqlScriptDao.get(id);
	}

	@Transactional("upgradeTransactionManager")
	public int delete(Serializable id) {
		return moduleSqlScriptDao.delete(id);
	}

	public List<ModuleSqlScript> findAll(){
		return moduleSqlScriptDao.findAll();
	}

}
