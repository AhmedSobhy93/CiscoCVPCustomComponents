/*
   Copyright (c) 1999-2005 Audium Corporation
   All rights reserved
*/

import java.util.*;

// These classes are used by custom elements.
import com.audium.server.voiceElement.VoiceElementBase;
import com.audium.server.voiceElement.ElementInterface;
import com.audium.server.voiceElement.Setting;
import com.audium.server.voiceElement.AudioGroup;
import com.audium.server.voiceElement.ExitState;
import com.audium.server.voiceElement.ElementData;
import com.audium.server.voiceElement.ElementException;
import com.audium.server.session.VoiceElementData;
import com.audium.server.voiceElement.Dependency;

// Include these to use VFCs.
import com.audium.core.vfc.*;
import com.audium.core.vfc.util.*;

/**
 * This sample voice element is used to demonstrate how a configuration is 
 * specified for Audium Builder for Studio. It is to be used as a reference for 
 * creating complex configurations for elements. It does not produce any 
 * VoiceXML and will cause an error if included in an application. 
 * 
 * <P>This class can be compiled and put into the 
 * Studio/eclipse/plugins/com.audium.studio.common_3.4.1/classes folder for the 
 * element to appear directly in the Element Pane along with the core Audium 
 * elements. It can also be placed in the deploy/java/application/classes folder 
 * of a particular application for the element to appear within the "Local 
 * Elements" folder for that application (it will show up only when the callflow 
 * for that application is open). 
 */
public class ConfigurationExample extends VoiceElementBase implements ElementInterface 
{
    /**
     * This method is where the voice element is expected to produce the 
     * appropriate VoiceXML in the form of VFCs. The VMain object is where the
     * VFCs are to be added to, the Hashtable contains the submit arguments
     * sent by the voice browser (or null if there are none), and VoiceElementData
     * is the API with which the voice element can get its configuration and get/set
     * variables. Returning null from the method indicates the voice element is not
     * done and Audium Call Services needs to revisit it to produce the next
     * VoiceXML page. Return a non-null exit state to indicate the voice element 
     * is done. Here, we just leave the method returning "done" because the purpose
     * of this example is to show complex configurations in Audium Builder for Studio.
     */
    protected String addXmlBody(VMain vxml, Hashtable reqParameters, VoiceElementData ved)
                                throws VException, ElementException 
    {
        // PUT YOUR CODE HERE.
        
        return "done";
    }
    
    /**
     * This method returns the name the voice element will have in the 
     * Element Pane in Audium Builder for Studio.
     */
    public String getElementName()
    {
        return "ConfigMe";
    }

    /**
     * This method returns the name of the folder in which this voice
     * element resides. Return null if it is to appear directly under the 
     * Elements folder.
     */
    public String getDisplayFolderName()
    {
        return "Examples";
    }
    
    /**
     * This method returns the text of a description of the voice element
     * that will appear as a tooltip when the cursor points to the element.
     */
    public String getDescription() 
    {
        return "This voice element example details many of the different " +
               "configuration options available to elements.";
    }

    /**
     * This method returns an array of Setting objects representing all the
     * settings this voice element expects. This example tries to include all
     * manners of setting types as well as those with dependencies.
     */
    public Setting[] getSettings() throws ElementException 
    {
        Setting[] settingArray = new Setting[8];
        
        /* Create a setting using the convenience constructor. This creates
        a setting named "Input Mode" which acts as a dropdown box with
        three options, "speech", "dtmf", and "both". The advantage to using
        the convenience constructor is that its all taken care of for you. */
        settingArray[0] = new Setting(Setting.INPUTMODE);
        
        /* Create a setting that expects a string value. This setting
        will be set up to be repeatable. An example of a repeatable
        setting is a set of utterances applying to the same result. */
        settingArray[1] = new Setting("StringEntry", "Enter a String", 
                          "This setting expects a string value",
                          true,   // It is required
                          false,  // It can appear multiple times
                          true,   // It allows substitution
                          Setting.STRING);             
                           
        /* Use the same constructor to create a text field. This setting
        allows for a large amount of data to be entered.      */ 
        settingArray[2] = new Setting("TextEntry", "Enter Text Content", 
                          "This setting expects large text content",
                          false,  // It is not required
                          true,   // It appears only once
                          true,   // It allows substitution
                          Setting.TEXTFIELD);
                          
        /* Use the same constructor to create a boolean setting. This 
        makes the setting display a dropdown box with two options:
        "true" and "false". */  
        settingArray[3] = new Setting("YesNoEntry", "Ask YesNo Question", 
                          "This setting expects a true or false",
                          false,  // It is not required
                          true,   // It appears only once
                          false,  // It does not allow substitution
                          Setting.BOOLEAN);
        /* We will make this setting default to false. */
        settingArray[3].setDefaultValue("false");
                      
        /* This same constructor can be used to create settings that expect
        an integer or floating point value with no bounds. */
       
        /* This constructor is used to specify an integer setting with 
        boundaries. The minimum and maximum values can be specified. 
        Along the same lines, another constructor is used to specify
        floating point values with boundaries.*/
        settingArray[4] = new Setting("LimitedInt", "Enter Int Value", 
                          "This setting expects an integer from 1 to infinity",
                          false,  // It is not required
                          true,   // It appears only once
                          false,  // It does not allow substitution
                          new Integer(1), // Lower bound of 1
                          null);  // There is no upper bound
       
        /* This setting displays a dropdown box with a developer-specified
        list of options. */   
        settingArray[5] = new Setting("Enumeration", "Choose a Value", 
                          "This setting expects one option to be chosen",
                          true,   // It is required
                          true,   // It appears only once
                          false,  // It does not allow substitution
                          new String[] {"apple", "orange", "grape"});
        
        /* You can specify a default value to appear when a new element is
        added to the workspace. */
        settingArray[5].setDefaultValue("apple");
        
        /* Now we will create settings that depend on values entered for
        the other settings. A dependent setting will appear in the 
        settings pane only if the setting(s) it depends on have the 
        appropriate values. */
        
        /* This setting appears only if the setting named 
        "Enumeration (display name "Choose a Value") is "grape". First, create
        the setting. */
        settingArray[6] = new Setting("GrapeEntry", "Grape String", 
                          "This setting expects a string value and appears " +
                          "only if another setting is set to grape",
                          true,   // It is required
                          true,   // It appears only once
                          true,   // It allows substitution
                          Setting.STRING);
        
        /* Now we handle the dependencies. Note that the name of the parent 
        setting must be the real name, not the display name. */
        Dependency depend = new Dependency("Enumeration", "grape", Dependency.EQUAL);
        Dependency[] depArray1 = { depend };  
        settingArray[6].setDependencies(depArray1);
        
        /* This setting appears only when:
        The setting "YesNoEntry" is "true" and the input mode setting is not "both"
        or
        The setting "LimitedInt" is "2". 
        The "ands" are handled within a single Dependency object, and "ors" are
        handled by adding additional Dependency objects to the array. First, create
        the setting. */
        settingArray[7] = new Setting("VeryDependent", "Very Dependent", 
                          "This setting depends on a lot of things",
                          true,   // It is required
                          true,   // It appears only once
                          false,  // It does not allow substitution
                          Setting.STRING);
        
        /* Now we handle the dependencies. Note how we get the name of the input mode 
        setting. We do this because using an Audium shortcut constructor means you 
        don't know the name of the setting. Even if you did, this is safer since it 
        will handle name changes. */
        Dependency depend1 = new Dependency("YesNoEntry", "true", Dependency.EQUAL);
        depend1.and(settingArray[0].getRealName(), "both", Dependency.NOT_EQUAL);

        /* This is the "or" dependency, handled by a new Dependency object. Note 
        that a setting value that is a text string is not checked for 
        dependencies until you finish entering that string (either by pressing 
        <Enter> or clicking on another setting). */
        Dependency depend2 = new Dependency("LimitedInt", "2", Dependency.EQUAL);
        
        /* Both dependencies are added to an array and the setting is made dependent
        on them. */
        Dependency[] depArray2 = { depend1, depend2 };  
        settingArray[7].setDependencies(depArray2);
        
        return settingArray;
    } 
    
    /**
     * This method returns a HashMap of arrays of AudioGroup objects representing 
     * each set of audio groups this voice element expects. The keys to the HashMap
     * are the names of sets of audio groups. The arrays represent the audio groups
     * that appear in the set. Return null if the voice element does not need any 
     * audio groups.
     */
    public HashMap getAudioGroups() throws ElementException 
    {
        /* This is the HashMap to return. It has two sets of audio groups. */
        HashMap audioGroups = new HashMap(2);
        
        AudioGroup[] set1 = new AudioGroup[2];
        AudioGroup[] set2 = new AudioGroup[2];
        
        /* Create an audio group using the convenience constructor. This creates
        an audio group that is required (meaning it automatically appears in the
        audio group pane when a new element is dragged to the Workspace), and is
        used to represent the audio played when first visiting the voice element. 
        As with using the convenience contructor for settngs, using the convenience 
        constructor for audio groups has the advantage that all is taken care of 
        for you. */
        set1[0] = new AudioGroup(AudioGroup.INITIAL);
        
        /* Make a required, non-repeatable audio group. */
        set1[1] = new AudioGroup("second", "The Second Group",
                  "This is the second audio group",
                  false,  // It is not required
                  true);  // It has only one count
        
        /* Make a required repeatable audio group. */          
        set2[0] = new AudioGroup("third", "The Third Group",
                  "This is the third audio group",
                  true,    // It is required
                  false);  // It can have multiple counts
                  
        /* Audio groups can also be dependent on the value of settings. 
        Here we will create an audio group that depends on the "YesNoEntry"
        setting being "true" in order for it to appear. */
        set2[1] = new AudioGroup(AudioGroup.NOMATCH);
        
        Dependency depend = new Dependency("YesNoEntry", "true", Dependency.EQUAL);
        Dependency[] depArray = { depend };  
        set2[1].setDependencies(depArray);
        
        /* Put each set of audio groups into the HashMap with their appropriate names. */
        audioGroups.put("Set 1", set1);
        audioGroups.put("Set 2", set2);
        return audioGroups;
    }

    /**
     * This method returns an array of Strings representing the order in which
     * the audio group sets are to be displayed in Audium Builder for Studio. 
     * Return null if the voice element does not need any audio groups.
     */
    public String[] getAudioGroupDisplayOrder()
    {
        /* Determine the display order of the sets of audio groups. This order
        will be reflected in Audium Builder for Studio by the order in which the 
        sets appear in the audio group dropdown box. */
        String[] displayOrder = new String[2];
        
        displayOrder[0] = "Set 1";
        displayOrder[1] = "Set 2";
        
        return displayOrder;
    }

    /**
     * This method returns an array of ExitState objects representing all the
     * possible exit states the voice element can return. There must be at least
     * one exit state.
     */
    public ExitState[] getExitStates() throws ElementException 
    {
        ExitState[] exitStateArray = new ExitState[2];
        
        /* Create an exit state using the convenience constructor. Many elements
        have a "done" exit state, so one is provided by Audium. */
        exitStateArray[0] = new ExitState(ExitState.DONE);
        
        /* Note that Audium Builder for Studio shows the display name when right-clicking
        an element but when referring to the exit state in the element history or
        XML decisions, the real name is used. Also, Builder for Studio does not 
        show the exit state description. A future version may, so it can be useful 
        to put in the description anyway. */
        exitStateArray[1] = new ExitState("myExit", "Another Exit",
                            "This is another exit state");
                            
        /* Note that despite having dependency methods in the ExitState class,
        Audium does not support exit state dependencies. The methods exist for a 
        possible future implementation of exit states dependent on setting values. */
        
        return exitStateArray;
    }

    /**
     * This method returns an array of ElementData objects representing the
     * Element Data that this voice element creates. Return null if the voice
     * element does not create any Element Data.
     */
    public ElementData[] getElementData() throws ElementException 
    {
        ElementData[] elementDataArray = new ElementData[2];

        /* Create element data using the convenience constructor. This one
        sets element data named "value". */
        elementDataArray[0] = new ElementData(ElementData.VALUE);
        
        /* Note that Audium Builder for Studio does not show the element data 
        description. A future version may, so it can be useful to put in the 
        description anyway. Also note that there is no display name, the actual 
        name is what is displayed in the Builder (and which you use to access 
        the data in other elements). */
        elementDataArray[1] = new ElementData("myData", "My Element Data");

        return elementDataArray;
    }
}
