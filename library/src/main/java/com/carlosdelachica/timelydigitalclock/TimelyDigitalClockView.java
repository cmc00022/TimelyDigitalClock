package com.carlosdelachica.timelydigitalclock;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carlosdelachica.timelydigitalclock.model.data.base.TimeSet;
import com.carlosdelachica.timelydigitalclock.model.data.base.TimeUnitSet;
import com.carlosdelachica.timelydigitalclock.model.data.clock.Clock;
import com.nineoldandroids.animation.ObjectAnimator;

import butterknife.ButterKnife;

public class TimelyDigitalClockView extends LinearLayout implements Clock.ClockCallback {

    public static final int DURATION = 500;

    private TimelyView hoursTensView, hoursUnitsView, minutesTensView, minutesUnitsView, secondsTendsView,
            secondsUnitsView;
    private TextView colonText;
    private TextView secondColonText;

    private TimeSet lastTimeSet;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int textSize;
    private int textColor;
    private int secondsTextSize;

    public TimelyDigitalClockView(Context context) {
        super(context);
        init(context, null);
    }

    public TimelyDigitalClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TimelyDigitalClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        setOrientation(HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.timely_digital_clock, this, true);
        bindViews(rootView);
        initClock();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TimelyDigitalClockView,
                0, 0);
        textSize = a.getDimensionPixelSize(R.styleable.TimelyDigitalClockView_dgTextSize, -1);
        secondsTextSize = a.getDimensionPixelSize(R.styleable.TimelyDigitalClockView_dgSecondsTextSize, -1);
        textColor = a.getColor(R.styleable.TimelyDigitalClockView_dgTextColor, -1);
        a.recycle();
    }

    private void bindViews(View rootView) {
        hoursTensView = ButterKnife.findById(rootView, R.id.hoursTensView);
        hoursUnitsView = ButterKnife.findById(rootView, R.id.hoursUnitsView);
        minutesTensView = ButterKnife.findById(rootView, R.id.minutesTensView);
        minutesUnitsView = ButterKnife.findById(rootView, R.id.minutesUnitsView);
        secondsTendsView = ButterKnife.findById(rootView, R.id.secondsTensView);
        secondsUnitsView = ButterKnife.findById(rootView, R.id.secondsUnitsView);
        colonText = ButterKnife.findById(rootView, R.id.digitalClockColon);
        secondColonText = ButterKnife.findById(rootView, R.id.secondDigitalClockColon);
    }

    private void initClock() {
        initProperties();
        new Clock(this);
    }

    private void initProperties() {
        if (textSize != -1) {
            initClockItemsSize();
        }
        if (textColor != -1) {
            initClockItemsTextColor();
        }
        if (secondsTextSize != -1) {
            initSecondItemsTextSize();
        }
    }

    private void initClockItemsSize() {
        hoursTensView.setTextSize(textSize);
        hoursUnitsView.setTextSize(textSize);
        minutesTensView.setTextSize(textSize);
        minutesUnitsView.setTextSize(textSize);
        secondsTendsView.setTextSize(textSize);
        secondsUnitsView.setTextSize(textSize);

        colonText.setTextSize(textSize);
        secondColonText.setTextSize(textSize);
    }

    private void initClockItemsTextColor() {
        hoursTensView.setTextColor(textColor);
        hoursUnitsView.setTextColor(textColor);
        minutesTensView.setTextColor(textColor);
        minutesUnitsView.setTextColor(textColor);
        secondsTendsView.setTextColor(textColor);
        secondsUnitsView.setTextColor(textColor);
        colonText.setTextColor(textColor);
        secondColonText.setTextColor(textColor);
    }

    private void initSecondItemsTextSize() {
        secondsTendsView.setTextSize(secondsTextSize);
        secondsUnitsView.setTextSize(secondsTextSize);
    }

    @Override
    public void onTimeUpdated(TimeSet timeSet) {
        if (lastTimeSet == null) {
            initClockValues(timeSet);
            lastTimeSet = timeSet;
            return;
        }
        if (!timeSet.getHours().equals(lastTimeSet.getHours())) {
            updateTime(timeSet.getHours(), lastTimeSet.getHours());
        }
        if (!timeSet.getMinutes().equals(lastTimeSet.getMinutes())) {
            updateTime(timeSet.getMinutes(), lastTimeSet.getMinutes());
        }
        if (!timeSet.getSeconds().equals(lastTimeSet.getSeconds())) {
            updateTime(timeSet.getSeconds(), lastTimeSet.getSeconds());
        }
        lastTimeSet = timeSet;
    }

    private void initClockValues(TimeSet timeSet) {
        animateTimelyView(timeSet.getHours(), timeSet.getHours(), true, hoursTensView);
        animateTimelyView(timeSet.getHours(), timeSet.getHours(), false, hoursUnitsView);

        animateTimelyView(timeSet.getMinutes(), timeSet.getMinutes(), true, minutesTensView);
        animateTimelyView(timeSet.getMinutes(), timeSet.getMinutes(), false, minutesUnitsView);

        animateTimelyView(timeSet.getSeconds(), timeSet.getSeconds(), true, secondsTendsView);
        animateTimelyView(timeSet.getSeconds(), timeSet.getSeconds(), false, secondsUnitsView);
    }

    private void updateTime(TimeUnitSet timeUnitSet, TimeUnitSet lastTimeUnitSet) {
        if (timeUnitSet.getActualValueTens() != lastTimeUnitSet.getActualValueTens()) {
            udpateTimeUnitView(timeUnitSet, lastTimeUnitSet, true);
        }
        if (timeUnitSet.getActualValueUnit() != lastTimeUnitSet.getActualValueUnit()) {
            udpateTimeUnitView(timeUnitSet, lastTimeUnitSet, false);
        }

    }

    private void udpateTimeUnitView(TimeUnitSet timeUnitSet, TimeUnitSet lastTimeUnitSet,
                                    boolean updateTens) {
        switch (timeUnitSet.getTimeUnitType()) {
            case SECONDS:
                TimelyView timelyView = updateTens ? secondsTendsView : secondsUnitsView;
                animateTimelyView(timeUnitSet, lastTimeUnitSet, updateTens, timelyView);
                break;
            case MINUTES:
                timelyView = updateTens ? minutesTensView : minutesUnitsView;
                animateTimelyView(timeUnitSet, lastTimeUnitSet, updateTens, timelyView);
                break;
            case HOURS:
                timelyView = updateTens ? hoursTensView : hoursUnitsView;
                animateTimelyView(timeUnitSet, lastTimeUnitSet, updateTens, timelyView);
                break;
        }
    }

    private void animateTimelyView(final TimeUnitSet timeUnitSet, final TimeUnitSet lastTimeUnitSet,
                                   final boolean updateTens, final TimelyView timelyView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                final ObjectAnimator animator = timelyView.animate(updateTens ? lastTimeUnitSet.getActualValueTens() : lastTimeUnitSet.getActualValueUnit(),
                        updateTens ? timeUnitSet.getActualValueTens() : timeUnitSet.getActualValueUnit());
                animator.setDuration(DURATION);
                animator.start();

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        textSize = Math.min(textSize,
                (getMeasuredWidth() / 6) + (colonText.getMeasuredWidth() * 2));
        initClockItemsSize();
    }
}
