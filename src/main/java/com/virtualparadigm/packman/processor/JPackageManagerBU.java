package com.virtualparadigm.packman.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.virtualparadigm.packman.util.ZipUtils;
import com.virtualparadigm.patch.processor.JPatchManager;

public class JPackageManagerBU
{
	private static Logger logger = LoggerFactory.getLogger(JPackageManagerBU.class);
	
    //PACKAGE != PATCH
    // packages do contain patch along with other stuff
    // packages are created by doing diffs between different (prestine) installed versions
    // packages contain variables that should be replaced during installation
	
    private static final String PACKAGE_MANAGER_DATA_DIR_NAME = "data";
    private static final String PACKAGE_FILE_NAME = "packages.xml";
    private static final String PACKAGE_PROPERTIES_FILE_NAME = "package.properties";
    private static final String TEMP_INSTALL_DIR_NAME = "temp";
    private static final String TEMP_CREATE_PACKAGE_DIR_NAME = "temp";
    private static final String METADATA_DIR_NAME = "metadata";
    private static final String PATCH_DIR_NAME = "patch";
    private static final String PATCH_FILE_NAME = "patch.xml";
    private static final String PATCH_FILES_DIR_NAME = "patch-files";
    private static final String AUTORUN_DIR_NAME = "autorun";
    private static final String INSTALL_DIR_NAME = "install";
    private static final String UNINSTALL_DIR_NAME = "uninstall";
    private static final String ARCHIVE_DIR_NAME = "archive";
    
    private static final String PACKAGE_NAME_KEY = "package.name";
    private static final String PACKAGE_VERSION_KEY = "package.version";
    
    private static final String CURRENT_TEMPLATE_NAME = "template";
    
    private static final SuffixFileFilter TEMPLATE_SUFFIX_FILE_FILTER = new SuffixFileFilter(new String[]{"xml", "properties", "sh", "bat"});
    
    
    private static JAXBContext jaxbContext;
    private static Map<String, Package> installPackageMap;

    static
    {
        try
        {
            jaxbContext = JAXBContext.newInstance(Package.class);
        }
        catch(JAXBException jbe)
        {
        	logger.error("", jbe);
//            jbe.printStackTrace();
        }
        
        installPackageMap = new HashMap<String, Package>();
        Collection<Package> installPackages = JPackageManagerBU.unmarshallFromFile(JPackageManagerBU.PACKAGE_MANAGER_DATA_DIR_NAME + "/" + PACKAGE_FILE_NAME);
        if(installPackages != null)
        {
            for(Package installPackage : installPackages)
            {
                installPackageMap.put(installPackage.getName(), installPackage);
            }
        }
    }
    
    // ======================================================================
    // PACKAGE LISTING METHODS
    // ======================================================================
    public static Collection<Package> listPackages()
    {
    	return JPackageManagerBU.findInstalledPackages();
    }
    
    public static VersionNumber getPackageVersion(String packageName)
    {
    	return JPackageManagerBU.getInstalledVersionNumber(packageName);
    }
    
    // ======================================================================
    // PACKAGE CREATION METHODS
    // ======================================================================
    public static boolean createPackage(String packageName, String packageVersion, File packageOutputFile, File oldStateDir, File newStateDir, File licenseFile, File autorunInstallDir, File autorunUninstallDir, boolean developmentMode)
    {
    	logger.info("PackageManager::createPackage - creating package: " + packageName + " version: " + packageVersion);
        boolean status = false;
        if(packageName != null && packageVersion != null)
        {
            File tempDir = new File(TEMP_CREATE_PACKAGE_DIR_NAME);
            File tempMetadataDir = new File(JPackageManagerBU.TEMP_CREATE_PACKAGE_DIR_NAME + "/" + JPackageManagerBU.METADATA_DIR_NAME);
            File tempPatchDir = new File(JPackageManagerBU.TEMP_CREATE_PACKAGE_DIR_NAME + "/" + JPackageManagerBU.PATCH_DIR_NAME);
            File tempPatchFilesDir = new File(JPackageManagerBU.TEMP_CREATE_PACKAGE_DIR_NAME + "/" + JPackageManagerBU.PATCH_DIR_NAME + "/" + JPackageManagerBU.PATCH_FILES_DIR_NAME);

            File tempAutorunDir = new File(JPackageManagerBU.TEMP_CREATE_PACKAGE_DIR_NAME + "/" + JPackageManagerBU.AUTORUN_DIR_NAME);
            File tempAutorunInstallDir = new File(JPackageManagerBU.TEMP_CREATE_PACKAGE_DIR_NAME + "/" + JPackageManagerBU.AUTORUN_DIR_NAME + "/" + JPackageManagerBU.INSTALL_DIR_NAME);
            File tempAutorunUninstallDir = new File(JPackageManagerBU.TEMP_CREATE_PACKAGE_DIR_NAME + "/" + JPackageManagerBU.AUTORUN_DIR_NAME + "/" + JPackageManagerBU.UNINSTALL_DIR_NAME);
            
            tempMetadataDir.mkdirs();
            tempPatchFilesDir.mkdirs();
            tempAutorunInstallDir.mkdirs();
            tempAutorunUninstallDir.mkdirs();
            
            try
            {
                FileUtils.copyFileToDirectory(licenseFile, tempMetadataDir, true);
                
                FileUtils.copyDirectory(autorunInstallDir, tempAutorunInstallDir, true);
                FileUtils.copyDirectory(autorunUninstallDir, tempAutorunUninstallDir, true);
                
                String strPackageProperties = JPackageManagerBU.PACKAGE_NAME_KEY + "=" + packageName + "\n" + JPackageManagerBU.PACKAGE_VERSION_KEY + "=" + packageVersion;
                FileUtils.writeStringToFile(new File(tempMetadataDir.getAbsolutePath() + "/" + JPackageManagerBU.PACKAGE_PROPERTIES_FILE_NAME), strPackageProperties, "UTF-8");
                
                JPatchManager directoryPatchManager = new JPatchManager();
//                directoryPatchManager.makePatch(newStateDir, oldStateDir, new File(tempPatchDir.getAbsolutePath() + "/" + JPackageManagerBU.PATCH_FILE_NAME), tempPatchFilesDir);
                directoryPatchManager.makePatch(oldStateDir, newStateDir, new File(tempPatchDir.getAbsolutePath()), null);

                if(packageOutputFile == null)
                {
                    packageOutputFile = new File(packageName + "-" + packageVersion + ".zip");
                }
                ZipUtils.createZipFile(packageOutputFile.getAbsolutePath(), new File[]{tempAutorunDir, tempMetadataDir, tempPatchDir}, 1024);
                
                status = true;
            }
            catch(Exception e)
            {
            	logger.error("", e);
//                e.printStackTrace();
            }
            
            if(!developmentMode)
            {
                JPackageManagerBU.cleanup(tempDir);
            }
            
        }
        return status;
    }
    
    
    
    // ======================================================================
    // PACKAGE INSTALL METHODS
    // ======================================================================
    public static boolean installPackage(File packageFile, File targetRootDir, File packageManagerDataDir, File localConfigurationFile, boolean developmentMode)
    {
    	logger.info("PackageManager::installPackage - installing package: " + packageFile.getAbsolutePath());        
        boolean status = false;
        if(packageFile != null)
        {
            File tempDir = JPackageManagerBU.prepare(packageFile, packageManagerDataDir);

            Properties packageProperties = JPackageManagerBU.validate(tempDir);
            
            if(packageProperties != null)
            {
                Configuration configuration = null;
                try
                {
                    configuration = new PropertiesConfiguration(localConfigurationFile);
                }
                catch(ConfigurationException ce)
                {
                    ce.printStackTrace();
                }
               
                JPackageManagerBU.configure(tempDir, configuration);
                
                JPackageManagerBU.deploy(targetRootDir, tempDir);
                
                JPackageManagerBU.autorun(new File(tempDir.getAbsolutePath() + "/" + AUTORUN_DIR_NAME + "/" + INSTALL_DIR_NAME));
                
                JPackageManagerBU.finalize(
                    packageProperties.getProperty(PACKAGE_NAME_KEY), 
                    packageProperties.getProperty(PACKAGE_VERSION_KEY), 
                    packageManagerDataDir, 
                    targetRootDir);
                
                status = true;
            }
            
            if(!developmentMode)
            {
                JPackageManagerBU.cleanup(tempDir);
            }
            
        }
        return status;
    }
    
    
    
    // ======================================================================
    // LIFECYCLE METHODS
    // ======================================================================
    public static File prepare(File packageFile, File packageManagerDataDir)
    {
    	logger.info("PackageManager::prepare()");
        File tempDir = new File(packageManagerDataDir.getAbsolutePath() + "/" + TEMP_INSTALL_DIR_NAME);
        // File tempDir = new File(TEMP_DIR_NAME);
        ZipUtils.unzipArchive(packageFile, tempDir);
        return tempDir;
    }
    
    public static Properties validate(File tempDir)
    {
    	logger.info("PackageManager::validate()");

        // INVALID (return empty properties) IF:
        //   package.properties not found
        //   package properties does not contain name and version
        //   found installed version greater than package to be installed

        Properties packageProperties = new Properties();
        InputStream packagePropInputStream = null;
        try
        {
            packagePropInputStream = new FileInputStream(new File(tempDir.getAbsolutePath() + "/" + JPackageManagerBU.METADATA_DIR_NAME + "/" +  JPackageManagerBU.PACKAGE_PROPERTIES_FILE_NAME));
            packageProperties.load(packagePropInputStream);
            logger.info("  loaded package properties.");
        }
        catch (IOException ioe)
        {
        	logger.error("", ioe);
//            ioe.printStackTrace();
        }
        finally
        {
            if (packagePropInputStream != null)
            {
                try
                {
                    packagePropInputStream.close();
                }
                catch (Exception e)
                {
                	logger.error("", e);
//                    e.printStackTrace();
                }
            }
        }

        if(packageProperties.containsKey(JPackageManagerBU.PACKAGE_NAME_KEY) && packageProperties.containsKey(JPackageManagerBU.PACKAGE_VERSION_KEY))
        {
            VersionNumber installedVersionNumber = null;
            Package installPackage = JPackageManagerBU.findInstalledPackage(packageProperties.getProperty(PACKAGE_NAME_KEY));
            if (installPackage == null)
            {
                installedVersionNumber = new VersionNumber("0");
            }
            else
            {
                installedVersionNumber = installPackage.getVersionNumber();
            }
            if(installedVersionNumber.compareTo(new VersionNumber(packageProperties.getProperty(JPackageManagerBU.PACKAGE_VERSION_KEY))) >= 0)
            {
            	logger.info("  installed version is more recent.");
                //installed version greater than or equal to package to install
                packageProperties = null;
            }
        }
        else
        {
        	logger.info("  could not find package.name or package.version values for new package.");
            //return null to signify error/invalid
            packageProperties = null;
        }
        return packageProperties;
    }
    
    public static boolean configure(File tempDir, Configuration configuration)
    {
    	logger.info("PackageManager::configure()");   
        boolean status = true;
        if(tempDir != null && configuration != null && !configuration.isEmpty())
        {
            VelocityEngine velocityEngine = new VelocityEngine();
            Properties vProps = new Properties();
    		vProps.setProperty("resource.loader", "string");
    		vProps.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");		
    		velocityEngine.init(vProps);
            Template template = null;
            VelocityContext velocityContext = JPackageManagerBU.createVelocityContext(configuration);
			StringResourceRepository stringResourceRepository = StringResourceLoader.getRepository();
			String templateContent = null;
            StringWriter stringWriter = null;
            long lastModified;
            
            Collection<File> patchFiles = 
                    FileUtils.listFiles(
                        new File(tempDir.getAbsolutePath() + "/" + JPackageManagerBU.PATCH_DIR_NAME + "/" + JPackageManagerBU.PATCH_FILES_DIR_NAME), 
                        TEMPLATE_SUFFIX_FILE_FILTER, 
                        DirectoryFileFilter.DIRECTORY);
            
            if(patchFiles != null)
            {
                for(File pfile : patchFiles)
                {
                	logger.debug("  processing patch fileset file: " + pfile.getAbsolutePath());
                    try
                    {
                    	lastModified = pfile.lastModified();
                    	templateContent = FileUtils.readFileToString(pfile);
                    	templateContent = templateContent.replaceAll("(\\$)(\\{)([^\\}]*)(\\:)([^\\}]*)(\\})", "#$2$3$4$5$6");
                    	
                    	stringResourceRepository.putStringResource(JPackageManagerBU.CURRENT_TEMPLATE_NAME, templateContent);
                    	stringWriter = new StringWriter();
                        template = velocityEngine.getTemplate(JPackageManagerBU.CURRENT_TEMPLATE_NAME);
                        template.merge(velocityContext, stringWriter);
                        
                        templateContent = stringWriter.toString();
                        templateContent = templateContent.replaceAll("(#)(\\{)([^\\}]*)(\\:)([^\\}]*)(\\})", "\\$$2$3$4$5$6");

                        FileUtils.writeStringToFile(pfile, templateContent);
                        pfile.setLastModified(lastModified);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            
            Collection<File> scriptFiles = 
                    FileUtils.listFiles(
                        new File(tempDir.getAbsolutePath() + "/" + JPackageManagerBU.AUTORUN_DIR_NAME), 
                        TEMPLATE_SUFFIX_FILE_FILTER, 
                        DirectoryFileFilter.DIRECTORY);

            if(scriptFiles != null)
            {
                for(File scriptfile : scriptFiles)
                {
                	logger.debug("  processing script file: " + scriptfile.getAbsolutePath());
                    try
                    {
                    	lastModified = scriptfile.lastModified();
                    	templateContent = FileUtils.readFileToString(scriptfile);
                    	templateContent = templateContent.replaceAll("(\\$)(\\{)([^\\}]*)(\\:)([^\\}]*)(\\})", "#$2$3$4$5$6");
                    	
                    	stringResourceRepository.putStringResource(JPackageManagerBU.CURRENT_TEMPLATE_NAME, templateContent);
                    	stringWriter = new StringWriter();
                        template = velocityEngine.getTemplate(JPackageManagerBU.CURRENT_TEMPLATE_NAME);
                        template.merge(velocityContext, stringWriter);
                        
                        templateContent = stringWriter.toString();
                        templateContent = templateContent.replaceAll("(#)(\\{)([^\\}]*)(\\:)([^\\}]*)(\\})", "\\$$2$3$4$5$6");

                        FileUtils.writeStringToFile(scriptfile, templateContent);
                        scriptfile.setLastModified(lastModified);

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return status;
    }
    
    private static VelocityContext createVelocityContext(Configuration configuration)
    {
    	VelocityContext velocityContext = new VelocityContext();
    	if(configuration != null)
    	{
            String key = null;
            for(Iterator<String> it=configuration.getKeys(); it.hasNext(); )
            {
                key = it.next();
                velocityContext.put(key, configuration.getString(key));
            }
    	}
    	return velocityContext;
    }

    public static boolean deploy(File targetRootDir, File tempDir)
    {
    	logger.info("PackageManager::deploy()");
        boolean status = true;
        if(targetRootDir != null && tempDir != null)
        {
            JPatchManager directoryPatchManager = new JPatchManager();
            directoryPatchManager.executePatch(
                    new File(tempDir.getAbsolutePath() + "/" + JPackageManagerBU.PATCH_DIR_NAME),
                    targetRootDir, 
                    null, 
                    null, 
                    false, 
                    false);
        }
        return status;
    }
    
    public static boolean autorun(File autorunDir)
    {
    	logger.info("PackageManager::autorun()");
        boolean status = true;

        if(autorunDir != null && autorunDir.isDirectory())
        {
            File[] autorunFiles = autorunDir.listFiles();
            Arrays.sort(autorunFiles);
            String fileExtension = null;
            DefaultExecutor cmdExecutor = new DefaultExecutor();
            for(File autorunFile : autorunFiles)
            {
                if(!autorunFile.isDirectory())
                {
                    try
                    {
                        fileExtension = FilenameUtils.getExtension(autorunFile.getAbsolutePath());
                        if(fileExtension != null)
                        {
                            if(fileExtension.equalsIgnoreCase("bat") || fileExtension.equalsIgnoreCase("sh"))
                            {
                                logger.info("  executing autorun file: " + autorunFile.getAbsolutePath());
                                cmdExecutor.execute(CommandLine.parse(autorunFile.getAbsolutePath()));
                            }
                            else if(fileExtension.equalsIgnoreCase("sql") || fileExtension.equalsIgnoreCase("ddl"))
                            {
                                logger.info("  executing autorun file: " + autorunFile.getAbsolutePath());
                            }
                            else if(fileExtension.equalsIgnoreCase("jar"))
                            {
                                logger.info("  executing autorun file: " + autorunFile.getAbsolutePath());
                            }
                        }
                    }
                    catch(Exception e)
                    {
                    	logger.error("", e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return status;
    }
    
    public static boolean finalize(String packageName, String packageVersion, File packageManagerDataDir, File targetRootDir)
    {
    	logger.info("PackageManager::finalize()");
        boolean status = true;

        Package installPackage = JPackageManagerBU.findInstalledPackage(packageName);
        if(installPackage == null)
        {
            //this is a new install
            installPackage = 
                    JPackageManagerBU.createInstalledPackage(
                        packageName, 
                        packageVersion, 
                        targetRootDir.getAbsolutePath());
            JPackageManagerBU.marshallToFile(JPackageManagerBU.findInstalledPackages(), packageManagerDataDir.getAbsolutePath() + "/" + PACKAGE_FILE_NAME);
        }
        else
        {
            installPackage.setVersion(packageVersion);
            installPackage.setInstallTimestamp(System.currentTimeMillis());
            JPackageManagerBU.marshallToFile(JPackageManagerBU.findInstalledPackages(), JPackageManagerBU.PACKAGE_MANAGER_DATA_DIR_NAME + "/" + PACKAGE_FILE_NAME);
        }
        logger.info("  recorded package installation.");

        JPackageManagerBU.archivePackage(installPackage, packageManagerDataDir);
        logger.info("  archived pacakge.");
        
        return status;
    }    
    
    public static boolean cleanup(File tempDir)
    {
    	logger.info("PackageManager::cleanup()");
        boolean status = true;
        try
        {
            FileUtils.deleteDirectory(tempDir);
        }
        catch(Exception e)
        {
        	logger.error("", e);
//            e.printStackTrace();
        }
        logger.info("  cleaned up temp dir.");
        return status;
    }    
    
    private static void archivePackage(Package installPackage, File packageManagerDataDir)
    {
        // create "archive" dir if it doesnt exist
        // create package archive dir (<package name>_v_<package version>)
        // copy contents of package metadata, autorun and patch file to archive dir
        try
        {
            File archiveDir = new File(packageManagerDataDir.getAbsolutePath() + "/" + ARCHIVE_DIR_NAME);
            File archivePackageDir = new File(archiveDir.getAbsolutePath() + "/" + installPackage.getName() + "_v_" + installPackage.getVersion());
//            if(!archivePackageDir.exists())
//            {
//                archivePackageDir.mkdirs();
//            }
            
            File archivePackageInstallDir = new File(archivePackageDir.getAbsolutePath() + "/" + JPackageManagerBU.INSTALL_DIR_NAME);
            if(!archivePackageInstallDir.exists())
            {
            	archivePackageInstallDir.mkdirs();
            }
            
            File archivePackageUninstallDir = new File(archivePackageDir.getAbsolutePath() + "/" + JPackageManagerBU.UNINSTALL_DIR_NAME);
            if(!archivePackageUninstallDir.exists())
            {
            	archivePackageUninstallDir.mkdirs();
            }
            
            // metadata archive dir
            FileUtils.copyDirectory(
                    new File(packageManagerDataDir.getAbsolutePath() + "/" + TEMP_INSTALL_DIR_NAME + "/" + JPackageManagerBU.METADATA_DIR_NAME), 
                    new File(archivePackageDir.getAbsolutePath()+ "/" + JPackageManagerBU.METADATA_DIR_NAME));
                
            // install archive dir
            FileUtils.copyDirectory(
                new File(packageManagerDataDir.getAbsolutePath() + "/" + TEMP_INSTALL_DIR_NAME + "/" + JPackageManagerBU.AUTORUN_DIR_NAME + "/" + JPackageManagerBU.INSTALL_DIR_NAME), 
                new File(archivePackageInstallDir.getAbsolutePath() + "/" + JPackageManagerBU.AUTORUN_DIR_NAME));
            
            FileUtils.copyFileToDirectory(
                new File(packageManagerDataDir.getAbsolutePath() + "/" + TEMP_INSTALL_DIR_NAME + "/" + JPackageManagerBU.PATCH_DIR_NAME + "/" + JPackageManagerBU.PATCH_FILE_NAME), 
                new File(archivePackageInstallDir.getAbsolutePath() + "/" + JPackageManagerBU.PATCH_DIR_NAME));
            
            // uninstall archive dir
            FileUtils.copyDirectory(
                new File(packageManagerDataDir.getAbsolutePath() + "/" + TEMP_INSTALL_DIR_NAME + "/" + JPackageManagerBU.AUTORUN_DIR_NAME + "/" + JPackageManagerBU.UNINSTALL_DIR_NAME), 
                new File(archivePackageUninstallDir.getAbsolutePath() + "/" + JPackageManagerBU.AUTORUN_DIR_NAME));
            
            FileUtils.copyFileToDirectory(
                new File(packageManagerDataDir.getAbsolutePath() + "/" + TEMP_INSTALL_DIR_NAME + "/" + JPackageManagerBU.PATCH_DIR_NAME + "/" + JPackageManagerBU.PATCH_FILE_NAME), 
                new File(archivePackageUninstallDir.getAbsolutePath() + "/" + JPackageManagerBU.PATCH_DIR_NAME));
            
        }
        catch(Exception e)
        {
        	logger.error("", e);
//            e.printStackTrace();
        }        
    }
    
   
    // ======================================================================
    // MARSHALLING METHODS
    // ======================================================================
    private static void marshallToFile(Collection<Package> installPackages, String filePath)
    {
        try
        {
            Marshaller marshaller = jaxbContext.createMarshaller();

            // removes the xml header:
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter stringWriter = new StringWriter();
            for(Package installPackage : installPackages)
            {
                marshaller.marshal(
                    new JAXBElement<Package>(
                            new QName(
                                null, 
                                "installPackage"), 
                            Package.class, 
                            installPackage), 
                    stringWriter);
                
                stringWriter.append("\n");
            }
            
            FileUtils.writeStringToFile(new File(filePath), stringWriter.toString(), "UTF-8");
        }
        catch(Exception e)
        {
        	logger.error("", e);
//            e.printStackTrace();
        }
    }

    private static Collection<Package> unmarshallFromFile(String inputFilePath)
    {
        List<Package> installPackageList = null;
        try
        {
            File inputFile = new File(inputFilePath);
            if(inputFile.exists())
            {
                Unmarshaller unmarashaller = jaxbContext.createUnmarshaller();
                List<String> lines = FileUtils.readLines(new File(inputFilePath));
                installPackageList = new ArrayList<Package>();
                for(String line : lines)
                {
                    installPackageList.add((Package)unmarashaller.unmarshal(new StringReader(line)));
                }
            }
        }
        catch(Exception e)
        {
        	logger.error("", e);
//            e.printStackTrace();
        }
        return installPackageList;
    }
    
    
    
    // ======================================================================
    // INSTALLED PACKAGE METHODS
    // ======================================================================
    
    private static Collection<Package> findInstalledPackages()
    {
        return installPackageMap.values();
    }
    
    private static Collection<Package> findInstalledPackages(String regex)
    {
        List<Package> installPackageList = new ArrayList<Package>();
        for(Package installPackage : JPackageManagerBU.findInstalledPackages())
        {
            if(installPackage.getName().matches(regex))
            {
                installPackageList.add(installPackage);
            }
        }
        return installPackageList;
    }
    
    private static Package findInstalledPackage(String name)
    {
        return installPackageMap.get(name);
    }
    
    private static Package createInstalledPackage(String name, String version, String rootDirectory)
    {
        Package installPackage = JPackageManagerBU.findInstalledPackage(name);
        if(installPackage == null)
        {
            installPackage = new Package(name, version, rootDirectory, System.currentTimeMillis());
            JPackageManagerBU.addInstalledPackage(installPackage);
        }
        return installPackage;
    }
    
    private static void addInstalledPackage(Package installPackage)
    {
        installPackageMap.put(installPackage.getName(), installPackage);
    }
    
    private static void removeInstalledPackage(String name)
    {
        installPackageMap.remove(name);
    }
    
    private static void clearInstalledPackages()
    {
        installPackageMap.clear();
    }
    
    public static VersionNumber getInstalledVersionNumber(String packageName)
    {
        VersionNumber versionNumber = null;
        Package installPackage = JPackageManagerBU.findInstalledPackage(packageName);
        if(installPackage != null)
        {
            versionNumber = installPackage.getVersionNumber();
        }
        return versionNumber;
    }

    
    // ======================================================================
    // MAIN
    // ======================================================================
    public static void main(String[] args)
    {
//    	StringTemplate templ = new StringTemplate("foo $fo$bo$r$ yo");
//    	templ.setAttribute("success", "foobar");
//    	templ.setAttribute("bo", "oba");
//        System.out.println(templ.toString());
        
        
        
        try
        {
//        	StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        	String firstTemplate = "firstTemplate";
        	String content = "this should ${foobar} ${foo:bar.0.1}";
        	String updatedContent = "this should ${foobar} #{foo:bar.0.1}";
//        	String content = "this should ${foo:bar}";

//        	System.out.println(content.matches("\\$\\{.*\\:.*\\}"));
        	
//        	System.out.println(content.replaceAll("\\$\\{.*\\:.*\\}", "hahaha"));
//        	System.out.println(content.replaceAll("(\\$\\{.*)(\\:)(.*\\})", "$1-$3"));
//        	System.out.println(content.replaceAll("(\\$\\{.*)(\\:)(.*\\})", "$1-$3"));
//        	System.out.println(content.replaceAll("(\\$)(\\{.*)(\\:)(.*\\})", "#$2$3$4"));
        	System.out.println(updatedContent.replaceAll("(#)(\\{)([^\\}]*)(\\:)([^\\}]*)(\\})", "\\$$2$3$4$5$6"));
        	System.out.println(content.replaceAll("(\\$)(\\{)(.*)(\\:)(.*)(\\})", "--$2$3$4$5$6--"));
        	System.out.println(content.replaceAll("(\\$)(\\{\\w*\\:\\w*\\})", "#$2"));
        	
//        	stringTemplateLoader.putTemplate(firstTemplate, "this should ${foobar} ${foo:bar}");
//        	
//            freemarker.template.Configuration freeMarkerConfiguration = new freemarker.template.Configuration();
//            freeMarkerConfiguration.setTemplateLoader(stringTemplateLoader);
//        	Template template = freeMarkerConfiguration.getTemplate(firstTemplate);        	
//            Map<String, Object> valueMap = new HashMap<String, Object>();
//            valueMap.put("foobar", "helloworld");
//            
//            Writer out = new OutputStreamWriter(System.out);
//            template.process(valueMap, out);
//            out.flush();
//            
//            freeMarkerConfiguration.clearTemplateCache();
        	
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        
        
        
		System.out.println("");
		System.out.println("");
        
        
		VelocityEngine velocityEngine = new VelocityEngine();
		Properties vProps = new Properties();
//		vProps.put("file.resource.loader.path", "");
		vProps.setProperty("resource.loader", "string");
		vProps.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");		
		velocityEngine.init(vProps);
		Template template = null;
		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("bo", "oba");
		velocityContext.put("foobar", "be replaced");

		try
		{
			StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("template", FileUtils.readFileToString(new File("c:/dev/workbench/paradigm_workspace/jpackage-manager/template.xml")));
			StringWriter writer = new StringWriter();
			template = velocityEngine.getTemplate("template");
			template.merge(velocityContext, writer);
			System.out.println(writer.toString());
		}
		catch (Exception e)
		{
			 e.printStackTrace();
		}
        
        
        
        
        
        
        
    }
    
//    private static void printInstalledPackages(Collection<InstalledPackage> installedPacakges)
//    {
//        if(installedPacakges != null)
//        {
//            for(InstalledPackage installPackage : installedPacakges)
//            {
//                logger.info("package name: " + installPackage.getName() + " version:" + installPackage.getVersion() + " timstamp:" + installPackage.getInstallTimestamp());
//            }
//        }
//    }
    
}