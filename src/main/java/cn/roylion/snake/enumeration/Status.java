package cn.roylion.snake.enumeration;

/**
 * Created by Roylion on 2018/3/28.
 */
public enum Status {
    MENU(0),
    PAUSE(1),
    RUNNING(2),
    GAME_OVER(3),
    CLEARANCE(4);

    private int statu;

    Status(int statu){
        this.statu = statu;
    }
}
