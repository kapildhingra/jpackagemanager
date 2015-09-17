package com.virtualparadigm.packman.ant;

import java.io.File;

import org.apache.tools.ant.Task;

import com.virtualparadigm.packman.processor.JPackageManager;

public class InstallPackageTask extends Task
{
    private String packageFile;
    private String targetDir;
    private String dataDir;
    private String config;
    private String devMode;
    
    public InstallPackageTask()
    {
        super(); 
    }
    
    @Override
    public void execute()
    {
System.out.println("===================================================================================================");
System.out.println("InstallPackageTask");
//System.out.println("---------------------------------------------------------------------------------------------------");
//System.out.println("Required Runtime Jars: paradigm-patch, dom4j.jar, jaxen.jar, commons-io.jar (built with 2.4), ");
//System.out.println("                       commons-codec.jar (built with 1.9).");
System.out.println("===================================================================================================");
System.out.println("Package File:" + packageFile);
System.out.println("Target Directory:" + targetDir);
System.out.println("Package Manager Data Directory:" + dataDir);
System.out.println("Configuration File:" + config);
System.out.println("Development Mode:" + devMode);
System.out.println("");

        try
        {
            JPackageManager.installPackage(
                new File(packageFile), 
                new File(targetDir), 
                new File(dataDir), 
                new File(config), 
                (devMode == null || devMode.length() == 0) ? false : Boolean.valueOf(devMode));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
System.out.println("Done.");
    }

    public String getPackageFile()
    {
        return packageFile;
    }
    public void setPackageFile(String packageFilePath)
    {
        this.packageFile = packageFilePath;
    }

    public String getTargetDir()
    {
        return targetDir;
    }
    public void setTargetDir(String targetRootDirPath)
    {
        this.targetDir = targetRootDirPath;
    }

    public String getDataDir()
    {
        return dataDir;
    }
    public void setDataDir(String packageManagerRootDir)
    {
        this.dataDir = packageManagerRootDir;
    }

    public String getConfig()
    {
        return config;
    }
    public void setConfig(String localConfigurationFile)
    {
        this.config = localConfigurationFile;
    }

	public String getDevMode()
	{
		return devMode;
	}
	public void setDevMode(String devMode)
	{
		this.devMode = devMode;
	}

}