/*   Copyright (c) 1999-2005 Audium Corporation   All rights reserved*/import com.audium.sayitsmart.plugins.AudiumSayItSmartDate;import com.audium.server.sayitsmart.*;import java.util.*;/** * This Say It Smart plugin is designed to demonstrate the process by which one  * can create a Say It Smart plugin that expands an existing Say It Smart  * plugin. The advantage of this is that with not a lot of extra code, a  * developer can add to existing plugins that show up merged in Audium Builder  * for Studio without having to access the code of the base plugin. In this  * manner developers can add input formats, output formats, and filesets. They  * can even change some of the input parameters to the base plugin.  * Additionally, with Say It Smart plugins being simple Java classes, a plugin  * need not actually extend the Say It Smart class it needs it can simply  * instantiate it just like any other class. * * In this example, a new Say It Smart plugin is created that adds an input  * format to the date type, which is handled by the AudiumSayItSmartDate plugin.  * The input format supports the use a Java Calendar object passed as input  * rather than a String. All it does is create a plugin that acts as if it has  * a single input format but with the same real name as the date plugin. This  * is important because then Audium Builder for Studio will "merge" the existing  * AudiumSayItSmartDate plugin with this one, making them seem as one. All this  * plugin need deal with is handling the new input format. When it handles that,  * all it actually does is convert the data appropriately and create and  * instance of AudiumSayItSmartDate to actually do the work. */public class NewDateInputFormat extends SayItSmartBase implements SayItSmartPlugin{    /* We define the constants holding the real name, display name, and     description of the new input format. */    public static final String JAVA_DATE_INPUT_FORMAT = 	            "java_date";    public static final String JAVA_DATE_INPUT_FORMAT_DISPLAY = 	    "Java Calendar Object";    public static final String JAVA_DATE_INPUT_FORMAT_DESCRIPTION = 	"This input format supports a Java Calendar object passed as input versus a String containign the date.";        /* We create an instance of AudiumSayItSmartDate because we need it to     access information and to actually take care of most of the work for us. */    public AudiumSayItSmartDate datePlugin = new AudiumSayItSmartDate();        /**     * This method only defines this plugin's information, which is the new      * input format and output formats and filesets already defined in the      * AudiumSayItSmartDate plugin. We use the instance of AudiumSayItSmartDate      * to return the information for these output formats and filesets. It is      * important that we give this plugin the same real name (and display name      * too) as the AudiumSayItSmartDate plugin type's real name otherwise the      * Builder will not merge the two plugins. We still have to define an output      * format and filesets even though we are just reusing the ones from      * AudiumSayItSmartDate because a plugin must define the dependencies      * correctly.     */	public SayItSmartDisplay getDisplayInformation() throws SayItSmartException	{		SayItSmartDisplay toReturn = new SayItSmartDisplay("date", "Date", "This plugin adds to date with a new input format");        toReturn.addInputFormat(JAVA_DATE_INPUT_FORMAT,                                 JAVA_DATE_INPUT_FORMAT_DISPLAY,                                 JAVA_DATE_INPUT_FORMAT_DESCRIPTION);        toReturn.addOutputFormat(datePlugin.STANDARD_OUTPUT_FORMAT,                                  datePlugin.STANDARD_OUTPUT_FORMAT_DISPLAY,                                  datePlugin.STANDARD_OUTPUT_FORMAT_DESCRIPTION);        toReturn.addFileset(datePlugin.FULL_DATE_FILESET_STANDARD,                             datePlugin.FULL_DATE_FILESET_STANDARD_DISPLAY,                             datePlugin.FULL_DATE_FILESET_STANDARD_DESCRIPTION);        toReturn.addFileset(datePlugin.FULL_DATE_FILESET_ENHANCED,                             datePlugin.FULL_DATE_FILESET_ENHANCED_DISPLAY,                             datePlugin.FULL_DATE_FILESET_ENHANCED_DESCRIPTION);        return toReturn;    }        /**     * Here, we simply report the dependencies between the new input format and      * the output format.     */    public SayItSmartDependency getFormatDependencies() throws SayItSmartException    {        return new SayItSmartDependency(JAVA_DATE_INPUT_FORMAT, datePlugin.STANDARD_OUTPUT_FORMAT);    }            /**     * Here, we simply report the dependencies between the output format and the     * filesets. We get this information straight from how the      * AudiumSayItSmartDate defines them.     */    public SayItSmartDependency getFilesetDependencies() throws SayItSmartException    {        return new SayItSmartDependency(datePlugin.STANDARD_OUTPUT_FORMAT, new String[] {                                        datePlugin.FULL_DATE_FILESET_STANDARD,                                         datePlugin.FULL_DATE_FILESET_ENHANCED});    }         /**     * The filesets have not changed, all we did is change how the input comes      * in as. Therefore, we simply return whatever the AudiumSayItSmartDate      * object returns.     */    public String[] getFilesetContents(String fileset) throws SayItSmartException    {    	return datePlugin.getFilesetContents(fileset);    }        /**     * All we need to do is convert the Calendar object to a String after which      * point we call the convertToFiles method of our AudiumSayItSmartDate      * object to do the work. Its as simple as that!     */    public SayItSmartContent convertToFiles(Object dataAsObject, String inputFormat, String outputFormat, String fileset) throws SayItSmartException    {        /* Convert Object to Calendar. if there is a ClassCastException, we         throw an exception because it is not a Calendar object. */        Calendar theDate = null;        try {            theDate = (Calendar) dataAsObject;        } catch (ClassCastException e) {            throw new SayItSmartException("SayItSmartDate addon Error - The Java object passed as input for the input format \"" +                                          JAVA_DATE_INPUT_FORMAT_DISPLAY + "\" is not a Calendar object.");        }                /* We get the components of the date. Note that the month is zero based         so we add 1. */        String dateAsString = "";        int month = theDate.get(Calendar.MONTH) + 1;        int day = theDate.get(Calendar.DAY_OF_MONTH);        int year = theDate.get(Calendar.YEAR);                /* We have to pad with zeros if any of the components are not long         enough. */        if (month < 10) {            dateAsString += "0";        }        dateAsString += month;                if (day < 10) {            dateAsString += "0";        }        dateAsString += day;                if (year < 10) {            dateAsString += "000";        } else if (year < 100) {            dateAsString += "00";        } else if (year < 1000) {            dateAsString += "0";        } else if (year > 9999) {            year = 9999;        }        dateAsString += year;                /* Now that we've converted, we let the super class' method do all the         work. */        return datePlugin.convertToFiles(dateAsString, "mmddyyyy", outputFormat, fileset);    }        /**     * This method shows some possible calendar objects that can be passed and      * what they return.     */    public static void main(String[] args) throws SayItSmartException    {        NewDateInputFormat newPlugin = new NewDateInputFormat();        AudiumSayItSmartDate datePlugin = new AudiumSayItSmartDate();                GregorianCalendar current = new GregorianCalendar();        System.out.println("\nData: The Present Date\nResult = " +         	newPlugin.convertToFiles(current, NewDateInputFormat.JAVA_DATE_INPUT_FORMAT,         							 datePlugin.STANDARD_OUTPUT_FORMAT,         							 datePlugin.FULL_DATE_FILESET_STANDARD));                GregorianCalendar aDate = new GregorianCalendar(954, 0, 1);        System.out.println("\nData: 01010954\nResult = " +         	newPlugin.convertToFiles(aDate, NewDateInputFormat.JAVA_DATE_INPUT_FORMAT,         							 datePlugin.STANDARD_OUTPUT_FORMAT,         							 datePlugin.FULL_DATE_FILESET_STANDARD));                        aDate = new GregorianCalendar(1999, 11, 31);        System.out.println("\nData: 12311999\nResult = " +         	newPlugin.convertToFiles(aDate, NewDateInputFormat.JAVA_DATE_INPUT_FORMAT,         							 datePlugin.STANDARD_OUTPUT_FORMAT,         							 datePlugin.FULL_DATE_FILESET_STANDARD));                aDate = new GregorianCalendar(38464, 3, 12);        System.out.println("\nData: 04129999\nResult = " +         	newPlugin.convertToFiles(aDate, NewDateInputFormat.JAVA_DATE_INPUT_FORMAT,         							 datePlugin.STANDARD_OUTPUT_FORMAT,         							 datePlugin.FULL_DATE_FILESET_STANDARD));         aDate = new GregorianCalendar(2020, 10, 3);        System.out.println("\nData: 11032020\nResult = " +         	newPlugin.convertToFiles(aDate, NewDateInputFormat.JAVA_DATE_INPUT_FORMAT,         							 datePlugin.STANDARD_OUTPUT_FORMAT,         							 datePlugin.FULL_DATE_FILESET_STANDARD));    }}