package com.esterlorente.jediapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

import com.esterlorente.jediapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ImageParser {
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] array) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeByteArray(array, 0, array.length, options);
    }

    public static Uri BitmapToUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap blurEffect(Context context, Bitmap bitmap) {
        //Output Bitmap
        Bitmap OutputBitmap = bitmap.copy(bitmap.getConfig(), true);
        //RenderScript Initialization
        RenderScript renderScript = RenderScript.create(context);

        Allocation inputAllocation = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation outputAllocation = Allocation.createTyped(renderScript, inputAllocation.getType());

        //Blur Effect on Image using
        ScriptIntrinsicBlur blurEffect = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //Specified radius on which blur effect will work.
        blurEffect.setRadius(6);
        blurEffect.setInput(inputAllocation);
        blurEffect.forEach(outputAllocation);
        outputAllocation.copyTo(OutputBitmap);

        return OutputBitmap;
    }

    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static void roundEffect(Context context, ImageView imageView, Uri uri) {
        Picasso.with(context)
                .load(uri)
                .error(R.drawable.gato5)
                .transform(new CircleTransform())
                .into(imageView);
    }
}
