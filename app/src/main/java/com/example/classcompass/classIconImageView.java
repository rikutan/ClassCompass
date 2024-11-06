package com.example.classcompass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import android.graphics.Color;
import android.util.TypedValue;

public class classIconImageView extends AppCompatImageView {

    private Paint paint;
    private Paint borderPaint;
    private Path path;
    private float cornerRadius;
    private float borderWidth;
    private int borderColor; // 追加: 色のリソースIDを保持するための変数

    public classIconImageView(Context context) {
        super(context);
        init(context);
    }

    public classIconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public classIconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // @color/black から色を取得して設定
        borderColor = context.getResources().getColor(R.color.black);
        borderPaint.setColor(borderColor);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        borderPaint.setStrokeWidth(borderWidth);
        path = new Path();
        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = getBitmapFromDrawable(getDrawable());
        if (bitmap != null) {
            int width = getWidth();
            int height = getHeight();

            // 画像の描画領域を計算
            float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
            int targetWidth;
            int targetHeight;
            if (width / aspectRatio >= height) {
                targetWidth = width;
                targetHeight = (int) (width / aspectRatio);
            } else {
                targetHeight = height;
                targetWidth = (int) (height * aspectRatio);
            }
            int left = (width - targetWidth) / 2;
            int top = (height - targetHeight) / 2;

            // 角丸矩形のクリッピングパスを設定
            path.reset();
            RectF rect = new RectF(0, 0, width, height);
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
            canvas.clipPath(path);

            // 画像を描画
            RectF targetRect = new RectF(left, top, left + targetWidth, top + targetHeight);
            canvas.drawBitmap(bitmap, null, targetRect, paint);

            // 枠線を描画
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint);
        } else {
            // 画像がセットされていない場合は通常の描画を行う
            super.onDraw(canvas);
        }
    }

    // Drawable から Bitmap を取得する
    private Bitmap getBitmapFromDrawable(android.graphics.drawable.Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return null;
        }
    }
}
