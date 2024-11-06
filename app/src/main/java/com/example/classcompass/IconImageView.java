package com.example.classcompass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

public class IconImageView extends androidx.appcompat.widget.AppCompatImageView {

    private Paint paint;
    private Path path;

    public IconImageView(Context context) {
        super(context);
        init();
    }

    public IconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = getBitmapFromDrawable(getDrawable());
        if (bitmap != null) {
            int width = getWidth();
            int height = getHeight();
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            // 画像の描画領域を計算
            float aspectRatio = (float) bitmapWidth / bitmapHeight;
            int targetWidth;
            int targetHeight;
            if (width > height) {
                targetHeight = height;
                targetWidth = (int) (height * aspectRatio);
            } else {
                targetWidth = width;
                targetHeight = (int) (width / aspectRatio);
            }
            int left = (width - targetWidth) / 2;
            int top = (height - targetHeight) / 2;

            // 円形のクリッピングパスを設定
            path.reset();
            path.addCircle(width / 2f, height / 2f, Math.min(width, height) / 2f, Path.Direction.CW);
            canvas.clipPath(path);

            // 画像を描画
            canvas.drawBitmap(bitmap, null, new Rect(left, top, left + targetWidth, top + targetHeight), paint);
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
