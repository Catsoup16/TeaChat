package top.heartstring.teachat;

import top.heartstring.teachat.utils.ColorUtil;

public class TestCore {
    public static void main(String[] args) {
        System.out.println(ColorUtil.parseColor("<#FFFFFF-#FCA800>123<end>"));
        System.out.printf("%01x",0xdf & 0xf);
    }
}
