package com.virtualparadigm.packman.processor;

import java.io.Serializable;
import java.util.List;

public class PackageDependencies implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<PackageIdentifier> packageIdentifier;
    
	public PackageDependencies()
	{
		super();
	}
	public PackageDependencies(List<PackageIdentifier> packageIdentifier)
	{
		super();
		this.packageIdentifier = packageIdentifier;
	}
	public List<PackageIdentifier> getPackageIdentifier()
	{
		return packageIdentifier;
	}
	public void setPackageIdentifier(List<PackageIdentifier> packageIdentifier)
	{
		this.packageIdentifier = packageIdentifier;
	}
    
}