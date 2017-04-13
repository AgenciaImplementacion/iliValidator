package org.interlis2.validator;

import java.util.Iterator;

import org.interlis2.validator.gui.MainFrame;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;

/** Main program and commandline interface of ilivalidator.
 */
public class Main {

	/** name of application as shown to user.
	 */
	public static final String APP_NAME="ilivalidator";
	/** name of jar file.
	 */
	public static final String APP_JAR="ilivalidator.jar";
	/** version of application.
	 */
	private static String version=null;
	/** main program entry.
	 * @param args command line arguments.
	 */
	static public void main(String args[]){
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.SETTING_DEFAULT_ILIDIRS);
		String appHome=getAppHome();
		if(appHome!=null){
		    settings.setValue(Validator.SETTING_PLUGINFOLDER, new java.io.File(appHome,"plugins").getAbsolutePath());
		}else{
		    settings.setValue(Validator.SETTING_PLUGINFOLDER, new java.io.File("plugins").getAbsolutePath());
		}
		// arguments on export
		String xtfFile=null;
		String httpProxyHost = null;
		String httpProxyPort = null;
		if(args.length==0){
			readSettings(settings);
			MainFrame.main(xtfFile,settings);
			return;
		}
		int argi=0;
		boolean doGui=false;
		for(;argi<args.length;argi++){
			String arg=args[argi];
			if(arg.equals("--trace")){
				EhiLogger.getInstance().setTraceFilter(false); 
			}else if(arg.equals("--gui")){
				readSettings(settings);
				doGui=true;
			}else if(arg.equals("--modeldir")){
				argi++;
				settings.setValue(Validator.SETTING_ILIDIRS, args[argi]);
				continue;
			}else if(arg.equals("--config")) {
			    argi++;
			    settings.setValue(Validator.SETTING_CONFIGFILE, args[argi]);
			    continue;
			}else if(arg.equals("--forceTypeValidation")){
				argi++;
				settings.setValue(Validator.SETTING_FORCE_TYPE_VALIDATION,Validator.TRUE);
			}else if(arg.equals("--disableAreaValidation")){
				argi++;
				settings.setValue(Validator.SETTING_DISABLE_AREA_VALIDATION,Validator.TRUE);
			}else if(arg.equals("--log")) {
			    argi++;
			    settings.setValue(Validator.SETTING_LOGFILE, args[argi]);
			    continue;
			}else if(arg.equals("--xtflog")) {
			    argi++;
			    settings.setValue(Validator.SETTING_XTFLOG, args[argi]);
			    continue;
			}else if(arg.equals("--plugins")) {
			    argi++;
			    settings.setValue(Validator.SETTING_PLUGINFOLDER, args[argi]);
			    continue;
			}else if(arg.equals("--proxy")) {
				    argi++;
				    settings.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_HOST, args[argi]);
				    continue;
			}else if(arg.equals("--proxyPort")) {
				    argi++;
				    settings.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_PORT, args[argi]);
				    continue;
			}else if(arg.equals("--version")){
				printVersion();
				return;
			}else if(arg.equals("--help")){
					printVersion ();
					System.err.println();
					printDescription ();
					System.err.println();
					printUsage ();
					System.err.println();
					System.err.println("OPTIONS");
					System.err.println();
					//System.err.println("--gui                 start GUI.");
				    System.err.println("--config file         config file to control validation.");
					System.err.println("--forceTypeValidation  restrict customization of validation related to \"multiplicity\".");
					System.err.println("--disableAreaValidation  disable AREA validation.");
				    System.err.println("--log file            text file, that receives validation results.");
				    System.err.println("--xtflog file         INTERLIS transfer file, that receives validation results.");
					System.err.println("--modeldir "+settings.getValue(Validator.SETTING_ILIDIRS)+" list of directories/repositories with ili-files.");
				    System.err.println("--plugins folder      directory with jar files that contain user defined functions.");
				    System.err.println("--proxy host          proxy server to access model repositories.");
				    System.err.println("--proxyPort port      proxy port to access model repositories.");
					System.err.println("--trace               enable trace messages.");
					System.err.println("--help                Display this help text.");
					System.err.println("--version             Display the version of "+APP_NAME+".");
					System.err.println();
					return;
				
			}else if(arg.startsWith("-")){
				EhiLogger.logAdaption(arg+": unknown option; ignored");
			}else{
				break;
			}
		}
		if(doGui){
			if(argi<args.length){
				xtfFile=args[argi];
				argi++;
			}
			if(argi<args.length){
				EhiLogger.logAdaption(APP_NAME+": wrong number of arguments; ignored");
			}
			MainFrame.main(xtfFile,settings);
		}else{
			if(argi+1==args.length){
				xtfFile=args[argi];
				Validator.runValidation(xtfFile,settings);
			}else{
				EhiLogger.logError(APP_NAME+": wrong number of arguments");
				return;
			}
		}
		
	}
	/** Name of file with program settings. Only used by GUI, not used by commandline version.
	 */
	private final static String SETTINGS_FILE = System.getProperty("user.home") + "/.ilivalidator";
	/** Reads program settings.
	 * @param settings Program configuration as read from file.
	 */
	public static void readSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			if(file.exists()){
				settings.load(file);
			}
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to load settings from file "+SETTINGS_FILE,ex);
		}
	}
	/** Writes program settings.
	 * @param settings Program configuration to write.
	 */
	public static void writeSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			settings.store(file,APP_NAME+" settings");
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to settings settings to file "+SETTINGS_FILE,ex);
		}
	}
	
	/** Prints program version.
	 */
	protected static void printVersion ()
	{
	  System.err.println(APP_NAME+", Version "+getVersion());
	  System.err.println("  Developed by Eisenhut Informatik AG, CH-3400 Burgdorf");
	}

	/** Prints program description.
	 */
	protected static void printDescription ()
	{
	  System.err.println("DESCRIPTION");
	  System.err.println("  Validates an INTERLIS transfer file.");
	}

	/** Prints program usage.
	 */
	protected static void printUsage()
	{
	  System.err.println ("USAGE");
	  System.err.println("  java -jar "+APP_JAR+" [Options] in.xtf");
	}
	/** Gets version of program.
	 * @return version e.g. "1.0.0"
	 */
	public static String getVersion() {
		  if(version==null){
		java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle(ch.ehi.basics.i18n.ResourceBundle.class2qpackageName(Main.class)+".Version");
			// Major version numbers identify significant functional changes.
			// Minor version numbers identify smaller extensions to the functionality.
			// Micro versions are even finer grained versions.
			StringBuffer ret=new StringBuffer(20);
		ret.append(resVersion.getString("versionMajor"));
			ret.append('.');
		ret.append(resVersion.getString("versionMinor"));
			ret.append('.');
		ret.append(resVersion.getString("versionMicro"));
			ret.append('-');
		ret.append(resVersion.getString("versionDate"));
			version=ret.toString();
		  }
		  return version;
	}
	
	/** Gets main folder of program.
	 * 
	 * @return folder Main folder of program.
	 */
	static public String getAppHome()
	{
	  String classpath = System.getProperty("java.class.path");
	  int index = classpath.toLowerCase().indexOf(APP_JAR);
	  int start = classpath.lastIndexOf(java.io.File.pathSeparator,index) + 1;
	  if(index > start)
	  {
		  return classpath.substring(start,index - 1);
	  }
	  return null;
	}
	
}
