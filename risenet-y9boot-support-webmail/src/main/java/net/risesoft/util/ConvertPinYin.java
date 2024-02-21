package net.risesoft.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ConvertPinYin {

    /**
     * 讲字符串中的中文转化为拼音,其他字符不变
     *
     * @param chinese 中文
     * @return 汉语拼音
     */
    public static String getPinyin(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);

        char[] hanYuArr = chinese.trim().toCharArray();
        StringBuilder pinYin = new StringBuilder();
        try {
            for (char c : hanYuArr) {
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {// 匹配是否是汉字
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);// 如果是多音字，返回多个拼音，这里只取第一个
                    pinYin.append(temp[0]);
                } else {
                    pinYin.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        return pinYin.toString();
    }
}
