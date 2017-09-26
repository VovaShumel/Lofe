package com.livejournal.lofe.lofe;

import android.view.View;
import android.view.MotionEvent;

// Класс для обнаружения свайпа на View
public class SwipeDetector implements View.OnTouchListener {

    public static enum Action {
        LR, // Слева направо
        RL, // Справа налево
        TB, // Сверху вниз
        BT, // Снизу вверх
        None // не обнаружено действий
    }

    private static final int HORIZONTAL_MIN_DISTANCE = 100; // Минимальное расстояние для свайпа по горизонтали
    private static final int VERTICAL_MIN_DISTANCE = 80;    // Минимальное расстояние для свайпа по вертикали
    private float downX, downY, upX, upY;                   // Координаты
    private Action mSwipeDetected = Action.None;            // Последнее дейтсвие

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {     // Определение свайпа
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {       // Горизонтальный свайп?

                    if (deltaX < 0) {                                   // Слева направо?
                        mSwipeDetected = Action.LR;
                        return true;
                    }

                    if (deltaX > 0) {                                   // Справа налево
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                } else if (Math.abs(deltaY) > VERTICAL_MIN_DISTANCE) {  // Вертикальный свайп?

                    if (deltaY < 0) {                                   // Сверху вниз?
                        mSwipeDetected = Action.TB;
                        return false;
                    }

                    if (deltaY > 0) {                                   // Снизу вверх?
                        mSwipeDetected = Action.BT;
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}