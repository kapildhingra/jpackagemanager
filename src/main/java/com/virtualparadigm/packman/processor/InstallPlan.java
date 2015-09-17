package com.virtualparadigm.packman.processor;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InstallPlan implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<InstallItem> installItem;
    
    
    public InstallPlan()
	{
		super();
	}
    public InstallPlan(List<InstallItem> installItem)
	{
		super();
		this.installItem = installItem;
	}
    


//    private String dataDir;
//    private String localConfigFile;
//    private Boolean developmentMode;
//    private InstallItems installItems;
//	public InstallPlan()
//	{
//		super();
//	}
//	public InstallPlan(String dataDir, String localConfigFile, Boolean developmentMode, InstallItems installItems)
//	{
//		super();
//		this.dataDir = dataDir;
//		this.localConfigFile = localConfigFile;
//		this.developmentMode = developmentMode;
//		this.installItems = installItems;
//	}
//	public String getDataDir()
//	{
//		return dataDir;
//	}
//	public void setDataDir(String dataDir)
//	{
//		this.dataDir = dataDir;
//	}
//	public String getLocalConfigFile()
//	{
//		return localConfigFile;
//	}
//	public void setLocalConfigFile(String localConfigFile)
//	{
//		this.localConfigFile = localConfigFile;
//	}
//	public Boolean getDevelopmentMode()
//	{
//		return developmentMode;
//	}
//	public void setDevelopmentMode(Boolean developmentMode)
//	{
//		this.developmentMode = developmentMode;
//	}
//	
//	public InstallItems getInstallItems()
//	{
//		return installItems;
//	}
//	public void setInstallItems(InstallItems installItems)
//	{
//		this.installItems = installItems;
//	}
	
	public List<InstallItem> getInstallItem()
	{
		return installItem;
	}
	public void setInstallItem(List<InstallItem> installItem)
	{
		this.installItem = installItem;
	}
	
	public static void main(String[] args)
	{
        try
        {
        	InstallPlan installPlan = new InstallPlan();
//        	installPlan.setDataDir("c:/temp/data");
//        	installPlan.setLocalConfigFile("c:/temp/install.properties");
//        	installPlan.setDevelopmentMode(false);
        	
        	List<InstallItem> installItems = new ArrayList<InstallItem>();
        	installItems.add(new InstallItem("c:/temp/p1.zip", "c:/temp/dest"));
        	installItems.add(new InstallItem("c:/temp/p23.zip", "c:/temp/dest"));
        	installPlan.setInstallItem(installItems);
        	
        	
            JAXBContext jaxbContext = JAXBContext.newInstance(InstallPlan.class);
            
            
            Marshaller marshaller = jaxbContext.createMarshaller();

            // removes the xml header:
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(
                    new JAXBElement<InstallPlan>(
                            new QName(
                                null, 
                                "installPlan"), 
                                InstallPlan.class, 
                                installPlan), 
                    stringWriter);
            
            
            System.out.print(stringWriter.toString());
            
            
        }
        catch(JAXBException jbe)
        {
            jbe.printStackTrace();
        }
		
		
		
	}    
//    public static boolean installPackage(File packageFile, File targetRootDir, File packageManagerDataDir, File localConfigurationFile, boolean developmentMode)

}