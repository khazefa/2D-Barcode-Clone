/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2d.barcode.clone.scanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author winuser
 */
public class GetBirth {
    public static int getAge(Date dateOfBirth, Date dateOfQuotation) throws Exception {

        Calendar currentDay = Calendar.getInstance();
        Calendar birthDay = Calendar.getInstance();

        int years = 0;
        int months = 0;
        int days = 0;

        currentDay.setTime(dateOfQuotation);
        birthDay.setTime(dateOfBirth);
        
        if (birthDay.after(currentDay)) {
            throw new Exception("Tanggal lahir tidak boleh hari mendatang");
        }

        years = currentDay.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

        int currMonth = currentDay.get(Calendar.MONTH)+1;
        int birthMonth = birthDay.get(Calendar.MONTH)+1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one and calculate the number of months.
        if(months < 0)
        {
         years--;
         months = 12 - birthMonth + currMonth;

         if(currentDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE))
          months--;

        }else if(months == 0 && currentDay.get(Calendar.DATE) < birthDay.get(Calendar.DATE)){
         years--;
         months = 11;
        }


        //Calculate the days
        if(currentDay.get(Calendar.DATE)>birthDay.get(Calendar.DATE))
         days = currentDay.get(Calendar.DATE) -  birthDay.get(Calendar.DATE);
        else if(currentDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE)){
         int today = currentDay.get(Calendar.DAY_OF_MONTH);
         currentDay.add(Calendar.MONTH, -1);
         days = currentDay.getActualMaximum(Calendar.DAY_OF_MONTH)-birthDay.get(Calendar.DAY_OF_MONTH)+today;
        }else{
         days=0;

         if(months == 12){
          years++;
          months = 0;
         }
        }
        if((months >=0) && (days > 0)){
            years = years + 1;
        } else {
            years = years;
        }
        return years;
    }
    
}
