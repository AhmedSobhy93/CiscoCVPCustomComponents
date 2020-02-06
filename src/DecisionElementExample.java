/*
   Copyright (c) 1999-2005 Audium Corporation
   All rights reserved
*/

import java.util.*;

import com.audium.server.voiceElement.DecisionElementBase;
import com.audium.server.voiceElement.ElementInterface;
import com.audium.server.voiceElement.Setting;
import com.audium.server.voiceElement.ElementData;
import com.audium.server.voiceElement.ExitState;
import com.audium.server.voiceElement.ElementException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.xml.DecisionElementConfig;

/**
 * This decision element is intended to be used as a template for a configurable
 * decision element. This decision returns whether the current time is in the
 * morning, afternoon, or evening, using a user-specified configuration to
 * determine what is the delineation time between afternoon and evening.
 */
public class DecisionElementExample extends DecisionElementBase implements ElementInterface 
{
	/**
	 * This is the name of the sample decision element which appears in 
	 * Audium Builder for Studio.
	 */
    public String getElementName()
    {
        return "SplitDay";
    }

	/**
	 * This is the name of the folder in Audium Builder for Studio in which this
	 * sample decision element appears.
	 */
    public String getDisplayFolderName()
    {
        return "Examples";
    }
    
	/**
	 * This is the description of what this decision element does.
	 */
    public String getDescription() 
    {
        return "This decision element returns either \"Morning\", " +
        	   "\"Afternoon\", or \"Evening\" depending on the current " +
        	   "time and the setting which determines how to split up the day.";
    }

	/**
	 * This returns the settings used by this sample decision element.
	 */
    public Setting[] getSettings() throws ElementException 
    {
    	// The only setting determines what the cutoff is between afternoon
    	// and evening. Any time after the cutoff will be considered evening.
    	// We make it a dropdown box to save us the effort of error checking,
    	// but this code can easily be changed to allow for more flexibility.
       	Setting[] settingArray = new Setting[1];
       	
        settingArray[0] = new Setting("EveningCutoff", "Evening Cutoff", 
        				  "This is the time after which is considered the evening.",
        				  true,   // It is required
        				  true,   // It appears only once
        				  false,  // It does not allow substitution
        				  new String[] {"5 PM", "6 PM", "7 PM", "8 PM"});
		settingArray[0].setDefaultValue("6 PM");

		return settingArray;
    }
    
	/**
	 * This method returns an array of ExitState objects representing all the
	 * possible exit states the decision element can return. Here, the exit states
	 * reflect the three possible states of the day, morning, afternoon, and evening.
	 */
    public ExitState[] getExitStates() throws ElementException 
    {
        ExitState[] exitStateArray = new ExitState[3];
        
        exitStateArray[0] = new ExitState("Morning", "Morning",
        					"This represents the morning result");
        exitStateArray[1] = new ExitState("Afternoon", "Afternoon",
        					"This represents the afternoon result");
        exitStateArray[2] = new ExitState("Evening", "Evening",
        					"This represents the evening result");
        					
        return exitStateArray;
    }
    
	/**
	 * This method returns an array of ElementData objects representing the
	 * element data that this decision element creates. Here, we store only
	 * the result of the decision.
	 */
    public ElementData[] getElementData() throws ElementException 
    {
        ElementData[] elementDataArray = new ElementData[1];

        elementDataArray[0] = new ElementData("result", "The result of the decision");

        return elementDataArray;
    }
    
    /**
     * This method makes the decision. Here it obtains the time and splits up the
     * hour of the day into three parts. The only part that is configurable is the 
     * cutoff between afternoon and evening. We get this from the configuration and
     * make the calculation accordingly.
     */
    public String doDecision(String name, DecisionElementData decisionData) throws ElementException
    {
    	System.out.println("We've entered the decision element.");
    	
    	// Get the configuration
    	DecisionElementConfig config = decisionData.getDecisionElementConfig();
    	
    	// Get the cutoff time.
    	String cutoff = config.getSettingValue("EveningCutoff", decisionData);
    	
    	// Convert that to a 24-hour number (the default is 6 PM or 18).
    	int intCutoff;
    	if ("5 PM".compareTo(cutoff) == 0) {
    		intCutoff = 17;
    	} else if ("7 PM".compareTo(cutoff) == 0) {
    		intCutoff = 19;
    	} else if ("8 PM".compareTo(cutoff) == 0) {
    		intCutoff = 20;
    	} else {
    		// Assumed to be 6 PM
    		intCutoff = 18;
    	}
    	
    	// Get the time and find out the hour of the day.
    	GregorianCalendar theTime = new GregorianCalendar();
    	int hour = theTime.get(Calendar.HOUR_OF_DAY);
    	
    	// Determine what the decision is.
    	String toReturn = null;
    	if (hour < 12) {
    		toReturn = "Morning";
    	} else if (hour > intCutoff) {
    		toReturn = "Evening";
    	} else {
    		toReturn = "Afternoon";
    	}
    	
    	// Store the decision in element data.
    	decisionData.setElementData("result", toReturn);
    	
    	System.out.println("The hour = " + hour + ".");
    	System.out.println("Result   = \"" + toReturn + "\".");
    	System.out.println("We're done with the decision element.");
    	
    	return toReturn;
    }
}