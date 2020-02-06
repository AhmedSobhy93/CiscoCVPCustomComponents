/*
   Copyright (c) 1999-2005 Audium Corporation
   All rights reserved
*/

import com.audium.server.sayitsmart.*;
import java.util.*;

/**
 * This sample Say It Smart plugin demonstrates the process of building a custom
 * Say It Smart plugin. This plugin spells a String passed to it. It can only 
 * handle either alphanumeric information or text with punctuation found on a
 * standard English keyboard. This could easily be modified to support Unicode
 * characters or other phrases containing specialized punctuation.
 *
 * The plugin is a good example of one that has multiple input formats, multiple
 * output formats, and multiple filesets.
 */
public class SpellingSayItSmart extends SayItSmartBase implements SayItSmartPlugin
{
	/* Input format real names. Here, we define an input format that handles just 
	alphanumeric input and another that handles any standard English text using 
	characters found on a standard English keyboard.*/
	
	public static final String ALPHANUM_INPUT_FORMAT = "alphanum";
	public static final String CHARACTER_INPUT_FORMAT = "all_char";
	
	/* Output format real names. Here, we define two output formats to depend on
	each input format that handle speaking the text character by character or for
	letters includin an "as in..." phrase afterwards (e.g. "a as in albert").
	Even though there are only two unique output formats, we have to make a total
	of 4 because they depend on the input formats. We could not just reuse the 
	output formats because it is still important to know which one is chosen
	when it is time to spell the phrase. They are just labeled the same. */
	
	public static final String ALPHANUM_OUTPUT_FORMAT = "alpha_char";
	public static final String ALPHANUM_AS_OUTPUT_FORMAT = "alpha_char_as";
	
	public static final String CHARACTER_OUTPUT_FORMAT = "character";
	public static final String CHARACTER_AS_OUTPUT_FORMAT = "character_as";
	
	/* Fileset real names. Here, we define two filesets for each output format
	that define spelling the phrase fast or slow. Again, since filesets are
	dependent on output formats, we have to define 8 separate filesets, even
	though two will be visible at any one time. */
	
	public static final String ALPHANUM_FAST_FILESET = "alpha_fast";
	public static final String ALPHANUM_SLOW_FILESET = "alpha_slow";
	public static final String ALPHANUM_AS_FAST_FILESET = "alpha_as_fast";
	public static final String ALPHANUM_AS_SLOW_FILESET = "alpha_as_slow";
	
	public static final String CHARACTER_FAST_FILESET = "character_fast";
	public static final String CHARACTER_SLOW_FILESET = "character_slow";
	public static final String CHARACTER_AS_FAST_FILESET = "character_as_fast";
	public static final String CHARACTER_AS_SLOW_FILESET = "character_as_slow";
	
	/* Input format, output format, and fileset display names. Note that there are
	only two unique output formats and two unique filesets, so we use the same
	display names. That way the Builder for Studio user only notices two separate 
	names. */
	
	public static final String ALPHANUM_INPUT_FORMAT_DISPLAY = "Alphanumeric";
	public static final String CHARACTER_INPUT_FORMAT_DISPLAY = "Alphanumeric w/ Punctuation";
	
	public static final String ALPHANUM_OUTPUT_FORMAT_DISPLAY = "Character-by-Character";
	public static final String ALPHANUM_AS_OUTPUT_FORMAT_DISPLAY = "Each Character w/ 'as in...'";
	
	public static final String CHARACTER_OUTPUT_FORMAT_DISPLAY = ALPHANUM_OUTPUT_FORMAT_DISPLAY;
	public static final String CHARACTER_AS_OUTPUT_FORMAT_DISPLAY = ALPHANUM_AS_OUTPUT_FORMAT_DISPLAY;
	
	public static final String ALPHANUM_FAST_FILESET_DISPLAY = "Fast Speed (Normal)";
	public static final String ALPHANUM_SLOW_FILESET_DISPLAY = "Slow Speed";
	public static final String ALPHANUM_AS_FAST_FILESET_DISPLAY = ALPHANUM_FAST_FILESET_DISPLAY;
	public static final String ALPHANUM_AS_SLOW_FILESET_DISPLAY = ALPHANUM_SLOW_FILESET_DISPLAY;
	
	public static final String CHARACTER_FAST_FILESET_DISPLAY = ALPHANUM_FAST_FILESET_DISPLAY;
	public static final String CHARACTER_SLOW_FILESET_DISPLAY = ALPHANUM_SLOW_FILESET_DISPLAY;
	public static final String CHARACTER_AS_FAST_FILESET_DISPLAY = ALPHANUM_AS_FAST_FILESET_DISPLAY;
	public static final String CHARACTER_AS_SLOW_FILESET_DISPLAY = ALPHANUM_AS_SLOW_FILESET_DISPLAY;
	
	/* Input format, output format, and fileset descriptions. */
	
	public static final String ALPHANUM_INPUT_FORMAT_DESCRIPTION = "The input data contains only alphanumeric characters, no punctuation (not even spaces or underscores).";
	public static final String CHARACTER_INPUT_FORMAT_DESCRIPTION = "The input data contains any character found in on a standard English keyboard. This is not ASCII as it covers only standard punctuation, not all 256 ASCII values.";
	
	public static final String ALPHANUM_OUTPUT_FORMAT_DESCRIPTION = "The output will be read back character by character.";
	public static final String ALPHANUM_AS_OUTPUT_FORMAT_DESCRIPTION = "the output will be read back with each character followed by 'as in...'. For example, 'a as in apple'.";
	
	public static final String CHARACTER_OUTPUT_FORMAT_DESCRIPTION = ALPHANUM_OUTPUT_FORMAT_DESCRIPTION;
	public static final String CHARACTER_AS_OUTPUT_FORMAT_DESCRIPTION = ALPHANUM_AS_OUTPUT_FORMAT_DESCRIPTION;
	
	public static final String ALPHANUM_FAST_FILESET_DESCRIPTION = "This fileset speaks the output in a normal speed.";
	public static final String ALPHANUM_SLOW_FILESET_DESCRIPTION = "This fileset speaks the output in a slow speed (good for if the caller is writing down the data).";
	public static final String ALPHANUM_AS_FAST_FILESET_DESCRIPTION = ALPHANUM_FAST_FILESET_DESCRIPTION;
	public static final String ALPHANUM_AS_SLOW_FILESET_DESCRIPTION = ALPHANUM_SLOW_FILESET_DESCRIPTION;
	
	public static final String CHARACTER_FAST_FILESET_DESCRIPTION = ALPHANUM_FAST_FILESET_DESCRIPTION;
	public static final String CHARACTER_SLOW_FILESET_DESCRIPTION = ALPHANUM_SLOW_FILESET_DESCRIPTION;
	public static final String CHARACTER_AS_FAST_FILESET_DESCRIPTION = ALPHANUM_AS_FAST_FILESET_DESCRIPTION;
	public static final String CHARACTER_AS_SLOW_FILESET_DESCRIPTION = ALPHANUM_AS_SLOW_FILESET_DESCRIPTION;
	
	/* We will create a static hashMap that contains the characters that we look 
	for. We keep a String array in each key which holds the audio file name of 
	the character, the TTS phrase to use as backup, and the TTS phrase to use 
	as backup when we use the "as in..." output format. The different audio file 
	names are figured out dynamically. */
	
	public static HashMap CHARACTERS = new HashMap();
	
	static {
	    CHARACTERS.put(new Character('1'), new String[] {"one",                 "one",                  "one"});
	    CHARACTERS.put(new Character('2'), new String[] {"two",                 "two",                  "two"});
	    CHARACTERS.put(new Character('3'), new String[] {"three",               "three",                "three"});
	    CHARACTERS.put(new Character('4'), new String[] {"four",                "four",                 "four"});
	    CHARACTERS.put(new Character('5'), new String[] {"five",                "five",                 "five"});
	    CHARACTERS.put(new Character('6'), new String[] {"six",                 "six",                  "six"});
	    CHARACTERS.put(new Character('7'), new String[] {"seven",               "seven",                "seven"});
	    CHARACTERS.put(new Character('8'), new String[] {"eight",               "eight",                "eight"});
	    CHARACTERS.put(new Character('9'), new String[] {"nine",                "nine",                 "nine"});
	    CHARACTERS.put(new Character('0'), new String[] {"zero",                "zero",                 "zero"});
	    CHARACTERS.put(new Character('a'), new String[] {"a",                   "ay",                   "ay as in albert"});
	    CHARACTERS.put(new Character('b'), new String[] {"b",                   "bee",                  "bee as in bill"});
	    CHARACTERS.put(new Character('c'), new String[] {"c",                   "cee",                  "cee as in charlie"});
	    CHARACTERS.put(new Character('d'), new String[] {"d",                   "dee",                  "dee as in david"});
	    CHARACTERS.put(new Character('e'), new String[] {"e",                   "ee",                   "ee as in edgar"});
	    CHARACTERS.put(new Character('f'), new String[] {"f",                   "ef",                   "ef as in frank"});
	    CHARACTERS.put(new Character('g'), new String[] {"g",                   "gee",                  "gee as in george"});
	    CHARACTERS.put(new Character('h'), new String[] {"h",                   "aych",                 "aych as in henry"});
	    CHARACTERS.put(new Character('i'), new String[] {"i",                   "eye",                  "eye as in iris"});
	    CHARACTERS.put(new Character('j'), new String[] {"j",                   "jay",                  "jay as in john"});
	    CHARACTERS.put(new Character('k'), new String[] {"k",                   "kay",                  "kay as in karen"});
	    CHARACTERS.put(new Character('l'), new String[] {"l",                   "ell",                  "ell as in larry"});
	    CHARACTERS.put(new Character('m'), new String[] {"m",                   "em",                   "em as in michael"});
	    CHARACTERS.put(new Character('n'), new String[] {"n",                   "en",                   "en as in nancy"});
	    CHARACTERS.put(new Character('o'), new String[] {"o",                   "oh",                   "oh as in oscar"});
	    CHARACTERS.put(new Character('p'), new String[] {"p",                   "pee",                  "pee as in paul"});
	    CHARACTERS.put(new Character('q'), new String[] {"q",                   "que",                  "que as in quentin"});
	    CHARACTERS.put(new Character('r'), new String[] {"r",                   "are",                  "are as in robert"});
	    CHARACTERS.put(new Character('s'), new String[] {"s",                   "ess",                  "ess as in sam"});
	    CHARACTERS.put(new Character('t'), new String[] {"t",                   "tee",                  "tee as in terry"});
	    CHARACTERS.put(new Character('u'), new String[] {"u",                   "you",                  "you as in ursula"});
	    CHARACTERS.put(new Character('v'), new String[] {"v",                   "vee",                  "vee as in valerie"});
	    CHARACTERS.put(new Character('w'), new String[] {"w",                   "double you",           "double you wendy"});
	    CHARACTERS.put(new Character('x'), new String[] {"x",                   "ex",                   "ex as in xray"});
	    CHARACTERS.put(new Character('y'), new String[] {"y",                   "why",                  "why as in yankee"});
	    CHARACTERS.put(new Character('z'), new String[] {"z",                   "zee",                  "zee as in zachery"});
	    CHARACTERS.put(new Character('`'), new String[] {"grave_accent",        "grave accent",         "grave accent"});
	    CHARACTERS.put(new Character('~'), new String[] {"tilde",               "tilde",                "tilde"});
	    CHARACTERS.put(new Character('!'), new String[] {"exclamation_point",   "exclamation point",    "exclamation point"});
	    CHARACTERS.put(new Character('@'), new String[] {"at_sign",             "at sign",              "at sign"});
	    CHARACTERS.put(new Character('#'), new String[] {"pound_sign",          "pound sign",           "pound sign"});
	    CHARACTERS.put(new Character('$'), new String[] {"dollar_sign",         "dollar sign",          "dollar sign"});
	    CHARACTERS.put(new Character('%'), new String[] {"percent_sign",        "percent sign",         "percent sign"});
	    CHARACTERS.put(new Character('^'), new String[] {"caret",               "caret",                "caret"});
	    CHARACTERS.put(new Character('&'), new String[] {"ampersand",           "ampersand",            "ampersand"});
	    CHARACTERS.put(new Character('*'), new String[] {"asterisk",            "asterisk",             "asterisk"});
	    CHARACTERS.put(new Character('('), new String[] {"left_parenthesis",    "left parenthesis",     "left parenthesis"});
	    CHARACTERS.put(new Character(')'), new String[] {"right_parenthesis",   "right parenthesis",    "right parenthesis"});
	    CHARACTERS.put(new Character('-'), new String[] {"dash",                "dash",                 "dash"});
	    CHARACTERS.put(new Character('_'), new String[] {"underscore",          "underscore",           "underscore"});
	    CHARACTERS.put(new Character('='), new String[] {"equal_sign",          "equal sign",           "equal sign"});
	    CHARACTERS.put(new Character('+'), new String[] {"plus_sign",           "plus sign",            "plus sign"});
	    CHARACTERS.put(new Character('['), new String[] {"left_bracket",        "left bracket",         "left bracket"});
	    CHARACTERS.put(new Character('{'), new String[] {"left_curly_brace",    "left curly brace",     "left curly brace"});
	    CHARACTERS.put(new Character(']'), new String[] {"right_bracket",       "right bracket",        "right bracket"});
	    CHARACTERS.put(new Character('}'), new String[] {"right_curly_brace",   "right curly brace",    "right curly brace"});
	    CHARACTERS.put(new Character('\\'), new String[] {"backslash",          "backslash",            "backslash"});
	    CHARACTERS.put(new Character('|'), new String[] {"veritcal_bar",        "veritcal bar",         "veritcal bar"});
	    CHARACTERS.put(new Character(';'), new String[] {"semicolon",           "semicolon",            "semicolon"});
	    CHARACTERS.put(new Character(':'), new String[] {"colon",               "colon",                "colon"});
	    CHARACTERS.put(new Character('\''), new String[] {"apostrophe",         "apostrophe",           "apostrophe"});
	    CHARACTERS.put(new Character('"'), new String[] {"double_quote",        "double quote",         "double quote"});
	    CHARACTERS.put(new Character(','), new String[] {"comma",               "comma",                "comma"});
	    CHARACTERS.put(new Character('<'), new String[] {"less_than_sign",      "less than sign",       "less than sign"});
	    CHARACTERS.put(new Character('.'), new String[] {"period",              "period",               "period"});
	    CHARACTERS.put(new Character('>'), new String[] {"greater_than_sign",   "greater than sign",    "greater than sign"});
	    CHARACTERS.put(new Character('/'), new String[] {"slash",               "slash",                "slash"});
	    CHARACTERS.put(new Character('?'), new String[] {"question_mark",       "question mark",        "question mark"});
	    CHARACTERS.put(new Character(' '), new String[] {"space",               "space",                "space"});
	    CHARACTERS.put(new Character('\t'), new String[] {"tab",                "tab",                  "tab"});
	    CHARACTERS.put(new Character('\n'), new String[] {"newline",            "new line",             "new line"});
	    CHARACTERS.put(new Character('\r'), new String[] {"carriage_return",    "carriage return",      "carriage return"});
	};
	
    /**
     * This method is used to specify the information on all parts of the plugin 
     * to Builder for Studio. We define the two input formats, 4 output formats, 
     * and 8 filesets here along with their display name and description.
     */
	public SayItSmartDisplay getDisplayInformation() throws SayItSmartException
	{
    	String description = "This Example Say It Smart plugin spells data passed to it as input.";
    	
		SayItSmartDisplay toReturn = new SayItSmartDisplay("spelling", "Spelling", description);
		toReturn.addInputFormat(ALPHANUM_INPUT_FORMAT, ALPHANUM_INPUT_FORMAT_DISPLAY, ALPHANUM_INPUT_FORMAT_DESCRIPTION);
		toReturn.addInputFormat(CHARACTER_INPUT_FORMAT, CHARACTER_INPUT_FORMAT_DISPLAY, CHARACTER_INPUT_FORMAT_DESCRIPTION);
		
		toReturn.addOutputFormat(ALPHANUM_OUTPUT_FORMAT, ALPHANUM_OUTPUT_FORMAT_DISPLAY, ALPHANUM_OUTPUT_FORMAT_DESCRIPTION);
		toReturn.addOutputFormat(ALPHANUM_AS_OUTPUT_FORMAT, ALPHANUM_AS_OUTPUT_FORMAT_DISPLAY, ALPHANUM_AS_OUTPUT_FORMAT_DESCRIPTION);
		
		toReturn.addOutputFormat(CHARACTER_OUTPUT_FORMAT, CHARACTER_OUTPUT_FORMAT_DISPLAY, CHARACTER_OUTPUT_FORMAT_DESCRIPTION);
		toReturn.addOutputFormat(CHARACTER_AS_OUTPUT_FORMAT, CHARACTER_AS_OUTPUT_FORMAT_DISPLAY, CHARACTER_AS_OUTPUT_FORMAT_DESCRIPTION);
		
		toReturn.addFileset(ALPHANUM_FAST_FILESET, ALPHANUM_FAST_FILESET_DISPLAY, ALPHANUM_FAST_FILESET_DESCRIPTION);
		toReturn.addFileset(ALPHANUM_SLOW_FILESET, ALPHANUM_SLOW_FILESET_DISPLAY, ALPHANUM_SLOW_FILESET_DESCRIPTION);
		toReturn.addFileset(ALPHANUM_AS_FAST_FILESET, ALPHANUM_AS_FAST_FILESET_DISPLAY, ALPHANUM_AS_FAST_FILESET_DESCRIPTION);
		toReturn.addFileset(ALPHANUM_AS_SLOW_FILESET, ALPHANUM_AS_SLOW_FILESET_DISPLAY, ALPHANUM_AS_SLOW_FILESET_DESCRIPTION);
    
		toReturn.addFileset(CHARACTER_FAST_FILESET, CHARACTER_FAST_FILESET_DISPLAY, CHARACTER_FAST_FILESET_DESCRIPTION);
		toReturn.addFileset(CHARACTER_SLOW_FILESET, CHARACTER_SLOW_FILESET_DISPLAY, CHARACTER_SLOW_FILESET_DESCRIPTION);
		toReturn.addFileset(CHARACTER_AS_FAST_FILESET, CHARACTER_AS_FAST_FILESET_DISPLAY, CHARACTER_AS_FAST_FILESET_DESCRIPTION);
		toReturn.addFileset(CHARACTER_AS_SLOW_FILESET, CHARACTER_AS_SLOW_FILESET_DISPLAY, CHARACTER_AS_SLOW_FILESET_DESCRIPTION);
    
		return toReturn;
    }
    
    /**
     * Here, we handle the input and output format dependencies. The 
     * alphanumeric input formats has two output formats for handling 
     * character-by-character playback as well as using "as in..." playback. 
     * The all character input format has its onw two output formats that 
     * handle the same idea.
     */
    public SayItSmartDependency getFormatDependencies() throws SayItSmartException
    {
        SayItSmartDependency toReturn = new SayItSmartDependency(ALPHANUM_INPUT_FORMAT, new String[] {ALPHANUM_OUTPUT_FORMAT, ALPHANUM_AS_OUTPUT_FORMAT});
        toReturn.addParent(CHARACTER_INPUT_FORMAT, new String[] {CHARACTER_OUTPUT_FORMAT, CHARACTER_AS_OUTPUT_FORMAT});
        return toReturn;
    }
    
    /**
     * Here, we handle the output format and filset dependencies. each output 
     * format has two filsets that handle fast (or normal) and slow playback. 
     * We'll simply record the audio with the person speaking slower.
     */
    public SayItSmartDependency getFilesetDependencies() throws SayItSmartException
    {
        SayItSmartDependency toReturn = new SayItSmartDependency(ALPHANUM_OUTPUT_FORMAT, new String[] {ALPHANUM_FAST_FILESET, ALPHANUM_SLOW_FILESET});
        toReturn.addParent(ALPHANUM_AS_OUTPUT_FORMAT, new String[] {ALPHANUM_AS_FAST_FILESET, ALPHANUM_AS_SLOW_FILESET});
        toReturn.addParent(CHARACTER_OUTPUT_FORMAT, new String[] {CHARACTER_FAST_FILESET, CHARACTER_SLOW_FILESET});
        toReturn.addParent(CHARACTER_AS_OUTPUT_FORMAT, new String[] {CHARACTER_AS_FAST_FILESET, CHARACTER_AS_SLOW_FILESET});
        return toReturn;
    }
    
    /**
     * This method actually does the spelling. It converts the data into a list 
     * of audio files with TTS backups all encapsulated into a single 
     * SayItSmartContent class. Errors are thrown if the data is incorrect or 
     * the input format, output format, or filset is invalid.
     */
    public SayItSmartContent convertToFiles(Object dataAsObject, String inputFormat, String outputFormat, String fileset) throws SayItSmartException
    {        
        /* This is a utility class defined in the SayItSmartBase class that 
        checks if the data, input format, output format, and fileset all conform 
        to the expected vaues. If not, it throws an exception that propagates 
        its way out. */
        validateArguments(dataAsObject, inputFormat, outputFormat, fileset);
        
        /* Even though Say It Smart plugins have the capability to accept any 
        Java Object, we are only interested in Strings, so we cast to a String 
        accordingly. We also convert the string to a lowercase because upper 
        and lowercase letters are spelled the same and we don't want to have to 
        add separate options in the HashMap for both. */
        String data = dataAsObject.toString().toLowerCase();
        
        SayItSmartContent toReturn = new SayItSmartContent();
        String filsetPostfix;
        int index;
        boolean asInOn;
        
        /* We make sure the output format is handled correctly. If we are using 
        the alphanumeric one, we set the index of the String array to 1, which 
        contains the TTS backup. If it is the "as in..." output format, we use 
        index 2. */
        if (outputFormat.compareTo(ALPHANUM_OUTPUT_FORMAT) == 0 || outputFormat.compareTo(CHARACTER_OUTPUT_FORMAT) == 0) {
            index = 1;
            asInOn = false;
        } else {
            index = 2;
            asInOn = true;
        }
        
        /* The filenames required have either "_slow" or "_fast" appended to 
        them depending on the filset used. We set up this String to append to 
        the filename. */
        if (fileset.compareTo(ALPHANUM_SLOW_FILESET) == 0 || fileset.compareTo(ALPHANUM_AS_SLOW_FILESET) == 0 ||
            fileset.compareTo(CHARACTER_SLOW_FILESET) == 0 || fileset.compareTo(CHARACTER_AS_SLOW_FILESET) == 0) {
            filsetPostfix = "_slow";
        } else {
            filsetPostfix = "_fast";
        }
        
        /* Go through each character of the string and spell it. If it is a 
        letter and the "as in..." output format is set, we append "_as_in" to 
        the filename. */
        String outputPostfix = null;
        for (int i = 0; i < data.length(); i++) {
            char theChar = data.charAt(i);
            if (Character.isLetter(theChar) && asInOn) {
                outputPostfix = "_as_in";
            } else {
                outputPostfix = "";
            }
            
            /* If we could not find the character in the HashMap, then it is not 
            a character we find on a standard Engligh keyboard, so we throw an 
            exception. */
            Object wordsAsObject = CHARACTERS.get(new Character(theChar));
            if (wordsAsObject == null) {
                throw new SayItSmartException("Say It Smart Spelling Error - The input data \"" + data + 
                                              "\" must contain only characters found on a standard " +
                                              "English keyboard in order to work with the \"" + inputFormat + 
                                              "\" input format.");
            }
            
            /* Now we add to the SayItSmartContent object we intend on 
            returning. We create the filename by first taking the part listed 
            in the HashMap, appending the "as in..." filename part (if 
            applicable, then appending the fast or slow part". The TTS backup 
            is found in the HashMap with the index set up at the start of the 
            method. If we are expecting only alphanumeric characters and we get 
            data with punctuation, we need to throw an exception. */
            if (inputFormat.compareTo(CHARACTER_INPUT_FORMAT) == 0) {
                String[] words = (String[]) wordsAsObject;
                toReturn.add(words[0] + outputPostfix + filsetPostfix, words[index], false);
            } else if (Character.isLetterOrDigit(theChar)) {
                String[] words = (String[]) CHARACTERS.get(new Character(theChar));
                toReturn.add(words[0] + outputPostfix + filsetPostfix, words[index], false);
            } else {
                throw new SayItSmartException("Say It Smart Spelling Error - The input data \"" + data + 
                                              "\" must contain only letters and numbers in order " +
                                              "work with the \"" + inputFormat + "\" input format.");
            }
        }
    	
    	return toReturn;
    }
    
    /**
     * This method returns what files need to be recorded for each filset. At 
     * this stage, this method is not used by Builder for Studio, but should be 
     * provided for future compatibility. The idea would be to call this method 
     * to learn what needs to be recorded. This could be combined with a list 
     * of all the audio files required by the application to create a script 
     * that can be given to a voice actor to record.
     */
    public String[] getFilesetContents(String fileset) throws SayItSmartException
    {
        if (fileset.compareTo(ALPHANUM_FAST_FILESET) == 0) {
            return new String[] {"one_fast", "two_fast", "three_fast", "four_fast", "five_fast", "six_fast", 
                                 "seven_fast", "eight_fast", "nine_fast", "zero_fast", 
                                 "a_fast", "b_fast", "c_fast", "d_fast", "e_fast", "f_fast", 
                                 "g_fast", "h_fast", "i_fast", "j_fast", "k_fast", "l_fast", 
                                 "m_fast", "n_fast", "o_fast", "p_fast", "q_fast", "r_fast", 
                                 "s_fast", "t_fast", "u_fast", "v_fast", "w_fast", "x_fast", 
                                 "y_fast", "z_fast"};
        } else if (fileset.compareTo(ALPHANUM_SLOW_FILESET) == 0) {
            return new String[] {"one_slow", "two_slow", "three_slow", "four_slow", "five_slow", "six_slow", 
                                 "seven_slow", "eight_slow", "nine_slow", "zero_slow", 
                                 "a_slow", "b_slow", "c_slow", "d_slow", "e_slow", "f_slow", 
                                 "g_slow", "h_slow", "i_slow", "j_slow", "k_slow", "l_slow", 
                                 "m_slow", "n_slow", "o_slow", "p_slow", "q_slow", "r_slow", 
                                 "s_slow", "t_slow", "u_slow", "v_slow", "w_slow", "x_slow", 
                                 "y_slow", "z_slow"};
        } else if (fileset.compareTo(ALPHANUM_AS_FAST_FILESET) == 0) {
            return new String[] {"one_fast", "two_fast", "three_fast", "four_fast", "five_fast", "six_fast", 
                                 "seven_fast", "eight_fast", "nine_fast", "zero_fast", 
                                 "a_as_in_fast", "b_as_in_fast", "c_as_in_fast", "d_as_in_fast", "e_as_in_fast", 
                                 "f_as_in_fast", "g_as_in_fast", "h_as_in_fast", "i_as_in_fast", "j_as_in_fast", 
                                 "k_as_in_fast", "l_as_in_fast", "m_as_in_fast", "n_as_in_fast", "o_as_in_fast", 
                                 "p_as_in_fast", "q_as_in_fast", "r_as_in_fast", "s_as_in_fast", "t_as_in_fast", 
                                 "u_as_in_fast", "v_as_in_fast", "w_as_in_fast", "x_as_in_fast", "y_as_in_fast", 
                                 "z_as_in_fast"};
        } else if (fileset.compareTo(ALPHANUM_AS_SLOW_FILESET) == 0) {
            return new String[] {"one_slow", "two_slow", "three_slow", "four_slow", "five_slow", "six_slow", 
                                 "seven_slow", "eight_slow", "nine_slow", "zero_slow", 
                                 "a_as_in_slow", "b_as_in_slow", "c_as_in_slow", "d_as_in_slow", "e_as_in_slow", 
                                 "f_as_in_slow", "g_as_in_slow", "h_as_in_slow", "i_as_in_slow", "j_as_in_slow", 
                                 "k_as_in_slow", "l_as_in_slow", "m_as_in_slow", "n_as_in_slow", "o_as_in_slow", 
                                 "p_as_in_slow", "q_as_in_slow", "r_as_in_slow", "s_as_in_slow", "t_as_in_slow", 
                                 "u_as_in_slow", "v_as_in_slow", "w_as_in_slow", "x_as_in_slow", "y_as_in_slow", 
                                 "z_as_in_slow"};
        } else if (fileset.compareTo(CHARACTER_FAST_FILESET) == 0) {
            return new String[] {"one_fast", "two_fast", "three_fast", "four_fast", "five_fast", "six_fast", 
                                 "seven_fast", "eight_fast", "nine_fast", "zero_fast", 
                                 "a_fast", "b_fast", "c_fast", "d_fast", "e_fast", "f_fast", 
                                 "g_fast", "h_fast", "i_fast", "j_fast", "k_fast", "l_fast", 
                                 "m_fast", "n_fast", "o_fast", "p_fast", "q_fast", "r_fast", 
                                 "s_fast", "t_fast", "u_fast", "v_fast", "w_fast", "x_fast", 
                                 "y_fast", "z_fast", 
                                 "grave_accent_fast", "tilde_fast", "exclamation_point_fast", "at_sign_fast", 
                                 "pound_sign_fast", "dollar_sign_fast", "percent_sign_fast", "caret_fast", 
                                 "ampersand_fast", "asterisk_fast", "left_parenthesis_fast", 
                                 "right_parenthesis_fast", "dash_fast", "underscore_fast", "equal_sign_fast", 
                                 "plus_sign_fast", "left_bracket_fast", "left_curly_brace_fast", 
                                 "right_bracket_fast", "right_curly_brace_fast", "backslash_fast", 
                                 "veritcal_bar_fast", "semicolon_fast", "colon_fast", "apostrophe_fast", 
                                 "double_quote_fast", "comma_fast", "less_than_sign_fast",  "period_fast", 
                                 "greater_than_sign_fast", "slash_fast", "question_mark_fast"};
        } else if (fileset.compareTo(CHARACTER_SLOW_FILESET) == 0) {
            return new String[] {"one_slow", "two_slow", "three_slow", "four_slow", "five_slow", "six_slow", 
                                 "seven_slow", "eight_slow", "nine_slow", "zero_slow", 
                                 "a_slow", "b_slow", "c_slow", "d_slow", "e_slow", "f_slow", 
                                 "g_slow", "h_slow", "i_slow", "j_slow", "k_slow", "l_slow", 
                                 "m_slow", "n_slow", "o_slow", "p_slow", "q_slow", "r_slow", 
                                 "s_slow", "t_slow", "u_slow", "v_slow", "w_slow", "x_slow", 
                                 "y_slow", "z_slow", 
                                 "grave_accent_slow", "tilde_slow", "exclamation_point_slow", "at_sign_slow", 
                                 "pound_sign_slow", "dollar_sign_slow", "percent_sign_slow", "caret_slow", 
                                 "ampersand_slow", "asterisk_slow", "left_parenthesis_slow", 
                                 "right_parenthesis_slow", "dash_slow", "underscore_slow", "equal_sign_slow", 
                                 "plus_sign_slow", "left_bracket_slow", "left_curly_brace_slow", 
                                 "right_bracket_slow", "right_curly_brace_slow", "backslash_slow", 
                                 "veritcal_bar_slow", "semicolon_slow", "colon_slow", "apostrophe_slow", 
                                 "double_quote_slow", "comma_slow", "less_than_sign_slow",  "period_slow", 
                                 "greater_than_sign_slow", "slash_slow", "question_mark_slow"};
        } else if (fileset.compareTo(CHARACTER_AS_FAST_FILESET) == 0) {
            return new String[] {"one_fast", "two_fast", "three_fast", "four_fast", "five_fast", "six_fast", 
                                 "seven_fast", "eight_fast", "nine_fast", "zero_fast", 
                                 "a_as_in_fast", "b_as_in_fast", "c_as_in_fast", "d_as_in_fast", "e_as_in_fast", 
                                 "f_as_in_fast", "g_as_in_fast", "h_as_in_fast", "i_as_in_fast", "j_as_in_fast", 
                                 "k_as_in_fast", "l_as_in_fast", "m_as_in_fast", "n_as_in_fast", "o_as_in_fast", 
                                 "p_as_in_fast", "q_as_in_fast", "r_as_in_fast", "s_as_in_fast", "t_as_in_fast", 
                                 "u_as_in_fast", "v_as_in_fast", "w_as_in_fast", "x_as_in_fast", "y_as_in_fast", 
                                 "z_as_in_fast", 
                                 "grave_accent_fast", "tilde_fast", "exclamation_point_fast", "at_sign_fast", 
                                 "pound_sign_fast", "dollar_sign_fast", "percent_sign_fast", "caret_fast", 
                                 "ampersand_fast", "asterisk_fast", "left_parenthesis_fast", 
                                 "right_parenthesis_fast", "dash_fast", "underscore_fast", "equal_sign_fast", 
                                 "plus_sign_fast", "left_bracket_fast", "left_curly_brace_fast", 
                                 "right_bracket_fast", "right_curly_brace_fast", "backslash_fast", 
                                 "veritcal_bar_fast", "semicolon_fast", "colon_fast", "apostrophe_fast", 
                                 "double_quote_fast", "comma_fast", "less_than_sign_fast",  "period_fast", 
                                 "greater_than_sign_fast", "slash_fast", "question_mark_fast"};
        } else if (fileset.compareTo(CHARACTER_AS_SLOW_FILESET) == 0) {
            return new String[] {"one_slow", "two_slow", "three_slow", "four_slow", "five_slow", "six_slow", 
                                 "seven_slow", "eight_slow", "nine_slow", "zero_slow", 
                                 "a_as_in_slow", "b_as_in_slow", "c_as_in_slow", "d_as_in_slow", "e_as_in_slow", 
                                 "f_as_in_slow", "g_as_in_slow", "h_as_in_slow", "i_as_in_slow", "j_as_in_slow", 
                                 "k_as_in_slow", "l_as_in_slow", "m_as_in_slow", "n_as_in_slow", "o_as_in_slow", 
                                 "p_as_in_slow", "q_as_in_slow", "r_as_in_slow", "s_as_in_slow", "t_as_in_slow", 
                                 "u_as_in_slow", "v_as_in_slow", "w_as_in_slow", "x_as_in_slow", "y_as_in_slow", 
                                 "z_as_in_slow", 
                                 "grave_accent_slow", "tilde_slow", "exclamation_point_slow", "at_sign_slow", 
                                 "pound_sign_slow", "dollar_sign_slow", "percent_sign_slow", "caret_slow", 
                                 "ampersand_slow", "asterisk_slow", "left_parenthesis_slow", 
                                 "right_parenthesis_slow", "dash_slow", "underscore_slow", "equal_sign_slow", 
                                 "plus_sign_slow", "left_bracket_slow", "left_curly_brace_slow", 
                                 "right_bracket_slow", "right_curly_brace_slow", "backslash_slow", 
                                 "veritcal_bar_slow", "semicolon_slow", "colon_slow", "apostrophe_slow", 
                                 "double_quote_slow", "comma_slow", "less_than_sign_slow",  "period_slow", 
                                 "greater_than_sign_slow", "slash_slow", "question_mark_slow"};
        } else {
            throw new SayItSmartException("Say It Smart Spelling Error - The fileset \"" + fileset + 
                                          "\" is not supported");
        }
            
    }
    
    /**
     * This method is provided to show some use cases for the spelling Say It 
     * Smart plugin. It lists various inputs and displays what the result is and 
     * also shows situations that cause errors. The errors are caught and the 
     * error message displayed. Note that this method actually makes an instance 
     * of the plugin and calls the convertToFiles() method. Also note that we 
     * are printing out the SayItSmartContent that we receive back from this 
     * method. As a convenience, this class implements the toString() method to 
     * print out the result. It puts the filename in quotes followed by the TTS 
     * backup in parenthesis. 
     */
    public static void main(String[] args) throws SayItSmartException
    {
    	SpellingSayItSmart spell = new SpellingSayItSmart();
    	
    	System.out.println("\nData: abc123            Input Format: alphanum      Output Format: alpha_char       Fileset: alpha_fast         \nResult: "+spell.convertToFiles("abc123", ALPHANUM_INPUT_FORMAT, ALPHANUM_OUTPUT_FORMAT, ALPHANUM_FAST_FILESET));
    	System.out.println("\nData: Audiumcorp        Input Format: alphanum      Output Format: alpha_char       Fileset: alpha_slow         \nResult: "+spell.convertToFiles("Audiumcorp", ALPHANUM_INPUT_FORMAT, ALPHANUM_OUTPUT_FORMAT, ALPHANUM_SLOW_FILESET));
    	System.out.println("\nData: r                 Input Format: alphanum      Output Format: alpha_char_as    Fileset: alpha_fast         \nResult: "+spell.convertToFiles("r", ALPHANUM_INPUT_FORMAT, ALPHANUM_AS_OUTPUT_FORMAT, ALPHANUM_AS_FAST_FILESET));
    	System.out.println("\nData: gr8               Input Format: alphanum      Output Format: alpha_char_as    Fileset: alpha_slow         \nResult: "+spell.convertToFiles("gr8", ALPHANUM_INPUT_FORMAT, ALPHANUM_AS_OUTPUT_FORMAT, ALPHANUM_AS_SLOW_FILESET));
    	System.out.println("\nData: Audiumcorp.com    Input Format: all_char      Output Format: character        Fileset: character_fast     \nResult: "+spell.convertToFiles("Audiumcorp.com", CHARACTER_INPUT_FORMAT, CHARACTER_OUTPUT_FORMAT, CHARACTER_FAST_FILESET));
    	System.out.println("\nData: <VoiceXML2.0>     Input Format: all_char      Output Format: character        Fileset: character_slow     \nResult: "+spell.convertToFiles("<VoiceXML2.0>", CHARACTER_INPUT_FORMAT, CHARACTER_OUTPUT_FORMAT, CHARACTER_SLOW_FILESET));
    	System.out.println("\nData: 3%i($;/sh?*       Input Format: all_char      Output Format: character_as     Fileset: character_as_fast  \nResult: "+spell.convertToFiles("3%i($;/sh?*", CHARACTER_INPUT_FORMAT, CHARACTER_AS_OUTPUT_FORMAT, CHARACTER_AS_FAST_FILESET));
    	System.out.println("\nData: my sample code    Input Format: all_char      Output Format: character_as     Fileset: character_as_slow  \nResult: "+spell.convertToFiles("my sample code", CHARACTER_INPUT_FORMAT, CHARACTER_AS_OUTPUT_FORMAT, CHARACTER_AS_SLOW_FILESET));
		
    	// These will cause errors. We catch them and print out the error message.
    	try {
    	    System.out.println("\nData: abc 123         Input Format: alphanum        Output Format: alpha_char          Fileset: alpha_fast     \nResult:"+spell.convertToFiles("abc 123", ALPHANUM_INPUT_FORMAT, ALPHANUM_OUTPUT_FORMAT, ALPHANUM_FAST_FILESET));
    	} catch (SayItSmartException e) {
    	    System.out.println("\nData: abc 123         Input Format: alphanum        Output Format: alpha_char          Fileset: alpha_fast     \nThis caused an error: " + e);
    	}
    	
    	try {
    	    System.out.println("\nData: abc123          Input Format: alphanumeric    Output Format: alpha_char          Fileset: character_slow \nResult:"+spell.convertToFiles("abc123", "alphanumeric", ALPHANUM_OUTPUT_FORMAT, CHARACTER_AS_SLOW_FILESET));
    	} catch (SayItSmartException e) {
    	    System.out.println("\nData: abc123          Input Format: alphanumeric    Output Format: alpha_char          Fileset: character_slow \nThis caused an error: " + e);
    	}
    	
    	try {
    	    System.out.println("\nData: abc123          Input Format: alphanum        Output Format: character           Fileset: alpha_fast     \nResult:"+spell.convertToFiles("abc123", ALPHANUM_INPUT_FORMAT, CHARACTER_OUTPUT_FORMAT, ALPHANUM_FAST_FILESET));
    	} catch (SayItSmartException e) {
    	    System.out.println("\nData: abc123          Input Format: alphanum        Output Format: character           Fileset: alpha_fast     \nThis caused an error: " + e);
    	}
    	
    	try {
    	    System.out.println("\nData: abc123          Input Format: alphanum        Output Format: alpha_char          Fileset: character_as_slow\nResult:"+spell.convertToFiles("abc123", ALPHANUM_INPUT_FORMAT, ALPHANUM_OUTPUT_FORMAT, CHARACTER_AS_SLOW_FILESET));
    	} catch (SayItSmartException e) {
    	    System.out.println("\nData: abc123          Input Format: alphanum        Output Format: alpha_char          Fileset: character_as_slow\nThis caused an error: " + e);
    	}
    	
    	try {
    	    System.out.println("\nData: null            Input Format: all_char        Output Format: character           Fileset: character_fast  \nResult:"+spell.convertToFiles(null, CHARACTER_INPUT_FORMAT, CHARACTER_OUTPUT_FORMAT, CHARACTER_FAST_FILESET));
    	} catch (SayItSmartException e) {
    	    System.out.println("\nData: null            Input Format: all_char        Output Format: character           Fileset: character_fast  \nThis caused an error: " + e);
    	}
    	
    	try {
    	    System.out.println("\nData: 39¢             Input Format: all_char        Output Format: character           Fileset: character_fast  \nResult:"+spell.convertToFiles("39¢", CHARACTER_INPUT_FORMAT, CHARACTER_OUTPUT_FORMAT, CHARACTER_FAST_FILESET));
    	} catch (SayItSmartException e) {
    	    System.out.println("\nData: 39¢             Input Format: all_char        Output Format: character           Fileset: character_fast  \nThis caused an error: " + e);
    	}
	}
}
