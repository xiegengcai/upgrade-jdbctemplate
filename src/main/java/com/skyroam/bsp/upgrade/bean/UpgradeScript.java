package com.skyroam.bsp.upgrade.bean;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 *
 * @author <a href="mailto:gengcai.xie@skyroam.com">Xie Gengcai</a>
 *         2015/8/17
 * @version 1.0
 */
public class UpgradeScript implements Comparable<UpgradeScript> {
	private String version;
	private File ddlScript;
	private File dmlScript;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public File getDdlScript() {
		return ddlScript;
	}

	public void setDdlScript(File ddlScript) {
		this.ddlScript = ddlScript;
	}

	public File getDmlScript() {
		return dmlScript;
	}

	public void setDmlScript(File dmlScript) {
		this.dmlScript = dmlScript;
	}

	@Override
	public int compareTo(UpgradeScript o) {
		if (this.getVersion().equals(o.getVersion())) {
			return 0;
		} else {
			return Integer.parseInt(this.getVersion().replace(".", "")) - Integer.parseInt(o.getVersion().replace(".", ""));
		}
	}
}
