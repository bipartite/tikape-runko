package tikape.runko.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {
    
    /**
     * Parses the given date to a desired format (dd.MM.yyyy HH:mm:ss)
     * 
     * @param d The date to be parsed
     * @return A parsed version of the given date
     */
    public String parseDate(Date d){
        Calendar formatoija = Calendar.getInstance();
        formatoija.setTime(d);
        
        // Calendar returns the previous month(january=0, february=1 ...) from the real one so we have to add one to the returned month to get the real result
        int realMonth = formatoija.get(Calendar.MONTH) + 1;
        
        // Check the hours, minutes and seconds and add a zero to the front of them if needed
        String tunnit = addZero(formatoija.get(Calendar.HOUR_OF_DAY));
        String minuutit = addZero(formatoija.get(Calendar.MINUTE));
        String sekunnit = addZero(formatoija.get(Calendar.SECOND));
        
        return formatoija.get(Calendar.DAY_OF_MONTH) + "." + realMonth + "." + formatoija.get(Calendar.YEAR)
                + " " + tunnit + ":" + minuutit + ":" + sekunnit;
    }
    
    public String parseDateWithMonthNames(Date d){
        Calendar formatoija = Calendar.getInstance();
        formatoija.setTime(d);
        
        String month = getMonthName(formatoija.get(Calendar.MONTH));
        
        // Check the hours, minutes and seconds and add a zero to the front of them if needed
        String tunnit = addZero(formatoija.get(Calendar.HOUR_OF_DAY));
        String minuutit = addZero(formatoija.get(Calendar.MINUTE));
        String sekunnit = addZero(formatoija.get(Calendar.SECOND));
        
        return formatoija.get(Calendar.DAY_OF_MONTH) + ". " + month + " " + formatoija.get(Calendar.YEAR)
                + " " + tunnit + ":" + minuutit + ":" + sekunnit;
    }
    
    /**
     * Returns the name of the month with the given id
     * 
     * @param id The id of the month
     * @return The name of the month
     */
    private String getMonthName(int id){
        if(id > 11 || id < 0){
            return "";
        }
        
        String[] months = new SimpleDateFormat().getDateFormatSymbols().getMonths();
        
        return months[id];
    }
    
    /**
     * Returns the given hours, minutes or seconds with zero added to the front of the number if the value is under 10
     * 
     */
    private String addZero(int time){
        String aika = "";
        if(time < 10){
            aika = "0" + time;
        } else {
            aika = ""+time;
        }
        return aika;
    }
}
