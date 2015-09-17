package com.virtualparadigm.packman.cli;

import java.io.File;
import java.util.Collection;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.virtualparadigm.packman.processor.Package;
import com.virtualparadigm.packman.processor.JPackageManager;

public class Main
{
	public static final String CMD_CREATE = "create";
	public static final String CMD_INSTALL = "install";
	public static final String CMD_LIST = "list";

	public static final String CMD_OPTION_LONG_PACKAGE_NAME = "name";
	public static final String CMD_OPTION_LONG_PACKAGE_VERSION = "version";
	public static final String CMD_OPTION_LONG_PACKAGE_FILE = "packagefile";
	public static final String CMD_OPTION_LONG_LICENSE_FILE = "licensefile";
	public static final String CMD_OPTION_LONG_AUTORUN_INSTALL_DIR = "autoinstalldir";
	public static final String CMD_OPTION_LONG_AUTORUN_UNINSTALL_DIR = "autouninstalldir";
	public static final String CMD_OPTION_LONG_NEW_STATE_DIR = "newdir";
	public static final String CMD_OPTION_LONG_OLD_STATE_DIR = "olddir";
	public static final String CMD_OPTION_LONG_TEMP_DIR = "tempdir";
	public static final String CMD_OPTION_LONG_DEV_MODE = "devmode";
	
	public static final String CMD_OPTION_LONG_TARGET_DIR = "targetdir";
	public static final String CMD_OPTION_LONG_DATA_DIR = "datadir";
	public static final String CMD_OPTION_LONG_LOCAL_CONFIG_FILE = "config";
	
	public static void main(String[] args)
	{
		if(args.length > 0)
		{
			CommandLineParser cliParser = new BasicParser();
			CommandLine cmd = null;
			try
			{
					cmd = cliParser.parse(Main.buildCommandLineOptions(), args);
			}
			catch(ParseException pe)
			{
					throw new RuntimeException(pe);
			}
			if(cmd != null)
			{
				boolean status = false;
				if(CMD_CREATE.equalsIgnoreCase(args[0]))
				{
					status = 
							JPackageManager.createPackage(
								cmd.getOptionValue(CMD_OPTION_LONG_PACKAGE_NAME), 
								cmd.getOptionValue(CMD_OPTION_LONG_PACKAGE_VERSION), 
								new File(cmd.getOptionValue(CMD_OPTION_LONG_PACKAGE_FILE)), 
								new File(cmd.getOptionValue(CMD_OPTION_LONG_OLD_STATE_DIR)), 
								new File(cmd.getOptionValue(CMD_OPTION_LONG_NEW_STATE_DIR)), 
								new File(cmd.getOptionValue(CMD_OPTION_LONG_LICENSE_FILE)), 
								new File(cmd.getOptionValue(CMD_OPTION_LONG_AUTORUN_INSTALL_DIR)), 
								new File(cmd.getOptionValue(CMD_OPTION_LONG_AUTORUN_UNINSTALL_DIR)), 
								(cmd.getOptionValue(CMD_OPTION_LONG_TEMP_DIR) == null) ? null : new File(cmd.getOptionValue(CMD_OPTION_LONG_TEMP_DIR)), 
								(cmd.getOptionValue(CMD_OPTION_LONG_DEV_MODE) != null ) ?  Boolean.valueOf(cmd.getOptionValue(CMD_OPTION_LONG_DEV_MODE)) : false);
					
	        		if(status)
	        		{
	        			System.out.println("Package creation successful");
	        		}
	        		else
	        		{
	        			System.out.println("Package creation failed");
	        		}
				}
				else if(CMD_INSTALL.equalsIgnoreCase(args[0]))
				{
	        		status = 
	        				JPackageManager.installPackage(
			        				new File(cmd.getOptionValue(CMD_OPTION_LONG_PACKAGE_FILE)), 
			        				new File(cmd.getOptionValue(CMD_OPTION_LONG_TARGET_DIR)), 
			        				new File(cmd.getOptionValue(CMD_OPTION_LONG_DATA_DIR)), 
			        				new File(cmd.getOptionValue(CMD_OPTION_LONG_LOCAL_CONFIG_FILE)), 
			        				(cmd.getOptionValue(CMD_OPTION_LONG_DEV_MODE) != null ) ?  Boolean.valueOf(cmd.getOptionValue(CMD_OPTION_LONG_DEV_MODE)) : false);
	        		
	        		if(status)
	        		{
	        			System.out.println("Package installation successful");
	        		}
	        		else
	        		{
	        			System.out.println("Package installation failed");
	        		}
			        
				}
				else if(CMD_LIST.equalsIgnoreCase(args[0]))
				{
			        System.out.println("Installed Packages");
			        System.out.println("");
			        Collection<Package> installedPackages = JPackageManager.listPackages();
			        if(installedPackages != null)
			        {
			            for(Package installedPackage : installedPackages)
			            {
			                System.out.println("package: " + installedPackage.getName());
			                System.out.println("  version: " + installedPackage.getVersion());
			                System.out.println("  timestamp: " + installedPackage.getInstallTimestamp());
			                System.out.println("  root directory: " + installedPackage.getRootDirectory());
			                System.out.println("");
			            }
			        }
				}
				else
				{
					System.out.println("unsupported JPatchManager operation");
				}
			}
			else
			{
				System.out.println("No command specified. Options are:");
				System.out.println("  " + CMD_CREATE);
				System.out.println("  " + CMD_INSTALL);
				System.out.println("  " + CMD_LIST);
			}
		}
	}
	
	private static Options buildCommandLineOptions()
	{
		Options cliOptions = new Options();
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_PACKAGE_NAME);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_PACKAGE_NAME);
		OptionBuilder.withDescription("package name");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_PACKAGE_NAME));
				
		OptionBuilder.withArgName(CMD_OPTION_LONG_PACKAGE_VERSION);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_PACKAGE_VERSION);
		OptionBuilder.withDescription("package version");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_PACKAGE_VERSION));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_PACKAGE_FILE);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_PACKAGE_FILE);
		OptionBuilder.withDescription("package file");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_PACKAGE_FILE));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_LICENSE_FILE);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_LICENSE_FILE);
		OptionBuilder.withDescription("license file");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_LICENSE_FILE));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_AUTORUN_INSTALL_DIR);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_AUTORUN_INSTALL_DIR);
		OptionBuilder.withDescription("install directory");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_AUTORUN_INSTALL_DIR));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_AUTORUN_UNINSTALL_DIR);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_AUTORUN_UNINSTALL_DIR);
		OptionBuilder.withDescription("uninstall directory");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_AUTORUN_UNINSTALL_DIR));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_NEW_STATE_DIR);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_NEW_STATE_DIR);
		OptionBuilder.withDescription("new state directory");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_NEW_STATE_DIR));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_OLD_STATE_DIR);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_OLD_STATE_DIR);
		OptionBuilder.withDescription("old state directory");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_OLD_STATE_DIR));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_DEV_MODE);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_DEV_MODE);
		OptionBuilder.withDescription("development mode");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_DEV_MODE));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_TARGET_DIR);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_TARGET_DIR);
		OptionBuilder.withDescription("target directory");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_TARGET_DIR));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_DATA_DIR);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_DATA_DIR);
		OptionBuilder.withDescription("jpackage manager data directory");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_DATA_DIR));
		
		OptionBuilder.withArgName(CMD_OPTION_LONG_LOCAL_CONFIG_FILE);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_LOCAL_CONFIG_FILE);
		OptionBuilder.withDescription("local install configuration values");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_LOCAL_CONFIG_FILE));

		OptionBuilder.withArgName(CMD_OPTION_LONG_TEMP_DIR);
		OptionBuilder.withLongOpt(CMD_OPTION_LONG_TEMP_DIR);
		OptionBuilder.withDescription("temp directory");
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		cliOptions.addOption(OptionBuilder.create(CMD_OPTION_LONG_TEMP_DIR));
		
		
		return cliOptions;
	}
}