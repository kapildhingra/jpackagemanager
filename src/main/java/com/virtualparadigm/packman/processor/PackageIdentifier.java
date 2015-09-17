package com.virtualparadigm.packman.processor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PackageIdentifier implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String versionNumber;

	public PackageIdentifier()
	{
		super();
	}
	public PackageIdentifier(String packageName, String versionNumber)
	{
		super();
		this.name = packageName;
		this.versionNumber = versionNumber;
	}
	
	@XmlAttribute
	public String getName()
	{
		return name;
	}
	public void setName(String packageName)
	{
		this.name = packageName;
	}
	
	@XmlAttribute
	public String getVersionNumber()
	{
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber)
	{
		this.versionNumber = versionNumber;
	}
	
	// ===============================================
	// UTILITY METHODS
	// ===============================================
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getName());
		builder.append(this.getVersionNumber());
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
		PackageIdentifier that = (PackageIdentifier)obj;
		EqualsBuilder builder = new EqualsBuilder();
	    builder.append(this.getName(), that.getName());
	    builder.append(this.getVersionNumber(), that.getVersionNumber());
		return builder.isEquals();
	}	
}