package com.example.edwin.colorutil;

public class Utils {

    /**
     * 获取两条线的夹角
     *
     * @param centerX
     * @param centerY
     * @param xInView
     * @param yInView
     * @return
     */
    public static int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;
        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) //第二象限
        {
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }

        return (int) rotation;
    }

    public static int[] color2rgb(int color) {
        int[] rgb = new int[3];
        rgb[0] = (color & 0xff0000) >> 16;
        rgb[1] = (color & 0x00ff00) >> 8;
        rgb[2] = (color & 0x0000ff);
        return rgb;
    }

    public static String toColorText(int color, float alpha) {
        int[] rgb = color2rgb(color);
        int numAlpha = (int) (alpha * 100);
        StringBuilder sb = new StringBuilder();
        sb.append("颜色:").append(Integer.toHexString(color)).append(" ").
                append("RGB[").append(rgb[0]).append(",").append(rgb[1]).append(",").append(rgb[2]).append("] ").
                append("透明度：").append(numAlpha).append("%");
        return sb.toString();
    }

    public static int rgb2Argb(int color, float alpha)
    {
        return (int) (0xFF * alpha) << 24 | (color & 0xFFFFFF);
    }
}
