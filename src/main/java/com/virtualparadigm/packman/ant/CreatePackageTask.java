package com.virtualparadigm.packman.ant;

import java.io.File;

import org.apache.tools.ant.Task;

import com.virtualparadigm.packman.processor.JPackageManager;

public class CreatePackageTask extends Task
{
    private String name; 
    private String version; 
    private String packageFile; 
    private String oldDir;
    private String newDir; 
    private String licenseFile; 
    private String autoInstallDir;
    private String autoUninstallDir;
    private String tempDir;
    private String devMode;
    
    
    public CreatePackageTask()
    {
        super(); 
    }
    
    @Override
    public void execute()
    {
System.out.println("===================================================================================================");
System.out.println("CreatePackageTask");
//System.out.println("---------------------------------------------------------------------------------------------------");
//System.out.println("Required Runtime Jars: paradigm-patch, dom4j.jar, jaxen.jar, commons-io.jar (built with 2.4), ");
//System.out.println("                       commons-codec.jar (built with 1.9).");
System.out.println("===================================================================================================");
System.out.println("Package Name:" + name);
System.out.println("Package Version:" + version);
System.out.println("Package File Name:" + packageFile);
System.out.println("Old State Directory:" + oldDir);
System.out.println("New State Directory:" + newDir);
System.out.println("License File:" + licenseFile);
System.out.println("Autorun Install Directory:" + autoInstallDir);
System.out.println("Autorun Uninstall Directory:" + autoUninstallDir);
System.out.println("Temp Directory:" + tempDir);
System.out.println("Development Mode:" + devMode);
System.out.println("");

        try
        {
            JPackageManager.createPackage(
                name, 
                version, 
                new File(packageFile), 
                new File(oldDir),
                new File(newDir), 
                new File(licenseFile), 
                new File(autoInstallDir), 
                new File(autoUninstallDir), 
                (tempDir == null) ? null : new File(tempDir), 
                (devMode == null || devMode.length() == 0) ? false : Boolean.valueOf(devMode));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
System.out.println("Done.");
    }

    public String getName()
    {
        return name;
    }
    public void setName(String packageName)
    {
        this.name = packageName;
    }

    public String getVersion()
    {
        return version;
    }
    public void setVersion(String packageVersion)
    {
        this.version = packageVersion;
    }

    public String getOldDir()
    {
        return oldDir;
    }
    public void setOldDir(String oldStateDirPath)
    {
        this.oldDir = oldStateDirPath;
    }

    public String getNewDir()
    {
        return newDir;
    }
    public void setNewDir(String newStateDirPath)
    {
        this.newDir = newStateDirPath;
    }

    public String getLicenseFile()
    {
        return licenseFile;
    }
    public void setLicenseFile(String licenseFilePath)
    {
        this.licenseFile = licenseFilePath;
    }

	public String getAutoInstallDir()
	{
		return autoInstallDir;
	}

	public void setAutoInstallDir(String autorunInstallDirPath)
	{
		this.autoInstallDir = autorunInstallDirPath;
	}

	public String getAutoUninstallDir()
	{
		return autoUninstallDir;
	}

	public void setAutoUninstallDir(String autorunUninstallDirPath)
	{
		this.autoUninstallDir = autorunUninstallDirPath;
	}

    public String getPackageFile()
    {
        return packageFile;
    }
    public void setPackageFile(String packageFileName)
    {
        this.packageFile = packageFileName;
    }

	public String getDevMode()
	{
		return devMode;
	}

	public void setDevMode(String devMode)
	{
		this.devMode = devMode;
	}

	public String getTempDir()
	{
		return tempDir;
	}

	public void setTempDir(String tempDir)
	{
		this.tempDir = tempDir;
	}
    
}