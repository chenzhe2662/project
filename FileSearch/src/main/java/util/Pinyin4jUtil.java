package util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Pinyin4jUtil {
    /**
     * 中文字符格式
     */
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();
    static {
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }



    public static boolean containsChinese(String str) {
        return str.matches(".*" + CHINESE_PATTERN + ".*");
    }



    public static String[] get(String hanyu) {
        String[] result = new String[2];
        StringBuilder pinyin = new StringBuilder();
        StringBuilder firstChar = new StringBuilder();
        for (int i = 0; i < hanyu.length(); i++) {
            try {
                String[] pinyins =
                        PinyinHelper.toHanyuPinyinStringArray(hanyu.charAt(i), FORMAT);
                if (pinyins == null || pinyins.length == 0) {
                    pinyin.append(hanyu.charAt(i));
                    firstChar.append(hanyu.charAt(i));
                } else {
                    pinyin.append(pinyins[0]);
                    firstChar.append(pinyins[0].charAt(0));
                }
            } catch (Exception e) {
                pinyin.append(hanyu.charAt(i));
                firstChar.append(hanyu.charAt(i));
            }
        }
        result[0] = pinyin.toString();
        result[1] = firstChar.toString();
        return result;
    }
    public static String[][] get(String hanyu ,boolean fullSpell) {
        String[][] result = new String[hanyu.length()][];
        for (int i = 0; i < hanyu.length(); i++) {
            try {
                String[] pinyins =
                        PinyinHelper.toHanyuPinyinStringArray(hanyu.charAt(i),FORMAT);
                if (pinyins == null || pinyins.length == 0) {
                   result[i] = new String[]{String.valueOf(hanyu.charAt(i))};
                }else {
                    result[i] = unique(pinyins,fullSpell);
                }
            } catch (Exception e) {
                result[i] = new String[]{String.valueOf(hanyu.charAt(i))};
            }
        }
        return result;
    }

    private static String[] unique(String[] pinyins,boolean fullSpell) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < pinyins.length; i++) {
            if (fullSpell) {
                set.add(pinyins[i]);
            }else {
                set.add(String.valueOf(pinyins[i].charAt(0)));
            }
        }
       return set.toArray(new String[set.size()]);
    }
    //排列  两两组合 依次
    private static String[] sort(String pinyin) {
        String[][] s = get(pinyin,true);

        String[] array = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[i].length; j++) {
                String[] arr = new String[s[i].length];
                if (s.length == 1) {
                    array[i] = s[i][0];
                }
            }

        }
        return array;
    }

//    public static void main(String[] args) {
//        String[] pinyin = get("中华人民共和国");
//        System.out.println(Arrays.toString(pinyin));
//
//        for (String[] s : get("中华人a民共1和国",true)){
//            System.out.println(Arrays.toString(s));
//
//        }
//
//       System.out.println(Arrays.toString(sort("中华人a民共1和国")));
//    }
}
