package com.hltc.mtmap.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by Redoblue on 2015/4/15.
 */
public class StringUtils {

//    public static final String  phoneRegexp = "^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$";

    public static final String phoneRegexp = "1[3|5|7|8|][0-9]{9}";
    public static final String vcodeRegexp = "[0-9]{4}";
    public static final String passwdRegexp = "[0-9]{6}";   //密码格式需要设置

    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static SimpleDateFormat dateFormater = new
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat dateFormater2 = new
            SimpleDateFormat("yyyy-MM-dd");

    /**
     * Is empty.
     *
     * @param input the input
     * @return the boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input)) {
            return true;
        }
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * String To long.
     *
     * @param obj the obj
     * @return the long
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static double toDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String toMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }


    /**
     * Gets today.
     *
     * @return the today
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String date = dateFormater2.format(cal.getTime());
        date = date.replace("-", "");
        return Long.parseLong(date);
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 严格验证是否是手机号码
     *
     * @param str
     * @return
     */
    public static boolean isPhone(String str) {
        Pattern pattern = Pattern
                .compile(phoneRegexp);
        return pattern.matcher(str).matches();
    }

    public static boolean isVerifyCode(String str) {
        Pattern pattern = Pattern
                .compile(vcodeRegexp);
        return pattern.matcher(str).matches();
    }

    public static boolean isPasswd(String str) {
        Pattern pattern = Pattern
                .compile(passwdRegexp);
        return pattern.matcher(str).matches();
    }

    public static boolean isEmail(String s) {
        return emailer.matcher(s).matches();
    }

    public static String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
    }

    public static String getFormatedPhone(String arg) {
        String phone = "";
        if (!isPhone(arg)) {
            if (arg.startsWith("+")) {
                phone = arg.substring(3);
            } else if (arg.contains("-")) {
                for (int i = 0; i < arg.length(); i++) {
                    if (arg.charAt(i) != '-') {
                        phone += arg.charAt(i);
                    }
                }
            } else if (arg.contains(" ")) {
                for (int i = 0; i < arg.length(); i++) {
                    if (arg.charAt(i) != ' ') {
                        phone += arg.charAt(i);
                    }
                }
            }
        } else {
            phone = arg;
        }
        return phone;
    }
}
