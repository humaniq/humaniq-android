package co.humaniq.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class GraphicKeyView extends View implements View.OnTouchListener {
    final static int BIG_CIRCLE_PIXELS = 40;
    final static int TOUCH = 60;
    final static int MEDIUM_CIRCLE_PIXELS = 15;
    final static int SMALL_CIRCLE_PIXELS = 7;

    public interface GraphicKeyCallback {
        void onFinish(final String password);
    }

    class Point {
        float X;
        float Y;
        boolean selected;
        int number;

        Point() {
            X = 0;
            Y = 0;
        }

        Point(float X, float Y) {
            this.X = X;
            this.Y = Y;
            selected = false;
        }

        Point(float X, float Y, int number) {
            this(X, Y);
            this.number = number;

        }

        void setPosition(float X, float Y) {
            this.X = X;
            this.Y = Y;
        }
    }

    class Edge {
        Point pointA;
        Point pointB;

        Edge() {
            pointA = new Point();
            pointB = new Point();
        }

        void draw(Canvas canvas) {
            canvas.drawLine(pointA.X, pointA.Y, pointB.X, pointB.Y, paint);
        }
    }

    GraphicKeyCallback callback;

    Paint paint;
    Point[] points;
    ArrayList<Edge> edges;
    Edge currentEdge;

    ArrayList<Point> selectedPoints;
    String enteredPassword;

    boolean newPasswordMode;
    boolean blockedPoints;
    int screenWidth;
    int screenHeight;

    public GraphicKeyView(Context context) {
        super(context);
        initComponents();
    }

    public void setNewPasswordMode(final boolean newPasswordMode) {
        this.newPasswordMode = newPasswordMode;
    }

    public GraphicKeyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponents();
    }

    public GraphicKeyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponents();
    }

    public void setCallback(GraphicKeyCallback callback) {
        this.callback = callback;
    }

    public String getEnteredPassword() {
        return enteredPassword;
    }

    private void initComponents() {
        callback = password -> {};
        paint = new Paint();
        edges = new ArrayList<>();
        selectedPoints = new ArrayList<>();

        initPoints();
        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        initPoints();
    }

    private void initPoints() {
        points = new Point[9];

        float thirdWidth = (float) screenWidth / 3;
        float halfHeight = (float) screenHeight / 2;
        float space = thirdWidth / 2;

        int k = 0;

        // Create points and assign number interpretation for each point from 1 to 9
        for (int j = -1; j <= 1; j++) {
            for (int i = 1; i <= 3; i++) {
                float X = thirdWidth * (i - 0.5f);
                float Y = halfHeight + (j * space * 2);
                points[k] = new Point(X, Y, k + 1);
                k++;
            }
        }
    }

    private float sqr(float val) {
        return val * val;
    }

    private Point freePointAboutTouch(float X, float Y) {
        for (Point point : points) {
            float pointX = point.X;
            float pointY = point.Y;
            float vecLength = (float) Math.sqrt(sqr(pointX - X) + sqr(pointY - Y));
            if (vecLength <= TOUCH && !point.selected) {
                return point;
            }
        }
        return null;
    }

    private boolean notBarier(Point point) {
        if (selectedPoints.isEmpty()) return true;
        Point lastPoint = selectedPoints.get(selectedPoints.size() - 1);

        boolean oddLastPoint = lastPoint.number % 2 != 0;
        boolean oddSelectedPoint = point.number % 2 != 0;
        if (oddLastPoint && lastPoint.number != 5) {
            if (oddSelectedPoint && point.number != 5) {
                return false;
            }
        }

        if (oddLastPoint)
            return true;

        switch (lastPoint.number) {
            case 2:
                if (point.number == 8)
                    return false;
                break;
            case 8:
                if (point.number == 2)
                    return false;
                break;
            case 4:
                if (point.number == 6)
                    return false;
                break;
            case 6:
                if (point.number == 4)
                    return false;
                break;
        }

        return true;
    }

    private void onActionMove(MotionEvent event) {
        if (currentEdge != null) {
            currentEdge.pointB.setPosition(event.getX(), event.getY());
        }

        Point selectedPoint = freePointAboutTouch(event.getX(), event.getY());
        if (selectedPoint == null) return;

        if (notBarier(selectedPoint)) {
            selectedPoint.selected = true;
            selectedPoints.add(selectedPoint);


            if (currentEdge == null) {
                currentEdge = new Edge();
                currentEdge.pointA.setPosition(selectedPoint.X, selectedPoint.Y);
            }
            currentEdge.pointB.setPosition(selectedPoint.X, selectedPoint.Y);
            edges.add(currentEdge);

            currentEdge = new Edge();
            currentEdge.pointA.setPosition(selectedPoint.X, selectedPoint.Y);
            currentEdge.pointB.setPosition(selectedPoint.X, selectedPoint.Y);
        }
    }

    private void onActionUp() {
        if (newPasswordMode) {
            newPasswordActionUp();
            blockedPoints = true;
        } else {
            actionUp();
        }
        currentEdge = null;
        invalidate();
    }

    private void actionUp() {
        enteredPassword = "";

        for (Point point : selectedPoints) {
            enteredPassword += point.number;
        }

        callback.onFinish(enteredPassword);

        currentEdge = null;
        edges.clear();
        selectedPoints.clear();

        for (Point point : points) {
            point.selected = false;
        }
    }

    private void newPasswordActionUp() {
        enteredPassword = "";
        for (Point point : selectedPoints) {
            enteredPassword += point.number;
        }
    }

    public String getKey() {
        enteredPassword = "";
        for (Point point : selectedPoints) {
            enteredPassword += point.number;
        }

        return enteredPassword;
    }

    public void clearKey() {
        currentEdge = null;
        edges.clear();
        selectedPoints.clear();

        for (Point point : points) {
            point.selected = false;
        }

        blockedPoints = false;
        invalidate();
    }

    private void onActionDown(MotionEvent event) {
        Point nowPoint = freePointAboutTouch(event.getX(), event.getY());
        if (nowPoint != null) {
            nowPoint.selected = true;
            selectedPoints.add(nowPoint);

            currentEdge = new Edge();
            currentEdge.pointA.setPosition(nowPoint.X, nowPoint.Y);
            currentEdge.pointB.setPosition(nowPoint.X, nowPoint.Y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        // Background
        canvas.drawARGB(255, 69, 148, 255);

        // Points of the graphic key
        paint.setColor(Color.WHITE);
        int circleSize;

        for (Point point : points) {
            final float X = point.X;
            final float Y = point.Y;

            if (point.selected) {
                paint.setStrokeWidth(5);
                circleSize = BIG_CIRCLE_PIXELS;
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(X, Y, SMALL_CIRCLE_PIXELS, paint);
                paint.setStyle(Paint.Style.STROKE);
            } else {
                paint.setStrokeWidth(2);
                circleSize = MEDIUM_CIRCLE_PIXELS;
            }

            //canvas.drawPoint(X, Y, paint);
            canvas.drawCircle(X, Y, circleSize, paint);
        }

        // Active edges
        paint.setStrokeWidth(10);
        paint.setColor(Color.argb(150, 255, 255, 255));

        for (Edge edge : edges) {
            edge.draw(canvas);
        }

        if (currentEdge != null) {
            currentEdge.draw(canvas);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (blockedPoints) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;

            case MotionEvent.ACTION_UP:
                onActionUp();
                break;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            default:
                return true;
        }

        invalidate();
        return true;
    }
}
