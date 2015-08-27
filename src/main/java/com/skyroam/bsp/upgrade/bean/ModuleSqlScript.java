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
public class ModuleSqlScript implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -5611704491569314648L;

    private Long id;

    private String moduleName;

    private String ddlScript;

    private String dmlScript;

    private String version;

    private Date updatedOn;

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

    public String getDdlScript()
    {
        return ddlScript;
    }

    public void setDdlScript(String ddlScript)
    {
        this.ddlScript = ddlScript;
    }

    public String getDmlScript() {
        return dmlScript;
    }

    public void setDmlScript(String dmlScript) {
        this.dmlScript = dmlScript;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }
}
