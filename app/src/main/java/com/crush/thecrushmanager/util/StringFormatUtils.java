package com.crush.thecrushmanager.util;

import java.text.DecimalFormat;

public class StringFormatUtils {
    public static String FormatCurrency(long money){
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(money) + " VNĐ";
    }

}
