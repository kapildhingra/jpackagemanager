package com.virtualparadigm.packman.processor;

public abstract class InstallAction
{
	private String name;
	
	public InstallAction(String name)
	{
		super();
		this.name = name;
	}

	public abstract Object execute() throws Exception;
	
	
}