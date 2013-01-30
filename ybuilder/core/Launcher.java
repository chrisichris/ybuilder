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

public class Launcher {

	public static final String VERSION = "0.6";
	
	public static final String JAR_NAME = "ybuilder-lib-"+VERSION+".jar";
	
	public static final File HOME_DIR = 
		new File(System.getProperty("user.home"), ".ybuilder/lib");
	
	public static final File JAR_FILE =
		new File(HOME_DIR, JAR_NAME);
	
	public static final String JAR_URL =
 		"http://chrisichris.github.com/chrisis-maven-repo/ybuilder/"
		+ JAR_NAME;

	private static void saveUrl(File target, String sourceUrl) 
			throws MalformedURLException, IOException
    {
    	BufferedInputStream in = null;
    	FileOutputStream fout = null;
    	try
    	{
    		in = new BufferedInputStream(new URL(sourceUrl).openStream());
    		fout = new FileOutputStream(target);

    		byte data[] = new byte[1024];
    		int count;
    		while ((count = in.read(data, 0, 1024)) != -1)
    		{
    			fout.write(data, 0, count);
				System.out.println(".");
    		}
    	}
    	finally
    	{
    		if (in != null)
    			in.close();
    		if (fout != null)
    			fout.close();
    	}
    }

	public static void main(String[] args) throws Exception {
		//check -version
		if (args != null && args.length > 0 && "-version".equals(args[0]))
			System.out.println("Ybuilder launcher version "+VERSION);
		
		//check wheter we have the home dir
		if(!HOME_DIR.exists())
			HOME_DIR.mkdirs();
		
		//check wheter we have the jar
		if(!JAR_FILE.exists()) {
			try {
				saveUrl(JAR_FILE,JAR_URL);
			}catch(Exception ex) {
				System.out.println(
					"Could not retrieve ybuilder.jar at:"+JAR_URL);
				System.out.println("Reason: "+ex);
				System.exit(-1);
			}
		}

		//load the jar in a classlaoder
		ClassLoader cl = 
			new URLClassLoader(
					new URL[]{JAR_FILE.toURI().toURL()},
					Thread.currentThread().getContextClassLoader());

		Thread.currentThread().setContextClassLoader(cl);
		
		//and start the main application
		cl.loadClass("ybuilder.core.main")
				.getMethod("main", new Class[]{String[].class})
				.invoke(null, new Object[]{args});

	}

}
