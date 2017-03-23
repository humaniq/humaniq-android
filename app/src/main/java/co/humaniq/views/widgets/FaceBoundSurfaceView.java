package co.humaniq.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.SurfaceView;


public class FaceBoundSurfaceView extends SurfaceView {
    private Paint paint = null;
    private RectF faceRect = new RectF();
    private int rectColor = Color.GREEN;

    public FaceBoundSurfaceView(Context context) {
        this(context, null);
    }

    public FaceBoundSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceBoundSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        configurePaint();
    }

    public void setFaceRect(int left, int top, int right, int bottom) {
        faceRect.set(left, top, right, bottom);
    }

    public void setFaceRectColor(@ColorInt int color)
    {
        rectColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (faceRect == null)
            return;

        paint.setColor(rectColor);
        canvas.drawRoundRect(faceRect, 20, 20, paint);
    }

    protected void configurePaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }
}
