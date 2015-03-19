package bdoty.celerygraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GraphView extends RelativeLayout implements View.OnTouchListener {

    private static final int COLOR_GREEN = 0xFF55FF66;
    private static final int COLOR_RED = 0xFFFF5566;
    private static final int COLOR_DARK_GREEN = 0xAA55FF66;
    private static final int COLOR_DARK_RED = 0xAAFF5566;
    private static final int WHITE = 0xFFFFFFEE;

    private List<Float> dataSet;
    private List<Float> reducedDataSet;

    private boolean inverted = true;
    private boolean move = false;

    private Paint paint;
    private Paint dataPaint;
    private Paint fillPaint;
    private Paint textPaint;

    private Path dashedPath;
    private Path path;
    private Path fillPath;

    private float initY;
    private int initHeight;

    private float paddingLeft = 75f;
    private float paddingTop = 16f;
    private float paddingBottom = 16f;

    private float segmentWidth;
    private float unitHeight;
    private float lin = 12f;
    private int scrollDist;
    private int initScrollDist;
    private float overflow;
    private int offset = 0;

    private float max;
    private float min;

    boolean resizeEnabled = false;

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(List<Float> data) {
        this.dataSet = data;

        paint = new Paint();
        dataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dataPaint.setAntiAlias(true);
        dataPaint.setStyle(Paint.Style.STROKE);
        dataPaint.setStrokeWidth(5);
        dataPaint.setColor(WHITE);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(16);
        textPaint.setStrokeWidth(1);

        path = new Path();
        dashedPath = new Path();
        fillPath = new Path();

        //dataSet = generateData();
        reducedDataSet = new ArrayList<Float>();

        if (dataSet.isEmpty()) {
            min = 0;
            max = 0;
        } else {
            min = Collections.min(dataSet);
            if (min > 0) min = 0;
            max = Collections.max(dataSet);
            if (max < 0) max = 0;
        }
        initHeight = getHeight();
        setOnTouchListener(this);
    }

    private List<Float> generateData() {
        Random rand = new Random();
        int num = rand.nextInt(5) + 5;

        List<Float> data = new ArrayList<>();
        data.add((float) rand.nextInt(6000) - 3000);
        String string = "" + data.get(0);
        for (int i = 1; i < num; i++) {
            data.add((data.get(i - 1) + (float) rand.nextInt(6000) - 3000));
            string += " , " + data.get(i);
        }
        System.out.println(string);
        return data;
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (initHeight == 0) initHeight = getHeight();

        segmentWidth = Math.max((getWidth() - paddingLeft) / 8, (getWidth() - paddingLeft) / (dataSet.size()));
        overflow = segmentWidth * dataSet.size() - getWidth() + paddingLeft;

        if (move) {
            scrollDist = (int) overflow;
            move = false;
        }
        offset = (int) (scrollDist / segmentWidth);
        int numVisible = (int) ((getWidth() + scrollDist) / segmentWidth) - offset - 1;

        if (!dataSet.isEmpty()) {
            reducedDataSet.clear();
            for (int i = offset - 1; i < numVisible + offset + 1; i++) {
                if (i == -1) reducedDataSet.add(0f);
                else if (i < dataSet.size()) reducedDataSet.add(dataSet.get(i));
            }
            if (inverted) {
                for (int i = 0; i < reducedDataSet.size(); i++) {
                    reducedDataSet.set(i, reducedDataSet.get(i) * -1);
                }
            }

            float interL = reducedDataSet.get(0) + ((scrollDist % segmentWidth) * (reducedDataSet.get(1) - reducedDataSet.get(0)) / segmentWidth);

            int p = reducedDataSet.size() - 2;
            float x2 = (segmentWidth * (p + 1)) + paddingLeft - (scrollDist % segmentWidth);
            float gap = segmentWidth - (x2 - getWidth());
            float interR = reducedDataSet.get(p) + (gap) * ((reducedDataSet.get(p + 1) - reducedDataSet.get(p)) / segmentWidth);

            List<Float> temp = new ArrayList<>();
            temp.addAll(reducedDataSet);
            temp.set(0, interL);
            temp.set(temp.size() - 1, interR);

            min = Collections.min(temp);
            if (min > 0) min = 0;
            max = Collections.max(temp);
            if (max < 0) max = 0;

            unitHeight = (getHeight() - paddingBottom - paddingTop) / (max - min);

            paint.setColor(0xCCFF5566);
            paint.setStrokeWidth(5);

            // MOVE TO '0,0' POSITION
            float y = getHeight() - (0 - min) * unitHeight - paddingBottom;
            path.reset();
            path.moveTo(paddingLeft, y);

            // DRAW BORDER
            paint.setColor(WHITE);
            dashedPath.reset();
            dashedPath.moveTo(paddingLeft, 0);
            dashedPath.lineTo(paddingLeft, getHeight());
            dashedPath.lineTo(getWidth(), getHeight());
            paint.setPathEffect(null);
            canvas.drawPath(dashedPath, paint);

            paint.setStrokeWidth(2);

            paint.setColor(0x99FF5566);
            float x;

            // DRAW HORIZONTAL LINES
            float[] fl = new float[]{max, 0f, min};
            for (int i = 0; i < 3; i++) {
                boolean draw = (i == 0 && max == 0) || (i == 2 && min == 0);
                y = getHeight() - (fl[i] - min) * unitHeight - paddingBottom;
                paint.setColor(WHITE);
                if (i == 2) {
                    fillPaint.setColor(COLOR_RED);
                    canvas.drawRect(paddingLeft, y, getWidth(), getHeight(), fillPaint);
                }
                if (i == 0) {
                    fillPaint.setColor(COLOR_GREEN);
                    canvas.drawRect(paddingLeft, 0, getWidth(), y, fillPaint);
                }
                if (!draw) {
                    dashedPath.reset();
                    dashedPath.moveTo(paddingLeft, y);
                    dashedPath.lineTo(getWidth(), y);
                    paint.setStrokeWidth(2);
                    canvas.drawPath(dashedPath, paint);
                }
            }

            drawData(canvas, reducedDataSet);

            fillPaint.setColor(0xFF292934);
            fillPath.moveTo(0, 0);
            fillPath.lineTo(paddingLeft, 0);
            fillPath.lineTo(paddingLeft, getHeight());
            fillPath.lineTo(0, getHeight());
            canvas.drawPath(fillPath, fillPaint);
            fillPath.reset();

            // DRAW TEXT
            path.reset();
            dashedPath.reset();
            for (int i = 0; i < 3; i++) {
                boolean draw = (i == 0 && max == 0) || (i == 2 && min == 0);
                y = getHeight() - (fl[i] - min) * unitHeight - paddingBottom;
                paint.setColor(WHITE);
                if (i == 2) {
                    textPaint.setColor(COLOR_RED);
                }
                if (i == 1) {
                    textPaint.setColor(WHITE);
                }
                if (i == 0) {
                    textPaint.setColor(COLOR_GREEN);
                }
                if (!draw) {
                    paint.setPathEffect(null);
                    path.reset();
                    path.moveTo(paddingLeft - lin, y);
                    path.lineTo(paddingLeft, y);

                    paint.setTextSize(16);
                    paint.setStrokeWidth(1);
                    Rect bounds = new Rect();
                    paint.getTextBounds("0", 0, 1, bounds);
                    y += bounds.height() / 2;
                    String text = fl[i] == 0 ? "0.0" : Float.toString(toTenths(-1 * fl[i]));
                    text = (-1 * fl[i] > 0 ? "+ " : "") + text;
                    x = (paddingLeft - lin) / 2 - paint.measureText(text) / 2;
                    canvas.drawText(text, x, y, textPaint);

                    canvas.drawPath(path, paint);
                }
            }
        } else {
            float y = getHeight() / 2;

            // DRAW COLOUR
            fillPaint.setColor(COLOR_RED);
            canvas.drawRect(paddingLeft, getHeight() - paddingBottom, getWidth(), getHeight(), fillPaint);

            fillPaint.setColor(COLOR_GREEN);
            canvas.drawRect(paddingLeft, 0, getWidth(), paddingTop, fillPaint);

            fillPaint.setColor(COLOR_DARK_GREEN);
            canvas.drawRect(paddingLeft, paddingTop, getWidth(), y, fillPaint);

            fillPaint.setColor(COLOR_DARK_RED);
            canvas.drawRect(paddingLeft, y, getWidth(), getHeight(), fillPaint);

            // DRAW LINES
            paint.setColor(WHITE);
            paint.setStrokeWidth(2);
            canvas.drawLine(paddingLeft, paddingTop, getWidth(), paddingTop, paint);
            canvas.drawLine(paddingLeft, getHeight()-paddingBottom, getWidth(), getHeight()-paddingBottom, paint);
            canvas.drawLine(paddingLeft, y, getWidth(), y, paint);
            canvas.drawLine(paddingLeft, paddingTop, paddingLeft, getHeight()-paddingBottom, paint);
        }
        //drawVerticalLines(canvas, dataSet);
    }

    public float toTenths(float time) {
        int seconds = (int) (time / 1000);
        int dec = (int) (time / 100);
        dec %= 10;
        return (seconds) + ((float) dec) / 10;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (resizeEnabled) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    initHeight = getHeight();
                    initY = event.getY();
                }

                case MotionEvent.ACTION_MOVE: {
                    float y = event.getY() - initY;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                    params.height = initHeight + y > 5 ? (int) (initHeight + y) : 5;
                    setLayoutParams(params);
                    invalidate();
                }
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    initScrollDist = scrollDist + (int) event.getX();
                }

                case MotionEvent.ACTION_MOVE: {
                    if (overflow == 0) return true;
                    scrollDist = initScrollDist - (int) event.getX();
                    if (scrollDist > overflow) {
                        scrollDist = (int) overflow;
                        initScrollDist = (int) (overflow + event.getX());
                    }
                    if (scrollDist < 0) {
                        scrollDist = 0;
                        initScrollDist = (int) event.getX();
                    }
                    invalidate();
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    public void notifyDataSetChanged() {
        invalidate();
    }

    public void moveToLast() {
        move = true;
    }

    public void reset() {
        scrollDist = 0;
        invalidate();
    }

    public void drawData(Canvas c, List<Float> data) {

        float prevX, prevY, x, y;
        prevX = paddingLeft - (scrollDist % segmentWidth);
        prevY = getHeight() - (data.get(0) - min) * unitHeight - paddingBottom;

        float zeroY = getHeight() - (0 - min) * unitHeight - paddingBottom;

        fillPaint.setColor(COLOR_DARK_GREEN);
        fillPath.moveTo(paddingLeft, paddingTop);
        fillPath.lineTo(getWidth(), paddingTop);
        fillPath.lineTo(getWidth(), zeroY);
        fillPath.lineTo(paddingLeft, zeroY);
        c.drawPath(fillPath, fillPaint);
        fillPath.reset();

        fillPaint.setColor(COLOR_DARK_RED);
        fillPath.moveTo(paddingLeft, getHeight() - paddingBottom);
        fillPath.lineTo(getWidth(), getHeight() - paddingBottom);
        fillPath.lineTo(getWidth(), zeroY);
        fillPath.lineTo(paddingLeft, zeroY);
        c.drawPath(fillPath, fillPaint);
        fillPath.reset();

        for (int i = 1; i < data.size(); i++) {
            x = (segmentWidth * (i)) + paddingLeft - (scrollDist % segmentWidth);
            y = getHeight() - (data.get(i) - min) * unitHeight - paddingBottom;

            c.drawLine(prevX, prevY, x, y, dataPaint);

            prevX = x;
            prevY = y;
        }

        paint.setColor(WHITE);
        for (int i = 0; i < data.size(); i++) {
            x = (segmentWidth * (i)) + paddingLeft - (scrollDist % segmentWidth);
            y = getHeight() - (data.get(i) - min) * unitHeight - paddingBottom;

            if (data.get(i) > 0) fillPaint.setColor(COLOR_GREEN);
            else fillPaint.setColor(COLOR_RED);
            c.drawCircle(x, y, 12, fillPaint);
            c.drawCircle(x, y, 12, paint);
        }
    }

    @SuppressWarnings("unused")
    public void drawVerticalLines(Canvas c, List<Float> data) {
        float x, y;
        for (int i = 0; i < data.size(); i++) {
            x = (segmentWidth * (i + 1)) + paddingLeft - scrollDist;
            y = getHeight() - (data.get(i) - min) * unitHeight - paddingBottom;

            dashedPath.reset();
            dashedPath.moveTo(x, y);
            dashedPath.lineTo(x, getHeight());
            if (data.get(i) > 0) paint.setColor(COLOR_GREEN);
            else paint.setColor(COLOR_RED);
            c.drawPath(dashedPath, paint);
        }
    }
}