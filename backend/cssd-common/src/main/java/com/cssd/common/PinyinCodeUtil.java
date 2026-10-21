package com.cssd.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public final class PinyinCodeUtil {
    private static final HanyuPinyinOutputFormat FMT = new HanyuPinyinOutputFormat();

    static {
        FMT.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        FMT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        FMT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    private PinyinCodeUtil() {
    }

    public static String initials(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : name.trim().toCharArray()) {
            if (c <= 128) {
                if (Character.isLetterOrDigit(c)) {
                    sb.append(Character.toUpperCase(c));
                }
                continue;
            }
            try {
                String[] arr = PinyinHelper.toHanyuPinyinStringArray(c, FMT);
                if (arr != null && arr.length > 0 && !arr[0].isEmpty()) {
                    sb.append(arr[0].charAt(0));
                }
            } catch (Exception ignored) {
                // skip unmapped char
            }
            if (sb.length() >= 64) {
                break;
            }
        }
        return sb.isEmpty() ? null : sb.toString();
    }
}
