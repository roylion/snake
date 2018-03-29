package cn.roylion.snake.enumeration;

import java.awt.event.KeyEvent;

/**
 * Created by Administrator on 2018/3/28.
 */
public enum MoveStatus {
    UP(KeyEvent.VK_UP),
    DOWN(KeyEvent.VK_DOWN),
    LEFT(KeyEvent.VK_LEFT),
    RIGHT(KeyEvent.VK_RIGHT);

    private int keyCode;

    MoveStatus(int keyCode){
        this.keyCode = keyCode;
    }

}
