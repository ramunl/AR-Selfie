package us.cyberstar.common.utils;

import com.crashlytics.android.Crashlytics;

public class yuvNv21Utils {

    public static int decodeNV21Pixel(byte[] nv21, int width, int height, int x, int y) {
        final int lRowStride = width;
        final int lColStride = 1;
        final int cRowStride = width;
        final int cColStride = 2;
        final int Y = 0xff & (int) nv21[y * lRowStride + x * lColStride];
        final int Cr = (0xff & (int) nv21[height * width + y / 2 * cRowStride + x / 2 * cColStride]) - 128;
        final int Cb = (0xff & (int) nv21[height * width + y / 2 * cRowStride + x / 2 * cColStride + 1]) - 128;
        return 0xff000000
                | Math.max(Math.min((Y + Cr + (Cr >> 1) + (Cr >> 2) + (Cr >> 6)), 255), 0) << 16
                | Math.max(Math.min((Y - (Cr >> 2) + (Cr >> 4) + (Cr >> 5) - (Cb >> 1) + (Cb >> 3) + (Cb >> 4) + (Cb >> 5)), 255), 0) << 8
                | Math.max(Math.min((Y + Cb + (Cb >> 2) + (Cb >> 3) + (Cb >> 5)), 255), 0);
    }
}
