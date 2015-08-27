package com.skyroam.bsp.upgrade.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/13
 * @version 1.0
 */
public class ModuleVersion implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -4463579978932393720L;

    private Long id;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 升级时间
     */
    private Date updatedOn;

    public ModuleVersion() {
    }

    public ModuleVersion(String version, String moduleName) {
        this.version = version;
        this.moduleName = moduleName;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public Date getUpdatedOn()
    {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn)
    {
        this.updatedOn = updatedOn;
    }
}
