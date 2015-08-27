package com.skyroam.bsp.upgrade.service;

import com.skyroam.bsp.upgrade.bean.ModuleVersion;
import com.skyroam.bsp.upgrade.dao.ModuleVersionDao;
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
public class ModuleVersionService {

	@Resource
	private ModuleVersionDao moduleVersionDao;

	@Transactional("upgradeTransactionManager")
	public void saveOrUpdate(ModuleVersion t) {
		moduleVersionDao.saveOrUpdate(t);
	}

	public ModuleVersion get(Serializable id) {
		return moduleVersionDao.get(id);
	}

	@Transactional("upgradeTransactionManager")
	public int delete(Serializable id) {
		return moduleVersionDao.delete(id);
	}

	public List<ModuleVersion> findAll(){
		return moduleVersionDao.findAll();
	}

	public ModuleVersion findByModuleName(String moduleName){
		return moduleVersionDao.findByModuleName(moduleName);
	}
}
