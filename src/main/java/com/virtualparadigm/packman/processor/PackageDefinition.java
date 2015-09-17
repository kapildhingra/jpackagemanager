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
public class PackageDefinition implements Serializable
{
    private static final long serialVersionUID = 1L;
    private PackageIdentifier packageIdentifier;
    private PackageDependencies dependencies;
    
	public PackageDefinition()
	{
		super();
	}
	public PackageIdentifier getPackageIdentifier()
	{
		return packageIdentifier;
	}
	public void setPackageIdentifier(PackageIdentifier packageIdentifier)
	{
		this.packageIdentifier = packageIdentifier;
	}
	
	public String getPackageNameString()
	{
		if(this.packageIdentifier != null)
		{
			return this.packageIdentifier.getName();
		}
		return "";
	}
	public VersionNumber getPackageVersionNumber()
	{
		if(this.packageIdentifier != null && this.packageIdentifier.getVersionNumber() != null)
		{
			return new VersionNumber(this.packageIdentifier.getVersionNumber());
		}
		return null;
	}

	public PackageDependencies getDependencies()
	{
		return dependencies;
	}
	public void setDependencies(PackageDependencies dependencies)
	{
		this.dependencies = dependencies;
	}
	
	
	public static void main(String[] args)
	{
        try
        {
        	PackageDefinition packageDef = new PackageDefinition();
        	packageDef.setPackageIdentifier(new PackageIdentifier("foo.bar", "1.2.3"));
        	
        	List<PackageIdentifier> dependencies = new ArrayList<PackageIdentifier>();
        	dependencies.add(new PackageIdentifier("prereq1", "1.2.3"));
        	dependencies.add(new PackageIdentifier("prereq2", "2.2.3"));

        	packageDef.setDependencies(new PackageDependencies(dependencies));
        	
//        	PackageIdentifier[] dependencies = new PackageIdentifier[2];
//        	dependencies[0] = new PackageIdentifier("prereq1", "1.2.3");
//        	dependencies[1] = new PackageIdentifier("prereq2", "2.2.3");
        	
            JAXBContext jaxbContext = JAXBContext.newInstance(PackageDefinition.class);
            
            
            Marshaller marshaller = jaxbContext.createMarshaller();

            // removes the xml header:
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(
                    new JAXBElement<PackageDefinition>(
                            new QName(
                                null, 
                                "packageDefinition"), 
                            PackageDefinition.class, 
                            packageDef), 
                    stringWriter);
            
            
            System.out.print(stringWriter.toString());
            
            
        }
        catch(JAXBException jbe)
        {
            jbe.printStackTrace();
        }
		
		
		
	}
}