package dk.statsbiblioteket.doms.transformers.common;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class CalendarUtils {

    /**
     * Private constructor. 
     */
    private CalendarUtils() {}
    
    /**
     * Creates a Date object from the string representation of unixtime in seconds. 
     * @param unixTime the unixtime. If the argument is null, then epoch is returned. 
     */
    public static Date getDate(String unixTime) {
        Date date;
        
        if(unixTime == null) {
            date = new Date(0);
        } else {
            date = new Date(Long.parseLong(unixTime)*1000);
        }
        return date;
    }
    
    /**
     * Turns a string representation of unix time into a XMLGregorianCalendar.
     * @param unixTime The unixtime. If the argument is null, then epoch is returned.
     * @return The XMLGregorianCalendar.
     */
    public static XMLGregorianCalendar getXmlGregorianCalendar(String unixTime) {
        return getXmlGregorianCalendar(getDate(unixTime));
    }
    
    /**
     * Turns a date into a XMLGregorianCalendar.
     * @param date The Date. If the argument is null, then epoch is returned.
     * @return The XMLGregorianCalendar.
     */
    public static XMLGregorianCalendar getXmlGregorianCalendar(Date date) {
        if(date == null) {
            date = new Date(0);
        } 
        
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (Exception e) {
            throw new IllegalStateException("Could not convert the date '" + date + "' into the xml format.", e);
        }
    }
}
