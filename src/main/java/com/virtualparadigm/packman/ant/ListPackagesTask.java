package com.virtualparadigm.packman.ant;

import org.apache.tools.ant.Task;

import com.virtualparadigm.packman.processor.JPackageManager;

public class ListPackagesTask extends Task
{
    public ListPackagesTask()
    {
        super(); 
    }
    
    @Override
    public void execute()
    {
        JPackageManager.listPackages();
    }


}