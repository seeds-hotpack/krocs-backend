package com.hotpack.krocs.global.common.util;

public class SortUtils {
    /**
     * 문자열을 한글 우선으로 정렬하기 위한 키 생성
     * @param text 정렬할 문자열
     * @return 정렬 키 (1:한글, 2:영어, 3:숫자 등 기타)
     */
    public static String sortByKoreanFirst(String text) {
        if (text == null || text.isEmpty()) {
            return "zzz";
        }
        char firstChar = text.charAt(0);

        if (firstChar >= '가' && firstChar <= '힣') {
            return "1" + text;
        }

        else if ((firstChar >= 'A' && firstChar <= 'Z') ||
                (firstChar >= 'a' && firstChar <= 'z')) {
            return "2" + text.toLowerCase();
        }

        else {
            return "3" + text;
        }
    }
}
