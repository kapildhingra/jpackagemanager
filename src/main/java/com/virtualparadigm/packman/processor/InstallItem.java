package com.virtualparadigm.packman.processor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class InstallItem implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String packageFile;
	private String targetDir;
	public InstallItem()
	{
		super();
	}
	public InstallItem(String packageFile, String targetDir)
	{
		super();
		this.packageFile = packageFile;
		this.targetDir = targetDir;
	}
	
	@XmlAttribute
	public String getPackageFile()
	{
		return packageFile;
	}
	public void setPackageFile(String packageFile)
	{
		this.packageFile = packageFile;
	}
	
	@XmlAttribute
	public String getTargetDir()
	{
		return targetDir;
	}
	public void setTargetDir(String targetDir)
	{
		this.targetDir = targetDir;
	}
	
	// ===============================================
	// UTILITY METHODS
	// ===============================================
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getPackageFile());
		builder.append(this.getTargetDir());
		return builder.toHashCode();		
	}
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (obj == this)
		{
			return true;
		}
		if (obj.getClass() != getClass())
		{
			return false;
		}
		InstallItem that = (InstallItem)obj;
		EqualsBuilder builder = new EqualsBuilder();
	    builder.append(this.getPackageFile(), that.getPackageFile());
	    builder.append(this.getTargetDir(), that.getTargetDir());
		return builder.isEquals();
	}	
	
}