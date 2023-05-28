package com.spot.alert.utils;


import android.graphics.Bitmap;

public class BitMapUtils {

    public static Bitmap scaleBitmap(final Bitmap input, final long maxBytes) {
        final int currentWidth = input.getWidth();
        final int currentHeight = input.getHeight();
        final int currentPixels = currentWidth * currentHeight;

        final long maxPixels = maxBytes / 4; // Floored
        if (currentPixels <= maxPixels) {
            return input;
        }

        final double scaleFactor = Math.sqrt(maxPixels / (double) currentPixels);
        final int newWidthPx = (int) Math.floor(currentWidth * scaleFactor);
        final int newHeightPx = (int) Math.floor(currentHeight * scaleFactor);

        final Bitmap output = Bitmap.createScaledBitmap(input, newWidthPx, newHeightPx, true);
        return output;
    }
}
