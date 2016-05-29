package com.example;

import java.text.ParseException;
import java.util.Calendar;

public class MyClass {
    public static void main(String []args) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        System.out.println("Today "+calendar.getTime());
        System.out.println("Day : "+calendar.get(Calendar.DAY_OF_MONTH));

        System.out.println("Month : "+calendar.get(Calendar.MONTH));
        System.out.println("Year : "+calendar.YEAR);

        calendar.set(calendar.MONTH,calendar.JANUARY);
        System.out.println("Date : "+calendar.getTime());

        System.out.println("Current mon "+Calendar.MONTH);
        calendar.add(calendar.MONTH,-1);

        System.out.println("modified date "+calendar.getTime());
        int Month_num = calendar.get(Calendar.MONTH) + 1;
        System.out.println("Before 1 month : "+calendar.get(Calendar.YEAR)+"-"+
                Month_num+"-"+calendar.get(Calendar.DAY_OF_MONTH));

    }
}
