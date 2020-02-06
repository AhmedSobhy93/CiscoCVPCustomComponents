/*
   Copyright (c) 1999-2005 Audium Corporation
   All rights reserved
*/

import java.util.*;

// These classes are used by custom voice elements.
import com.audium.server.voiceElement.VoiceElementBase;
import com.audium.server.voiceElement.ElementInterface;
import com.audium.server.voiceElement.Setting;
import com.audium.server.voiceElement.AudioGroup;
import com.audium.server.voiceElement.ExitState;
import com.audium.server.voiceElement.ElementData;
import com.audium.server.voiceElement.ElementException;
import com.audium.server.session.VoiceElementData;
import com.audium.server.xml.VoiceElementConfig;

// Include these to use VFCs.
import com.audium.core.vfc.*;
import com.audium.core.vfc.form.*;
import com.audium.core.vfc.audio.*;
import com.audium.core.vfc.util.*;

/**
 * This class illustrates a basic voice element.  It contains two audio prompts
 * spread between two VoiceXML pages.
 */
public class DualAudioVoiceElement extends VoiceElementBase implements ElementInterface 
{
    private static final String AUDIO_GROUP_1 = "audio_group_1";
    private static final String AUDIO_GROUP_2 = "audio_group_2";
    private static final String PLAYED_AUDIO_1 = "played_audio_group_1";
    private static final String DUMMY_VAR = "dummy";

    /**
     * This method returns the name the voice element will have in the 
     * Element Pane in Audium Builder for Studio.
     */
    public String getElementName()
    {
        return "Dual_Audio";
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
        return "This voice element plays two audio groups, one after the other. " +
               "It is used as an example of how to build a voice element.";
    }

    /**
     * This voice element has no settings so a null is returned.
     */
    public Setting[] getSettings() throws ElementException 
    {
        return null;
    } 

    /**
     * This voice element has two audio groups, both in a single set.
     * The HashMap, therefore, has a single key named "Dual Audio" and in it
     * is an array of two AudioGroup objects.
     */
    public HashMap getAudioGroups() throws ElementException 
    {
        HashMap groups = new HashMap(1);
        
        /* The audio group is created by specifying its real name (the one that
        appears in the XML files, its display name (that appears in Audium 
        Builder for Studio), its description (shown in the Builder as a popup), 
        whether it is a required audio group (it is), and whether it is a single 
        audio group (meaning it does not have a count like for an event).*/
        AudioGroup[] audioGroupArray = new AudioGroup[2];
        audioGroupArray[0] = new AudioGroup("audio_group_1", "Audio Group 1", 
                                            "This audio group will be played first.", true, true);
        audioGroupArray[1] = new AudioGroup("audio_group_2", "Audio Group 2", 
                                            "This audio group will be played next.", true, true);
        
        /* Put the array into the HashMap. If there were other sets, this same
        Process would continue. */
        groups.put("Dual Audio", audioGroupArray);
        return groups;
    }
    
    /**
     * This method determines the order in which the sets are listed in the audio
     * group dropdowm menu. There is only one set of audio groups, so we return an 
     * array containing just the name of the single set. The name returned must be  
     * the same as the name given in the method above.
     */
    public String[] getAudioGroupDisplayOrder() 
    {
        String[] displayOrder = new String[1];
        displayOrder[0] = "Dual Audio";
        return displayOrder;
    }

    /**
     * This voice element has only one exit state, "done".
     */
    public ExitState[] getExitStates() throws ElementException 
    {
        ExitState[] exitStateArray = new ExitState[1];
        exitStateArray[0] = new ExitState(ExitState.DONE);
        return exitStateArray;
    }

    /**
     * This element does not create any element data.
     */
    public ElementData[] getElementData() throws ElementException 
    {
        return null;
    }

    /**
     * This is where the action takes place. This method adds the VoiceXML for
     * the two different pages it can produce.
     */
    protected String addXmlBody(VMain vxml, Hashtable reqParameters, VoiceElementData ved)
                                    throws VException, ElementException 
    {
        // Get the VoiceElementConfig object that contains the configuration
        VoiceElementConfig config = ved.getVoiceElementConfig();
        VPreference pref = ved.getPreference();
        
        // Retrieve the dummy variable from the Hashtable of request parameters
        String dummyVar = (String)reqParameters.get(DUMMY_VAR);
        if (dummyVar != null) {
            System.out.println("The dummy variable is " + dummyVar);
        }
        
        // Get info from the scratch data regarding whether audio 1 has been
        // played yet. If it is null, we know it has not been played yet.
        if (ved.getScratchData(PLAYED_AUDIO_1) == null) {
            
            // Create the vxml form to play audio 1
            playAudio(1, vxml, pref, config, ved);
            
            // Make a note in the scratch data that we played audio 1
            ved.setScratchData(PLAYED_AUDIO_1, new Boolean(true));
            
            // Return null since we're not ready to return an exit state yet
            return null;
            
        } else {
            // If audio 2 has not played yet, create the vxml form then exit.
            playAudio(2, vxml, pref, config, ved);
            
            // We know that there will be no more pages to produce after this
            // one so we set the exit state to "done" knowing that Audium Call
            // Services will handle it.
            return "done";
        }
    }

    /**
     * This is a utility method that produces the VoiceXML for playing audio.
     */
    private void playAudio(int whichAudio, VMain vxml, VPreference pref, 
                           VoiceElementConfig config, VoiceElementData ved)
                           throws VException, ElementException 
    {
        String audioGroupName;
        
        // Determine which audio audio group we're using
        if(whichAudio == 1) {
            audioGroupName = AUDIO_GROUP_1;
        } else {
            audioGroupName = AUDIO_GROUP_2;
        }

        // Get the audio group with the specified audio name
        VoiceElementConfig.AudioGroup audioGroup = config.getAudioGroup(audioGroupName, 1);

        // Get a VAudio object containing all the audio for this audio group
        VAudio audio = audioGroup.constructAudio(ved);

        // Set the dummy variable value to tell us which audio we're playing
        vxml.add(DUMMY_VAR, "playing audio #" + whichAudio, VMain.WITH_QUOTES);

        // Create an action to add the playing of the audio group to the vxml log variable.
        VAction logAction = VAction.getNew(pref, VAction.ASSIGN, VXML_LOG_VARIABLE_NAME,
                                           VXML_LOG_VARIABLE_NAME + " + '|||audio_group$$$" + 
                                           audioGroupName + "^^^' + application.getElapsedTime(" +
                                           ELEMENT_START_TIME_MILLISECS + ")", 
                                           VAction.WITHOUT_QUOTES);

        // Create a submit action to submit the dummy var back to Audium Call
        // Services. The VoiceXML log is automatically sent as well.
        VAction submitAction = getSubmitVAction(DUMMY_VAR, pref);

        // -- Below is where the form is created and added to the VoiceXML page --

        // Create a form called "start"
        VForm form = VForm.getNew(pref, "start");

        // Create an empty block
        VBlock block = VBlock.getNew(pref);

        // Add the audio and the logging and submit actions to the block
        block.add(audio);
        block.add(logAction);
        block.add(submitAction);

        // Add the block to the form
        form.add(block);

        // Add the form to the vxml page
        vxml.add(form);
    }
}