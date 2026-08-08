package com.mm55.mtkbypass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BypassActivity extends Activity {

    private static final String PROP_BYPASS = "persist.sys.mtk_bypass";
    private static final String PROP_AUTO = "persist.sys.mtk_bypass_auto";
    private static final String PROP_LIMIT = "persist.sys.mtk_bypass_limit";

    private static final int COLOR_CARD_BG = 0xFF222A2A;
    private static final int COLOR_ACCENT = 0xFF70D3CE;
    private static final int COLOR_TRACK_OFF = 0xFF354141;
    private static final int COLOR_THUMB_ON = 0xFF003737;

    private LinearLayout autoCard;
    private LinearLayout sliderCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        startService(new Intent(this, BypassService.class));

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        rootLayout.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int paddingPx = dpToPx(16);
            v.setPadding(paddingPx, topInset + dpToPx(8), paddingPx, paddingPx);
            return insets;
        });

        TypedValue primaryColor = new TypedValue();
        TypedValue secondaryColor = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.textColorPrimary, primaryColor, true);
        getTheme().resolveAttribute(android.R.attr.textColorSecondary, secondaryColor, true);

        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);
        headerLayout.setPadding(0, dpToPx(4), 0, dpToPx(20));

        View backButton = createBackButton(primaryColor.data);

        TextView titleView = new TextView(this);
        titleView.setText(R.string.app_name);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
        titleView.setTypeface(null, Typeface.NORMAL);
        titleView.setTextColor(primaryColor.data);

        headerLayout.addView(backButton);
        headerLayout.addView(titleView);
        rootLayout.addView(headerLayout);

        int cardMarginBottom = dpToPx(2);

        LinearLayout mainCard = new LinearLayout(this);
        mainCard.setOrientation(LinearLayout.HORIZONTAL);
        mainCard.setGravity(Gravity.CENTER_VERTICAL);
        mainCard.setPadding(dpToPx(16), dpToPx(18), dpToPx(16), dpToPx(18));
        mainCard.setBackground(createCardShape(24f, 4f, COLOR_CARD_BG));

        LinearLayout.LayoutParams card1Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card1Params.setMargins(0, 0, 0, cardMarginBottom);
        mainCard.setLayoutParams(card1Params);

        TextView labelMain = new TextView(this);
        labelMain.setText(R.string.enable_bypass_charging);
        labelMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        labelMain.setTextColor(primaryColor.data);
        labelMain.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        M3Switch mainSwitch = new M3Switch(this);
        mainSwitch.setChecked(SystemProperties.getInt(PROP_BYPASS, 0) == 1);
        mainSwitch.setOnCheckedChangeListener(isChecked -> {
            SystemProperties.set(PROP_BYPASS, isChecked ? "1" : "0");
        });

        mainCard.addView(labelMain);
        mainCard.addView(mainSwitch);
        rootLayout.addView(mainCard);

        autoCard = new LinearLayout(this);
        autoCard.setOrientation(LinearLayout.HORIZONTAL);
        autoCard.setGravity(Gravity.CENTER_VERTICAL);
        autoCard.setPadding(dpToPx(16), dpToPx(18), dpToPx(16), dpToPx(18));

        LinearLayout.LayoutParams card2Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card2Params.setMargins(0, 0, 0, cardMarginBottom);
        autoCard.setLayoutParams(card2Params);

        LinearLayout autoTextLayout = new LinearLayout(this);
        autoTextLayout.setOrientation(LinearLayout.VERTICAL);
        autoTextLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView labelAuto = new TextView(this);
        labelAuto.setText(R.string.auto_limit_title);
        labelAuto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        labelAuto.setTextColor(primaryColor.data);

        TextView labelAutoDesc = new TextView(this);
        labelAutoDesc.setText(R.string.auto_limit_desc);
        labelAutoDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        labelAutoDesc.setTextColor(secondaryColor.data);
        labelAutoDesc.setPadding(0, dpToPx(4), dpToPx(12), 0);

        autoTextLayout.addView(labelAuto);
        autoTextLayout.addView(labelAutoDesc);

        M3Switch autoSwitch = new M3Switch(this);
        boolean isAutoChecked = SystemProperties.getInt(PROP_AUTO, 0) == 1;
        autoSwitch.setChecked(isAutoChecked);

        autoCard.addView(autoTextLayout);
        autoCard.addView(autoSwitch);
        rootLayout.addView(autoCard);

        sliderCard = new LinearLayout(this);
        sliderCard.setOrientation(LinearLayout.VERTICAL);
        sliderCard.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(18));

        LinearLayout.LayoutParams card3Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card3Params.setMargins(0, 0, 0, cardMarginBottom);
        sliderCard.setLayoutParams(card3Params);

        TextView labelThreshold = new TextView(this);
        int currentLimit = SystemProperties.getInt(PROP_LIMIT, 80);
        labelThreshold.setText(getString(R.string.cutoff_threshold, currentLimit));
        labelThreshold.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        labelThreshold.setTextColor(primaryColor.data);
        labelThreshold.setPadding(0, 0, 0, dpToPx(12));

        M3Slider slider = new M3Slider(this);
        int initialProgress = (currentLimit - 20) / 10;
        if (initialProgress < 0) initialProgress = 0;
        if (initialProgress > 6) initialProgress = 6;
        slider.setProgress(initialProgress);

        slider.setOnSliderChangeListener(progress -> {
            int selectedPct = 20 + (progress * 10);
            labelThreshold.setText(getString(R.string.cutoff_threshold, selectedPct));
            SystemProperties.set(PROP_LIMIT, String.valueOf(selectedPct));
        });

        sliderCard.addView(labelThreshold);
        sliderCard.addView(slider);
        rootLayout.addView(sliderCard);

        updateCardShapes(isAutoChecked);

        autoSwitch.setOnCheckedChangeListener(isChecked -> {
            SystemProperties.set(PROP_AUTO, isChecked ? "1" : "0");
            updateCardShapes(isChecked);
            startService(new Intent(this, BypassService.class));
        });

        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);
        infoLayout.setPadding(dpToPx(4), dpToPx(20), dpToPx(4), 0);

        View infoIcon = createInfoIcon(secondaryColor.data);
        LinearLayout.LayoutParams infoIconParams = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));
        infoIconParams.setMarginEnd(dpToPx(12));
        infoIcon.setLayoutParams(infoIconParams);

        TextView infoText = new TextView(this);
        infoText.setText("Bypass şarj etkinleştirildiğinde cihaz gücü doğrudan şarj cihazından alır, pil dolumu durdurularak ısınma engellenir.");
        infoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        infoText.setTextColor(secondaryColor.data);
        infoText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        infoLayout.addView(infoIcon);
        infoLayout.addView(infoText);
        rootLayout.addView(infoLayout);

        setContentView(rootLayout);
    }

    private void updateCardShapes(boolean isSliderVisible) {
        if (isSliderVisible) {
            autoCard.setBackground(createCardShape(4f, 4f, COLOR_CARD_BG));
            sliderCard.setVisibility(View.VISIBLE);
            sliderCard.setBackground(createCardShape(4f, 24f, COLOR_CARD_BG));
        } else {
            autoCard.setBackground(createCardShape(4f, 24f, COLOR_CARD_BG));
            sliderCard.setVisibility(View.GONE);
        }
    }

    private GradientDrawable createCardShape(float topDp, float bottomDp, int bgColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(bgColor);
        float topPx = dpToPx(topDp);
        float bottomPx = dpToPx(bottomDp);
        shape.setCornerRadii(new float[]{
                topPx, topPx,
                topPx, topPx,
                bottomPx, bottomPx,
                bottomPx, bottomPx
        });
        return shape;
    }

    private View createBackButton(int textColor) {
        int size = dpToPx(40);
        FrameLayout container = new FrameLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMarginEnd(dpToPx(16));
        container.setLayoutParams(lp);

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        boolean isDark = Color.red(textColor) > 128;
        bg.setColor(isDark ? 0x24FFFFFF : 0x12000000);
        container.setBackground(bg);

        View arrowView = new View(this) {
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            {
                paint.setColor(textColor);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dpToPx(2.2f));
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
            }
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float w = getWidth();
                float h = getHeight();
                float cy = h / 2f;

                canvas.drawLine(w * 0.32f, cy, w * 0.68f, cy, paint);
                canvas.drawLine(w * 0.32f, cy, w * 0.48f, cy - dpToPx(5f), paint);
                canvas.drawLine(w * 0.32f, cy, w * 0.48f, cy + dpToPx(5f), paint);
            }
        };
        container.addView(arrowView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        container.setOnClickListener(v -> finish());
        return container;
    }

    private View createInfoIcon(int color) {
        return new View(this) {
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            {
                paint.setColor(color);
            }
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float w = getWidth();
                float h = getHeight();
                float cx = w / 2f;
                float cy = h / 2f;
                float r = Math.min(w, h) / 2f - dpToPx(1f);

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dpToPx(1.5f));
                canvas.drawCircle(cx, cy, r, paint);

                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy - dpToPx(4f), dpToPx(1.2f), paint);
                canvas.drawRect(cx - dpToPx(1f), cy - dpToPx(1f), cx + dpToPx(1f), cy + dpToPx(5f), paint);
            }
        };
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return Math.round(dp * (metrics.densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT));
    }

    private float dpToPx(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT);
    }

    public static class M3Switch extends View {
        public interface OnCheckedChangeListener {
            void onCheckedChanged(boolean isChecked);
        }

        private boolean isChecked = false;
        private OnCheckedChangeListener listener;

        private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public M3Switch(Context context) {
            super(context);
            setOnClickListener(v -> {
                isChecked = !isChecked;
                invalidate();
                if (listener != null) listener.onCheckedChanged(isChecked);
            });
            iconPaint.setStyle(Paint.Style.STROKE);
            iconPaint.setStrokeCap(Paint.Cap.ROUND);
            iconPaint.setStrokeJoin(Paint.Join.ROUND);
        }

        public void setChecked(boolean checked) {
            this.isChecked = checked;
            invalidate();
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
            this.listener = l;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = Math.round(52 * getResources().getDisplayMetrics().density);
            int h = Math.round(32 * getResources().getDisplayMetrics().density);
            setMeasuredDimension(w, h);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float r = h / 2f;
            float density = getResources().getDisplayMetrics().density;

            bgPaint.setColor(isChecked ? COLOR_ACCENT : COLOR_TRACK_OFF);
            RectF trackRect = new RectF(0, 0, w, h);
            canvas.drawRoundRect(trackRect, r, r, bgPaint);

            float thumbRadius = isChecked ? (r - (4 * density)) : (r - (6 * density));
            float thumbCx = isChecked ? (w - r) : r;
            float thumbCy = h / 2f;

            thumbPaint.setColor(isChecked ? COLOR_THUMB_ON : 0xFF232A2A);
            canvas.drawCircle(thumbCx, thumbCy, thumbRadius, thumbPaint);

            iconPaint.setStrokeWidth(2f * density);
            if (isChecked) {
                iconPaint.setColor(COLOR_ACCENT);
                Path path = new Path();
                path.moveTo(thumbCx - (3f * density), thumbCy);
                path.lineTo(thumbCx - (0.5f * density), thumbCy + (3f * density));
                path.lineTo(thumbCx + (4f * density), thumbCy - (3f * density));
                canvas.drawPath(path, iconPaint);
            } else {
                iconPaint.setColor(0xFF8A9A99);
                float s = 3f * density;
                canvas.drawLine(thumbCx - s, thumbCy - s, thumbCx + s, thumbCy + s, iconPaint);
                canvas.drawLine(thumbCx + s, thumbCy - s, thumbCx - s, thumbCy + s, iconPaint);
            }
        }
    }

    public static class M3Slider extends View {
        public interface OnSliderChangeListener {
            void onProgressChanged(int progress);
        }

        private int progress = 6; // 0..6
        private static final int MAX = 6;
        private OnSliderChangeListener listener;

        private final Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint inactivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint gapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public M3Slider(Context context) {
            super(context);
            activePaint.setColor(COLOR_ACCENT);
            inactivePaint.setColor(COLOR_TRACK_OFF);
            gapPaint.setColor(COLOR_CARD_BG);
        }

        public void setProgress(int p) {
            this.progress = Math.max(0, Math.min(MAX, p));
            invalidate();
        }

        public void setOnSliderChangeListener(OnSliderChangeListener l) {
            this.listener = l;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int h = Math.round(36 * getResources().getDisplayMetrics().density);
            int w = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(w, h);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float r = h / 2f;
            float gapWidth = 4f * getResources().getDisplayMetrics().density;

            float ratio = (float) progress / MAX;
            float splitX = w * ratio;

            RectF fullRect = new RectF(0, 0, w, h);
            canvas.drawRoundRect(fullRect, r, r, inactivePaint);

            if (progress > 0) {
                canvas.save();
                canvas.clipRect(0, 0, splitX, h);
                canvas.drawRoundRect(fullRect, r, r, activePaint);
                canvas.restore();
            }

            if (progress > 0 && progress < MAX) {
                canvas.drawRect(splitX - (gapWidth / 2f), 0, splitX + (gapWidth / 2f), h, gapPaint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                float touchX = event.getX();
                int newProgress = Math.round((touchX / getWidth()) * MAX);
                newProgress = Math.max(0, Math.min(MAX, newProgress));

                if (newProgress != progress) {
                    progress = newProgress;
                    invalidate();
                    if (listener != null) listener.onProgressChanged(progress);
                }
                return true;
            }
            return super.onTouchEvent(event);
        }
    }
}
