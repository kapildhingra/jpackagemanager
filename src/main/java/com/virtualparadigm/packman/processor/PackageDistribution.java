package com.virtualparadigm.packman.processor;

public class PackageDistribution
{
//    private File packageFile;
	private PackageDefinition packageDefinition;
    private byte[] license;
//    private File patchFile;
//    private File patchFileSetDir;
//    private File targetDir;

    public PackageDistribution()
    {
        super();
    }

    public PackageDefinition getPackageDefinition()
	{
		return packageDefinition;
	}
	public void setPackageDefinition(PackageDefinition packageDefinition)
	{
		this.packageDefinition = packageDefinition;
	}

	public String getPackageNameString()
    {
		if(this.packageDefinition != null)
		{
			return this.packageDefinition.getPackageNameString();
		}
        return "";
    }
    public VersionNumber getPackageVersionNumber()
    {
		if(this.packageDefinition != null)
		{
			return this.packageDefinition.getPackageVersionNumber();
		}
        return null;
    }

    public byte[] getLicense()
    {
        return license;
    }
    public void setLicense(byte[] license)
    {
        this.license = license;
    }

    
}