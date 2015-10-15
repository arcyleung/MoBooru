package com.example.arthurl.mobooru;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import java.io.File;

import com.squareup.picasso.Transformation;

public class BlurTransformation implements Transformation {

    Context con;

    RenderScript rs;
    public BlurTransformation(Context context) {
        super();
        rs = RenderScript.create(context);
        con = context;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        // Create another bitmap that will hold the results of the filter.
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);

        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);

        // Set the blur radius
        script.setRadius(20);

        // Start the ScriptIntrinisicBlur
        script.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap);
        bitmap.recycle();

//            blurredBitmap = overlay(BitmapFactory.decodeResource(con.getResources(),
//                    R.drawable.nsfwlogo), blurredBitmap);


        return blurredBitmap;
    }

    private Bitmap overlay(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, new Matrix(), null);
        return bmOverlay;
    }

    @Override
    public String key() {
        return "blur";
    }

}