package com.virtualparadigm.packman.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class ZipUtils
{
    public static void unzipArchive(File archive, File outputDir)
    {
        try
        {
//        	if(!archive.getParentFile().getAbsolutePath().equals(outputDir.getAbsoluteFile()))
//        	{
//                FileUtils.deleteDirectory(outputDir);
//        	}
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements();)
            {
                ZipEntry entry = e.nextElement();
                unzipEntry(zipfile, entry, outputDir);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException
    {
        if (entry.isDirectory())
        {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists())
        {
            createDir(outputFile.getParentFile());
        }

        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try
        {
            IOUtils.copy(inputStream, outputStream);
        }
        finally
        {
            outputStream.close();
            inputStream.close();
        }
        outputFile.setLastModified(ZipUtils.toLong(entry.getExtra()));
    }

    public static File createZipFile(String zipFilePath, File[] files, int bufferSize)
    {
        File zipFile = null;
        try
        {
            zipFile = new File(zipFilePath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            ZipUtils.addToZip(zipOutputStream, files, "", new byte[bufferSize]);
            zipOutputStream.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return zipFile;
    }
    
    private static void addToZip(ZipOutputStream zipOutputStream, File[] files, String zipSubDir, byte[] buffer)
    {
        if(zipOutputStream != null && files != null)
        {
            InputStream entryInputStream = null;
            ZipEntry zipEntry = null;
            for(int fileIndex=0; fileIndex<files.length; fileIndex++)
            {
                try
                {
                    if(files[fileIndex].isDirectory())
                    {
                    	if(zipSubDir == null || zipSubDir.length() == 0)
                    	{
                            zipOutputStream.putNextEntry(new ZipEntry(files[fileIndex].getName()+"/"));
                            ZipUtils.addToZip(zipOutputStream, files[fileIndex].listFiles(), files[fileIndex].getName(), buffer);
                    	}
                    	else
                    	{
                            zipOutputStream.putNextEntry(new ZipEntry(zipSubDir + "/" + files[fileIndex].getName()+"/"));
                            ZipUtils.addToZip(zipOutputStream, files[fileIndex].listFiles(), zipSubDir + "/" + files[fileIndex].getName(), buffer);
                    	}
                    }
                    else
                    {
                        try
                        {
                            entryInputStream = new FileInputStream(files[fileIndex]);
                            zipEntry = new ZipEntry(zipSubDir + "/" + files[fileIndex].getName());
                            zipEntry.setTime(files[fileIndex].lastModified());
                            zipEntry.setExtra(ZipUtils.toBytes(files[fileIndex].lastModified()));
                            
                            zipOutputStream.putNextEntry(zipEntry);
                            int length;
                            while((length = entryInputStream.read(buffer)) > 0)
                            {
                                zipOutputStream.write(buffer, 0, length);
                            }
                            zipOutputStream.closeEntry();
                            entryInputStream.close();
                        }
                        catch(IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }               
            }
        }
    }       
    
	public static byte[] toBytes(long l)
	{
//	    ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
//	    buffer.putLong(l);
//	    return buffer.array();		
		
		byte[] result = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(Long.SIZE / 8);
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeLong(l);
			result = baos.toByteArray();
		}
		catch(IOException e)
		{
		}
		finally
		{
			try
			{
				dos.close();
			}
			catch(IOException ioe)
			{
			}
		}
		return result;
	}

	public static long toLong(byte[] b) 
	{
//		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
//		buffer.put(b);
//		buffer.flip();// need flip
//		return buffer.getLong();
		
		long result = -1;
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		DataInputStream dis = new DataInputStream(bais);
		try
		{
			result = dis.readLong();
			dis.close();
		}
		catch(IOException e)
		{
		}
		finally
		{
			try
			{
				dis.close();
			}
			catch(IOException ioe)
			{
			}
		}
		return result;
	}    
    
    private static void createDir(File dir)
    {
        if (!dir.mkdirs())
            throw new RuntimeException("Can not create dir " + dir);
    }
    
    
}