/*
   Copyright (c) 1999-2005 Audium Corporation
   All rights reserved
*/

import java.util.*;

import com.audium.server.voiceElement.ActionElementBase;
import com.audium.server.voiceElement.ElementInterface;
import com.audium.server.voiceElement.Setting;
import com.audium.server.voiceElement.ElementData;
import com.audium.server.voiceElement.ElementException;
import com.audium.server.session.ActionElementData;
import com.audium.server.xml.ActionElementConfig;
import com.audium.server.AudiumException;

/**
 * This sample action element is intended to simulate what an element would 
 * do to interface with a backend system such as a mainframe or database. It 
 * should be used as a template for custom confiurable action elements that
 * perform such tasks.
 * 
 * In this element, the user specifies through the configuration which of
 * three different types of systems to connect to. When executing, the action
 * element visits a method that sleeps for a predetermined amount of time,
 * essentially simulating the act of connecting to the backend system. It
 * then stores pertinent information as element and session data. The developer 
 * can easily substitute the sleep code with actual backend integration code.
 */
public class ActionElementExample extends ActionElementBase implements ElementInterface 
{
	/**
	 * This is the name of the sample action element which appears in 
	 * Audium Builder for Studio.
	 */
    public String getElementName()
    {
        return "BackendIntegration";
    }

	/**
	 * This is the name of the folder in Audium Builder for Studio in which 
	 * this sample action element appears.
	 */
    public String getDisplayFolderName()
    {
        return "Examples";
    }
    
	/**
	 * This is the description of what this action element does.
	 */
    public String getDescription() 
    {
        return "This action element is an example of one that would perform " +
        	   "some back-end integration with a mainframe, database, or other " +
        	   "system. It takes data in return and stored it in element data " +
        	   "and session data.";
    }

	/**
	 * This returns the settings used by this sample action element.
	 */
    public Setting[] getSettings() throws ElementException 
    {
    	// The two settings deal with the name of the backend system
    	// and the type of the backend system (which ostensibly determines
    	// how to perform the action).	
    		Setting[] settingArray = new Setting[2];
       	
        settingArray[0] = new Setting("SystemName", "Backend System Name", 
        				  "This is the name of the backend system to integrate with.",
        				  true,   // It is required
        				  true,   // It appears only once
        				  true,   // It allows substitution
        				  Setting.STRING);
        settingArray[1] = new Setting("SystemType", "Backend System Type", 
        				  "This is the type of backend system to integrate with",
        				  true,   // It is required
        				  true,   // It appears only once
        				  false,  // It does not allow substitution
        				  new String[] {"Mainframe", "Web Database", "Terminal"});
		settingArray[1].setDefaultValue("Web Database");

		return settingArray;
    }
    
	/**
	 * This method returns an array of ElementData objects representing the
	 * element data that this action element creates. Here, the result of the
	 * connection is stored as well as the time it took to make the connection.
	 */
    public ElementData[] getElementData() throws ElementException 
    {
        ElementData[] elementDataArray = new ElementData[2];

        elementDataArray[0] = new ElementData("result", "The result of the backend integration.");
        elementDataArray[1] = new ElementData("reactionTime", "The time it took to run the command.");

        return elementDataArray;
    }
    
    /**
     * This method performs the action. It gets the configuration settings,
     * calls the appropriate methods which perform the action depending on the 
     * type and times the action to see how long it takes. This duration and
     * the result are stored in element data, an average elapsed time is stored
     * in Session Data, and the average result is also stored in the activity log.
     */
    public void	doAction(String name, ActionElementData actionData) throws AudiumException
    {
    	System.out.println("We've entered the action element.");
    	
    	// Get the configuration
    	ActionElementConfig config = actionData.getActionElementConfig();
    	
    	// Get the name of the system to integrate with.
    	String systemName = config.getSettingValue("SystemName", actionData) ;
    	
    	// Get the type of system.
    	String systemType = config.getSettingValue("SystemType", actionData) ;
    	
    	// We want to find out how long the action took, so we collect the before time.
    	Date before = new Date();
    	
    	// Depending on the type of commection to make, we call the appropriate method.
    	String result = null;
    	if ("Mainframe".compareTo(systemType) == 0) {
    		result = doMainframe(systemName);
    	} else if ("Web Database".compareTo(systemType) == 0) {
    		result = doWebDatabase(systemName);
    	} else {
    		result = doTerminal(systemName);
    	}
    	
    	// We get the after time an figure out how many milliseconds the action took.
    	Date after = new Date();
    	long elapsedTime = after.getTime() - before.getTime();
    	
    	// We now set the result and reactionTime element data. We want the result 
    	// to end up in the log, so we use the four-argument constructor.
    	actionData.setElementData("result", result, ActionElementData.PD_STRING, true);
    	actionData.setElementData("reactionTime", Long.toString(elapsedTime));
    	
    	// The average reaction time and number of backend connections is stored in 
    	// session data as Java Integer objects. Here we calculate the new average.
    	// Note that if this element is called for the first time, there is no session
    	// data, so the method will return null. In this case we set the average and
    	// visits to 0. 
    	Object avg = actionData.getSessionData("Average Reaction");
    	int average = 0, nVisits = 0;
    	
    	if (avg != null) {
    		average = ((Integer) avg).intValue();
    		nVisits = ((Integer) actionData.getSessionData("Number Visits")).intValue();
    	}
    	int newAverage = (average * nVisits + (int) elapsedTime) / (nVisits + 1);
    	
    	// We need to put the new average and count back into session data.
    	actionData.setSessionData("Average Reaction", new Integer(newAverage));
    	actionData.setSessionData("Number Visits", new Integer(nVisits + 1));
    	
    	// When System.out statements are used, the data is printed to the application
    	// server console. This is good for debugging.
    	
    	System.out.println("Result               = \"" + result + "\".");
    	System.out.println("Reaction Time        = " + elapsedTime + " milliseconds.");
    	System.out.println("Old Average Reaction = " + average + " milliseconds.");
    	System.out.println("Old Number Visits    = " + nVisits + ".");
    	System.out.println("New Average Reaction = " + newAverage + " milliseconds.");
    	System.out.println("New Number Visits    = " + (nVisits + 1) + ".");
    	
    	// While we like printing to the console, it really is useful only during
    	// debugging. If we want something more permanent, we can store custom content
    	// to the activity log. Since sesson data is not logged, we put the average 
    	// there ourselves.
    	actionData.addToLog("New Average", Integer.toString(newAverage) + " milliseconds");
    	
    	System.out.println("We're done with the action element.");
    }
    
    /**
     * This method performs the action if the configuration determined the 
     * server type was a mainframe. It returns the result of the action. Here,
     * we simulate the action by sleeping for 100 milliseonds.
     */
    private String doMainframe(String systemName)
    {
    	System.out.println("We are making a backend connection to the mainframe.");
    	try {
    		Thread.sleep(100);
    	} catch (InterruptedException ignore) {}
    	return "Mainframe Done";
    }
    
    /**
     * This method performs the action if the configuration determined the 
     * server type was a web database. It returns the result of the action. Here,
     * we simulate the action by sleeping for 200 milliseonds.
     */
    private String doWebDatabase(String systemName) 
    {
    	System.out.println("We are making a backend connection to the web database.");
    	try {
    		Thread.sleep(200);
    	} catch (InterruptedException ignore) {}
    	return "Web Database Done";
    }
    
    /**
     * This method performs the action if the configuration determined the 
     * server type was a terminal. It returns the result of the action. Here,
     * we simulate the action by sleeping for 300 milliseonds.
     */
    private String doTerminal(String systemName) 
    {
    	System.out.println("We are making a backend connection to the terminal.");
    	try {
    		Thread.sleep(300);
    	} catch (InterruptedException ignore) {}
    	return "Terminal Done";
    }
    
}