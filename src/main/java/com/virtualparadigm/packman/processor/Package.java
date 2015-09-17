package com.virtualparadigm.packman.processor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Package implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String version;
    private long installTimestamp;
    private String rootDirectory;
    

    public Package()
    {
        super();
    }

    public Package(String name, String version, String rootDirectory, long installTimestamp)
    {
        super();
        this.setName(name);
        this.setVersion(version);
        this.setRootDirectory(rootDirectory);
        this.setInstallTimestamp(installTimestamp);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public VersionNumber getVersionNumber()
    {
        return new VersionNumber(this.version);
    }
    public void setVersionNumber(VersionNumber versionNumber)
    {
        this.setVersion(versionNumber.toString());
    }

    public long getInstallTimestamp()
    {
        return installTimestamp;
    }

    public void setInstallTimestamp(long installTimestamp)
    {
        this.installTimestamp = installTimestamp;
    }

    public String getRootDirectory()
    {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory)
    {
        this.rootDirectory = rootDirectory;
    }
    
}

