package com.virtualparadigm.packman.processor;

import java.io.Serializable;
import java.util.List;

public class InstallItems implements Serializable
{
	private static final long serialVersionUID = 1L;
	private List<InstallItem> installItem;
	public InstallItems()
	{
		super();
	}
	public InstallItems(List<InstallItem> installItem)
	{
		super();
		this.installItem = installItem;
	}
	public List<InstallItem> getInstallItem()
	{
		return installItem;
	}
	public void setInstallItem(List<InstallItem> installItem)
	{
		this.installItem = installItem;
	}
	
}