package com.livejournal.lofe.lofe;

import android.view.View;
import android.view.MotionEvent;

// Класс для обнаружения свайпа на View
public class SwipeDetector implements View.OnTouchListener {

    public enum Action {
        LR, // Слева направо
        RL, // Справа налево
        TB, // Сверху вниз
        BT, // Снизу вверх
        None // не обнаружено действий
    }

    static class SwipeAction {

        long velocityX, velocityY;
        Action action;

        SwipeAction() {
            action = Action.None;
            velocityX = velocityY = 0;
        }
    }

    private static final int HORIZONTAL_MIN_DISTANCE = 100; // Минимальное расстояние для свайпа по горизонтали
    //private static final int VERTICAL_MIN_DISTANCE = 80;    // Минимальное расстояние для свайпа по вертикали
    private static final int VERTICAL_MIN_DISTANCE = 0;
    private float downX, downY, upX, upY;                   // Координаты
    private long downMS;                                    // Момент времени нажатия
    //private Action mSwipeDetected = Action.None;            // Последнее дейтсвие
    private SwipeAction swipeAction = new SwipeAction();

//    public boolean swipeDetected() {
//        return mSwipeDetected != Action.None;
//    }
//
//    public Action getAction() {
//        return mSwipeDetected;
//    }

    boolean swipeDetected() {
        return swipeAction.action != Action.None;
    }

    SwipeAction getAction() {
        return swipeAction;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {     // Определение свайпа
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                downMS = System.currentTimeMillis();
                //mSwipeDetected = Action.None;
                swipeAction.action = Action.None;
                return false;
            }
//            case MotionEvent.ACTION_MOVE:
//                return false;
//
//            case MotionEvent.ACTION_UP: {
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                long swipeTimeMS = System.currentTimeMillis() - downMS;

                swipeAction.velocityX = (long)(Math.abs(deltaX) * 1000 / swipeTimeMS);
                swipeAction.velocityY = (long)Math.abs(deltaY) * swipeTimeMS;

                if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {       // Горизонтальный свайп?

                    if (deltaX < 0) {                                   // Слева направо?
                        //mSwipeDetected = Action.LR;
                        swipeAction.action = Action.LR;
                        return true;
                    }

                    if (deltaX > 0) {                                   // Справа налево
                        //mSwipeDetected = Action.RL;
                        swipeAction.action = Action.RL;
                        return true;
                    }
                } else if (Math.abs(deltaY) > VERTICAL_MIN_DISTANCE) {  // Вертикальный свайп?
                    return false;
//                    if (deltaY < 0) {                                   // Сверху вниз?
//                        //mSwipeDetected = Action.TB;
//                        swipeAction.action = Action.TB;
//                        return false;
//                    }
//
//                    if (deltaY > 0) {                                   // Снизу вверх?
//                        //mSwipeDetected = Action.BT;
//                        swipeAction.action = Action.BT;
//                        return false;
//                    }
                }
                return true;
            }
        }
        return false;
    }
}
