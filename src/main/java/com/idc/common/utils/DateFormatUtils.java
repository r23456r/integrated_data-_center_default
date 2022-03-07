package com.idc.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {
    private final  static String YYYY = "yyyy";
    private final  static String YYYYMM = "yyyyMM";
    public static String formatDateForYYYY(Date date){
        return new SimpleDateFormat(YYYY).format(date);
    }

    public static String formatDateForYYYYMM(Date date){
        return new SimpleDateFormat(YYYYMM).format(date);
    }
}
