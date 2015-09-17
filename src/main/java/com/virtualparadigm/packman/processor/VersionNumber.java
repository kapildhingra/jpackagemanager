package com.virtualparadigm.packman.processor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VersionNumber implements Serializable, Comparable<VersionNumber>
{
    private static final long serialVersionUID = 1L;
    private static final String SEPERATOR = ".";
    
    private List<Integer> digits;

    public VersionNumber()
    {
        super();
    }

    public VersionNumber(String str)
    {
        super();
        this.digits = new ArrayList<Integer>();
        if(str != null)
        {
            String[] strDigits = str.split("\\.");
            for(String strDigit : strDigits)
            {
                this.digits.add(Integer.parseInt(strDigit));
            }
        }
        else
        {
            this.digits.add(0);
        }
    }

    public VersionNumber(List<Integer> digits)
    {
        super();
        if(digits != null && digits.size() > 0)
        {
            this.digits = digits;
        }
        else
        {
            this.digits = new ArrayList<Integer>();
            this.digits.add(0);
        }
    }

    public List<Integer> getDigits()
    {
        return digits;
    }
    
    // NEEDS TO BE IMMUTABLE
//    public void setDigits(List<Integer> digits)
//    {
//        this.digits = digits;
//    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if(this.digits.size() > 0)
        {
            sb.append(this.digits.get(0));
            
            int i = 1;
            while(i < this.digits.size())
            {
                sb.append(SEPERATOR);
                sb.append(this.digits.get(i));
                i++;
            }
        }
        return sb.toString();
    }
    
    
    public int compareTo(VersionNumber version)
    {
        int result = 0;
        if(this.digits.size() == version.getDigits().size())
        {
            for(int i=0; i<this.digits.size(); i++)
            {
                result = this.digits.get(i).compareTo(version.getDigits().get(i));
                if(result != 0)
                {
                    break;
//                    return result;
                }
            }
        }
        else
        {
            //start with whichever has less numbers
            if(this.digits.size() < version.getDigits().size())
            {
                for(int i=0; i<this.digits.size(); i++)
                {
                    result = this.digits.get(i).compareTo(version.getDigits().get(i));
                    if(result != 0)
                    {
                        break;
//                      return result;
                    }
                }
                if(result == 0)
                {
                    result = -1;
                }
            }
            else
            {
                for(int i=0; i<version.getDigits().size(); i++)
                {
                    result = this.digits.get(i).compareTo(version.getDigits().get(i));
                    if(result != 0)
                    {
                        break;
//                      return result;
                    }
                }
                if(result == 0)
                {
                    result = 1;
                }
            }
        }
        return result;
    }    
    
    
    public static void main(String[] args)
    {

        
        System.out.println("Version(1.2.3.4).compareTo(Version(1.2.3.4)) = " + new VersionNumber("1.2.3.4").compareTo(new VersionNumber("1.2.3.4")));
        System.out.println("Version(1.2.3.0).compareTo(Version(1.2.3.4)) = " + new VersionNumber("1.2.3.0").compareTo(new VersionNumber("1.2.3.4")));
        System.out.println("Version(1.2.3.0.0).compareTo(Version(1.2.3.4)) = " + new VersionNumber("1.2.3.0.0").compareTo(new VersionNumber("1.2.3.4")));
        System.out.println("Version(1.2.3.4).compareTo(Version(1.2.3.2)) = " + new VersionNumber("1.2.3.4").compareTo(new VersionNumber("1.2.3.2")));
        System.out.println("Version(1.2.3).compareTo(Version(1.2.3.2)) = " + new VersionNumber("1.2.3").compareTo(new VersionNumber("1.2.3.2")));
        System.out.println("Version(4).compareTo(Version(1.2.3.2)) = " + new VersionNumber("4").compareTo(new VersionNumber("1.2.3.2")));
        System.out.println("Version(4.3.2.1).compareTo(Version(1.2.3.2)) = " + new VersionNumber("4.3.2.1").compareTo(new VersionNumber("1.2.3.2")));
        
        
    }
    
    
}