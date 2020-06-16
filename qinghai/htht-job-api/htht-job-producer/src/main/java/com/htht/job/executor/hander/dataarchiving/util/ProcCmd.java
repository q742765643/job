package com.htht.job.executor.hander.dataarchiving.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProcCmd {
	private String charsetName = "GBK";
	private String logFilePath = "c:/log";
	
	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public boolean exec(String commandstr){
		try{
			final Process pr = Runtime.getRuntime().exec(commandstr); 
			
			new Thread(new Runnable(){ 
				public void run() { 
					BufferedReader br_in = null; 
					try { 
						br_in = new BufferedReader(new InputStreamReader(pr.getInputStream(),charsetName)); 
						String buff = null; 
						while ((buff = br_in.readLine()) != null) { 
//							writeComLog("Process out :" + buff);
							System.out.println("Process out :" + buff);
							try {Thread.sleep(100); } catch(Exception e) {} 
						} 
						br_in.close(); 
					} catch (IOException ioe) { 
						System.out.println("Exception caught printing process output."); 
						ioe.printStackTrace(); 
					} finally { 
						try { 
							br_in.close(); 
						} catch (Exception ex) {} 
					} 
				} 
			}).start(); 
	
			new Thread(new Runnable(){ 
				public void run() { 
					BufferedReader br_err = null; 
					try { 
						br_err = new BufferedReader(new InputStreamReader(pr.getErrorStream(), charsetName)); 
						String buff = null; 
						while ((buff = br_err.readLine()) != null) { 
//							writeComLog("Process err :" + buff);
							try {Thread.sleep(100); } catch(Exception e) {} 
						} 
						br_err.close(); 
					} catch (IOException ioe) { 
						System.out.println("Exception caught printing process error."); 
						ioe.printStackTrace(); 
					} finally { 
						try { 
							br_err.close(); 
						} catch (Exception ex) {} 
					} 
				} 
			}).start(); 
			
			//阻塞线程,等待命令执行完毕
			pr.waitFor(); 
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
     * 记录日志内容
     * */
    public synchronized void writeComLog(String str) {
        Calendar c = GregorianCalendar.getInstance();
        String filename = logFilePath +"/"+ "databackup-"+c.get(c.YEAR)+ fillZero(1+c.get(c.MONTH)+"", 2) + ".log";
        try {
            BufferedWriter bufOut;
            File f = new File(filename);
            if(f.exists()==true){
                bufOut = new BufferedWriter(new FileWriter(f,true));
            } else {
                bufOut = new BufferedWriter(new FileWriter(f));
            }
            String datetime = "" + c.get(c.YEAR) + "-"
                               + fillZero(1+c.get(c.MONTH)+"", 2) + "-"
                               + fillZero(""+c.get(c.DAY_OF_MONTH), 2) + " "
                               + fillZero(""+c.get(c.HOUR), 2) + ":"
                               + fillZero(""+c.get(c.MINUTE), 2) + ":"
                               + fillZero(""+c.get(c.SECOND), 2);
            bufOut.write("["+datetime+"] "+str + "\n");
			System.out.println("["+datetime+"] "+str); 
            bufOut.close();
         
        } catch(Exception e) {
            System.out.println("Error");
        }
    }
    
    /*右对齐左补零*/
    public static String fillZero(String str, int len) {
        int tmp = str.length();
        int t;
        String str1 = str;
        if(tmp >= len)
          return str1;
        t = len - tmp;
        for(int i = 0; i < t; i++ )
          str1 = "0" + str1;
        return str1;
     }
}
