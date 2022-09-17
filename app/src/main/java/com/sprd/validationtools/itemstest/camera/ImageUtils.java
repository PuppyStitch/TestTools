package com.sprd.validationtools.itemstest.camera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import android.graphics.ImageFormat;
import android.media.Image;
import android.media.Image.Plane;
import android.util.Log;

public class ImageUtils {

    private static final String TAG = "ImageUtils";

    public static void writeYuvImage(Image img, OutputStream out)
            throws IOException {
        if (img.getFormat() != ImageFormat.YUV_420_888) {
            Log.w(TAG,
                    String.format(
                            "Unexpected Image format: %d, expected ImageFormat.YUV_420_888",
                            img.getFormat()));
        }
        WritableByteChannel outChannel = Channels.newChannel(out);
        for (int plane = 0; plane < 3; plane++) {
            Plane colorPlane = img.getPlanes()[plane];
            ByteBuffer colorData = colorPlane.getBuffer();
            int subsampleFactor = (plane == 0) ? 1 : 2;
            int colorW = img.getWidth() / subsampleFactor;
            int colorH = img.getHeight() / subsampleFactor;
            colorData.rewind();
            colorData.limit(colorData.capacity());
            if (colorPlane.getPixelStride() == 1) {
                // Can write contiguous rows
                for (int y = 0, rowStart = 0; y < colorH; y++, rowStart += colorPlane
                        .getRowStride()) {
                    colorData.limit(rowStart + colorW);
                    colorData.position(rowStart);
                    outChannel.write(colorData);
                }
            } else {
                // Need to pack rows
                byte[] row = new byte[(colorW - 1)
                        * colorPlane.getPixelStride() + 1];
                byte[] packedRow = new byte[colorW];
                ByteBuffer packedRowBuffer = ByteBuffer.wrap(packedRow);
                for (int y = 0, rowStart = 0; y < colorH; y++, rowStart += colorPlane
                        .getRowStride()) {
                    colorData.position(rowStart);
                    colorData.get(row);
                    for (int x = 0, i = 0; x < colorW; x++, i += colorPlane
                            .getPixelStride()) {
                        packedRow[x] = row[i];
                    }
                    packedRowBuffer.rewind();
                    outChannel.write(packedRowBuffer);
                }
            }
        }
    }

    public static byte[] getNV21FromImage(Image img) {
        long t1 = System.currentTimeMillis();
        final int NUM_PLANES = 3;
        final Plane[] planeList = img.getPlanes();
        ByteBuffer[] planeBuf = new ByteBuffer[NUM_PLANES];

        for (int i = 0; i < NUM_PLANES; i++) {
            Plane plane = planeList[i];
            planeBuf[i] = plane.getBuffer();
        }

        ByteBuffer buf = planeBuf[0];
        int yLength = buf.remaining();
        byte[] imageBytes = new byte[yLength * 3 / 2];
        buf.get(imageBytes, 0, yLength);
        buf.clear();

        byte[] bytes_u;
        buf = planeBuf[1];
        int uLength = buf.remaining();
        bytes_u = new byte[uLength];
        buf.get(bytes_u);
        buf.clear();

        byte[] bytes_v;
        buf = planeBuf[2];
        bytes_v = new byte[1];
        buf.get(bytes_v);
        buf.clear();
        imageBytes[yLength] = bytes_v[0];
        System.arraycopy(bytes_u, 0, imageBytes, yLength + 1, uLength);
        bytes_u = null;
        bytes_v = null;
        Log.i(TAG, "getNV21FromImage cost " + (System.currentTimeMillis() - t1));
        return imageBytes;
    }
}
