/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $Id: FactoryFinder.java 446598 2006-09-15 12:55:40Z jeremias $

package jp.kshoji.javax.xml.datatype;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * <p>Implement pluggabile Datatypes.</p>
 * 
 * <p>This class is duplicated for each JAXP subpackage so keep it in
 * sync.  It is package private for secure class loading.</p>
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 446598 $, $Date: 2006-09-15 08:55:40 -0400 (Fri, 15 Sep 2006) $
 * @since 1.5
 */
final class FactoryFinder {
	
	/**
	 * <p>Name of class to display in output messages.</p>
	 */
	private static final String CLASS_NAME = "javax.xml.datatype.FactoryFinder";
	
    /**
     * <p>Debug flag to trace loading process.</p>
     */
    private static boolean debug = false;
    
    /**
     * <p>Cache properties for performance.</p>
     */
	private static Properties cacheProps = new Properties();
	
	/**
	 * <p>First time requires initialization overhead.</p>
	 */
	private static boolean firstTime = true;
    
    /**
     * Default columns per line.
     */
    private static final int DEFAULT_LINE_LENGTH = 80;
	
	/**
	 * <p>Check to see if debugging enabled by property.</p>
	 * 
	 * <p>Use try/catch block to support applets, which throws
     * SecurityException out of this code.</p>
	 * 
	 */
    static {
        try {
            String val = SecuritySupport.getSystemProperty("jaxp.debug");
            // Allow simply setting the prop to turn on debug
            debug = val != null && (! "false".equals(val));
        } catch (Exception x) {
            debug = false;
        }
    }
    
    private FactoryFinder() {}

	/**
	 * <p>Output debugging messages.</p>
	 * 
	 * @param msg <code>String</code> to print to <code>stderr</code>.
	 */
    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println(
            	CLASS_NAME
            	+ ":"
            	+ msg);
        }
    }

    /**
     * <p>Find the appropriate <code>ClassLoader</code> to use.</p>
     * 
     * <p>The context ClassLoader is prefered.</p>
     * 
     * @return <code>ClassLoader</code> to use.
     * 
     * @throws javax.xml.datatype.FactoryFinder.ConfigurationError If a valid <code>ClassLoader</code> cannot be identified.
     */
    private static ClassLoader findClassLoader()
        throws ConfigurationError {
        ClassLoader classLoader;

        // Figure out which ClassLoader to use for loading the provider
        // class.  If there is a Context ClassLoader then use it.

        classLoader = SecuritySupport.getContextClassLoader();            

        if (debug) debugPrintln(
            "Using context class loader: "
            + classLoader);

        if (classLoader == null) {
            // if we have no Context ClassLoader
            // so use the current ClassLoader
            classLoader = FactoryFinder.class.getClassLoader();
            if (debug) debugPrintln(
                "Using the class loader of FactoryFinder: "
                + classLoader);                
        }
                    
        return classLoader;
    }

    /**
     * <p>Create an instance of a class using the specified ClassLoader.</p>
     * 
     * @param className Name of class to create.
     * @param classLoader ClassLoader to use to create named class.
     * 
     * @return New instance of specified class created using the specified ClassLoader.
     * 
     * @throws javax.xml.datatype.FactoryFinder.ConfigurationError If class could not be created.
     */
    private static Object newInstance(
    	String className,
        ClassLoader classLoader)
        throws ConfigurationError {
        	
        try {
            Class spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            } else {
                spiClass = classLoader.loadClass(className);
            }
            
            if (debug) {
            	debugPrintln("Loaded " + className + " from " + which(spiClass));
            }
             
            return spiClass.newInstance();
        } catch (ClassNotFoundException x) {
            throw new ConfigurationError(
                "Provider " + className + " not found", x);
        } catch (Exception x) {
            throw new ConfigurationError(
                "Provider " + className + " could not be instantiated: " + x,
                x);
        }
    }

    /**
     * Finds the implementation Class object in the specified order.  Main
     * entry point.
     * Package private so this code can be shared.
     *
     * @param factoryId Name of the factory to find, same as a property name
     * @param fallbackClassName Implementation class name, if nothing else is found.  Use null to mean no fallback.
     *
     * @return Class Object of factory, never null
     * 
     * @throws javax.xml.datatype.FactoryFinder.ConfigurationError If Class cannot be found.
     */
    static Object find(String factoryId, String fallbackClassName)
        throws ConfigurationError {
        	
        ClassLoader classLoader = findClassLoader();

        // Use the system property first
        try {
            String systemProp = SecuritySupport.getSystemProperty(factoryId);
            if (systemProp != null) {
                if (debug) debugPrintln("found " + systemProp + " in the system property " + factoryId);
                return newInstance(systemProp, classLoader);
            }
        } catch (SecurityException se) {
        	; // NOP, explicitly ignore SecurityException
        }

        // try to read from $java.home/lib/jaxp.properties
        try {
            String javah = SecuritySupport.getSystemProperty("java.home");
            String configFile = javah + File.separator + "lib" + File.separator + "jaxp.properties";
			String factoryClassName = null;
			if (firstTime) {
				synchronized (cacheProps) {
					if (firstTime) {
						File f = new File(configFile);
						firstTime = false;
						if (SecuritySupport.doesFileExist(f)) {
							if (debug) debugPrintln("Read properties file " + f);
							cacheProps.load(SecuritySupport.getFileInputStream(f));
						}
					}
				}
			}
			factoryClassName = cacheProps.getProperty(factoryId);
            if (debug) debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties"); 
			
			if (factoryClassName != null) {
				return newInstance(factoryClassName, classLoader);
			}
        } catch (Exception ex) {
            if (debug) {
            	ex.printStackTrace();
            } 
        }
        
        // Try Jar Service Provider Mechanism
        Object provider = findJarServiceProvider(factoryId);
        if (provider != null) {
            return provider;
        }

        if (fallbackClassName == null) {
            throw new ConfigurationError(
                "Provider for " + factoryId + " cannot be found", null);
        }

        if (debug) debugPrintln("loaded from fallback value: " + fallbackClassName);
        return newInstance(fallbackClassName, classLoader);
    }

    /*
     * Try to find provider using Jar Service Provider Mechanism
     *
     * @return instance of provider class if found or null
     */
    private static Object findJarServiceProvider(String factoryId)
        throws ConfigurationError
    {

        String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;

        // First try the Context ClassLoader
        ClassLoader cl = SecuritySupport.getContextClassLoader();
        if (cl != null) {
            is = SecuritySupport.getResourceAsStream(cl, serviceId);

            // If no provider found then try the current ClassLoader
            if (is == null) {
                cl = FactoryFinder.class.getClassLoader();
                is = SecuritySupport.getResourceAsStream(cl, serviceId);
            }
        } else {
            // No Context ClassLoader, try the current
            // ClassLoader
            cl = FactoryFinder.class.getClassLoader();
            is = SecuritySupport.getResourceAsStream(cl, serviceId);
        }

        if (is == null) {
            // No provider found
            return null;
        }

        if (debug) debugPrintln("found jar resource=" + serviceId +
               " using ClassLoader: " + cl);

        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"), DEFAULT_LINE_LENGTH);
        } catch (java.io.UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is), DEFAULT_LINE_LENGTH);
        }
        
        String factoryClassName = null;
        try {
            // XXX Does not handle all possible input as specified by the
            // Jar Service Provider specification
            factoryClassName = rd.readLine();
        } 
        catch (IOException x) {
            // No provider found
            return null;
        }
        finally {
            try { 
                // try to close the reader. 
                rd.close(); 
            } 
            // Ignore the exception. 
            catch (IOException exc) {}
        }

        if (factoryClassName != null &&
            ! "".equals(factoryClassName)) {
            if (debug) debugPrintln("found in resource, value="
                   + factoryClassName);

            return newInstance(factoryClassName, cl);
        }

        // No provider found
        return null;
    }
    
	/**
	 * <p>Configuration Error.</p>
	 */
    static class ConfigurationError extends Error {
        
        private static final long serialVersionUID = -3644413026244211347L;
    	
    	/**
    	 * <p>Exception that caused the error.</p>
    	 */
        private Exception exception;

        /**
         * <p>Construct a new instance with the specified detail string and
         * exception.</p>
         * 
         * @param msg Detail message for this error.
         * @param x Exception that caused the error.
         */
        ConfigurationError(String msg, Exception x) {
            super(msg);
            this.exception = x;
        }

		/**
		 * <p>Get the Exception that caused the error.</p>
		 * 
		 * @return Exception that caused the error.
		 */
        Exception getException() {
            return exception;
        }
    }



    /**
     * Returns the location where the given Class is loaded from.
     * 
     * @param clazz Class to find load location.
     * 
     * @return Location where class would be loaded from.
     */
    private static String which(Class clazz) {
        try {
            String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
    
            ClassLoader loader = clazz.getClassLoader();
            
            URL it;
    
            if (loader != null) {
            	it = loader.getResource(classnameAsResource);
            } else {
            	it = ClassLoader.getSystemResource(classnameAsResource);
            } 
    
            if (it != null) {
            	return it.toString();
            } 
        } catch (Throwable t) {
            // work defensively.
            if (debug) {
            	t.printStackTrace();
            } 
        }
        return "unknown location";
    }
}
