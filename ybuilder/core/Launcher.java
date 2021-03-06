/*
 * Copyright 2011 Christian Essl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package ybuilder.core;

import java.io.*;
import java.net.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Launcher {

	public static final String YBUILDER_URL_CMD = "-update";

	public static final String VERSION = "0.6";
	
	public static final String JAR_NAME = "ybuilder-lib-"+VERSION+".jar";
	
	public static final File HOME_DIR = 
		new File(System.getProperty("user.home"), ".ybuilder/lib");
	
	public static final File JAR_FILE =
		new File(HOME_DIR, JAR_NAME);
	
	public static final File CHECK_FILE = 
			new File(HOME_DIR, "timestamp.txt");


	public static final File PROJECT_CLASS_FILE =
		new File("lib/ybuilder/project/project.class");
	
	public static final String GITHUB_REPO =
 		"http://chrisichris.github.com/chrisis-maven-repo/ybuilder/lib/";

	private static void saveUrl(File target, String sourceUrl) 
			throws MalformedURLException, IOException
    {
    	BufferedInputStream in = null;
    	FileOutputStream fout = null;
    	try
    	{
			if(target.exists()) target.delete();

			System.out.print("Downloading "+JAR_NAME+" to "+target+" ..."); 
    		in = new BufferedInputStream(new URL(sourceUrl).openStream());
    		fout = new FileOutputStream(target);

    		byte data[] = new byte[1024];
    		int count;
			int loadCount = 0;
    		while ((count = in.read(data, 0, 1024)) != -1)
    		{
    			fout.write(data, 0, count);
				loadCount = loadCount + count;
				if(loadCount > (20 * 1024)) {
					System.out.print(".");
					loadCount = 0;
				}
    		}
			System.out.println("\n");
			
    	}
    	finally
    	{
    		if (in != null)
    			in.close();
    		if (fout != null)
    			fout.close();
    	}
    }

	private static boolean checkUpdate(File target, File checkFile, 
										String sourceUrl) {
		long lastCheck = checkFile.lastModified();
	
		//check every hour
		if(lastCheck < System.currentTimeMillis() - (1000 * 60 * 60 * 3 )) {
			System.out.println("Checking for new ybuilder version");
			long lastModified = 0;
			try{
				URLConnection con = new URL(sourceUrl).openConnection();
				con.connect();
				lastModified = con.getLastModified();
				//update checkfile
				if(checkFile.exists()) {
					checkFile.delete();
				}

				checkFile.createNewFile();

			}catch(IOException ex) {
			}
			
			return lastModified > target.lastModified();
		}else{
			return false;
		}
	}


	public static void main(String[] args) throws Throwable {
		//check -version
		if (args == null)
			args = new String[]{};

		if (args.length > 0 && "-version".equals(args[0])) {
			System.out.println(VERSION);
			return;
		};

		//check systemproperties
		ArrayList<String> _newArgs = new ArrayList<String>();
		for (String arg:args) {
			if(arg.startsWith("-D")){
				String nv = arg.substring(2,arg.length());
				int cut = nv.indexOf("=");
				if(cut < 0)
					System.setProperty(nv,"");
				else{
					if(cut == 0)
						throw new IllegalArgumentException(
										"SystemProperty "+arg);
					else{
						System.setProperty(
								nv.substring(0,cut),
								nv.substring(cut+1, nv.length()));
					}
				}
			}else{
				_newArgs.add(arg);
			}
		}
		args = _newArgs.toArray(new String[_newArgs.size()]);
		
		//check wheter we have the home dir
		if(!HOME_DIR.exists())
			HOME_DIR.mkdirs();

		
		//check other download url
		boolean download = !JAR_FILE.exists();
		String downloadUrl = GITHUB_REPO;

		if(args != null && args.length > 0 && YBUILDER_URL_CMD.equals(args[0])) {
			download = true;
			if (args.length > 1) {
				downloadUrl = args[1];
				System.out.println("Downloading from: "+downloadUrl);
				String[] newArgs = new String[args.length -1];
				System.arraycopy(args,1,newArgs, 0,args.length -1);
				args = newArgs;
			}
					

		}else{
			download = checkUpdate(JAR_FILE,CHECK_FILE, GITHUB_REPO + JAR_NAME);
		}	
					
		//check wheter we have the jar
		if(download) {
			String jarUrl = downloadUrl + JAR_NAME;
			try {
				saveUrl(JAR_FILE,jarUrl);
			}catch(Exception ex) {
				System.out.println(
					"\n\n\nERRROR: Could not retrieve ybuilder.jar at:"+jarUrl);
				System.out.println("Reason: "+ex);
				System.exit(-1);
			}
		}

		//check wheter the ybuilder jar is newer than the project
		//in that case recompile it
		if(PROJECT_CLASS_FILE.exists() && 
				PROJECT_CLASS_FILE.lastModified() < JAR_FILE.lastModified()) {
			System.out.println("New ybuilder.jar recompiling project");
			PROJECT_CLASS_FILE.delete();
		};

		//load the jar in a classlaoder
		ClassLoader cl = 
			new URLClassLoader(
					new URL[]{JAR_FILE.toURI().toURL()},
					Thread.currentThread().getContextClassLoader());

		Thread.currentThread().setContextClassLoader(cl);
		
		//and start the main application
		try{
			cl.loadClass("ybuilder.core.main")
				.getMethod("main", new Class[]{String[].class})
				.invoke(null, new Object[]{args});
		}catch(InvocationTargetException ex) {
			throw ex.getCause();
		}

	}

}
