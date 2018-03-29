package cn.roylion.snake;

import cn.roylion.snake.enumeration.MoveStatus;
import cn.roylion.snake.enumeration.Status;
import cn.roylion.snake.pojo.Body;
import cn.roylion.snake.pojo.Cell;
import cn.roylion.snake.pojo.Meat;
import cn.roylion.snake.resources.ConfigConsts;
import cn.roylion.snake.resources.Img;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Roylion on 2018/3/28.
 */
public class Snake extends JPanel {
    private static final LinkedList<Body> BODY = new LinkedList<Body>();
    private static final LinkedList<String> menus = new LinkedList<String>();
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private Status statu;

    private MoveStatus moveStatus;

    private Meat meat;

    private LongAdder count;

    static{
        int x = ConfigConsts.COL/2;
        int y = ConfigConsts.ROW/2;
        int i = ConfigConsts.ROW - y;

        if(i>1) {
            BODY.add(new Body(x, y++));
        }
        if(i>2) {
            BODY.add(new Body(x, y++));
        }
        if(i>3) {
            BODY.add(new Body(x, y++));
        }
        if(i>4) {
            BODY.add(new Body(x, y));
        }
    }

    public Snake(){
        statu = Status.MENU;
        moveStatus = MoveStatus.UP;
        meat = new Meat(0,0);
        count = new LongAdder();
        generateMeat();
    }

    public void action(){
        Timer t = new Timer();

        KeyListener kl = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (statu) {
                    case RUNNING:
                        proessRunningKey(e);
                        break;
                    case MENU:
                    case PAUSE:
                        proessPauseKey(e);
                        break;
                    case GAME_OVER:
                    case CLEARANCE:
                        proessGameOverKey(e);
                        break;
                }

            }
        };

        addKeyListener(kl);
        setFocusable(true);
        requestFocusInWindow();

        t.schedule(new TimerTask() {
            @Override
            public void run() {
               if(statu == Status.RUNNING && count.intValue() % ConfigConsts.FRAME == 0 ){
                   moveAction();
                   count.reset();
               }
               repaint();
               count.increment();
            }
        },0,1000 / (ConfigConsts.FRAME * ConfigConsts.SPEED));
    }

    public void moveAction(){
        Cell head = BODY.getFirst();
        Cell second = BODY.size()>1 ? BODY.get(1) : null;

        int x = head.getX();
        int y = head.getY();
        switch(moveStatus){
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
            default:
        }
        // 修正
        if(second!=null && x == second.getX() && y == second.getY()){
            switch (moveStatus){
                case UP:
                    moveStatus = moveStatus.DOWN;
                    break;
                case DOWN:
                    moveStatus = moveStatus.UP;
                    break;
                case LEFT:
                    moveStatus = moveStatus.RIGHT;
                    break;
                case RIGHT:
                    moveStatus = moveStatus.LEFT;
                    break;
                default:
            }
            moveAction();
            return;
        }

        // eating food
        if(x == meat.getX() && y == meat.getY()){
            BODY.addFirst(new Body(x,y));
            generateMeat();
            return;
        }

        // outBounds 出界
        if(x < 0 || x >= ConfigConsts.COL || y < 0 || y >= ConfigConsts.ROW){
            statu = Status.GAME_OVER;
            return;
        }

        // crashBody 身体
        for(Cell b : BODY){
            if(b.getX() == x && b.getY() == y){
                statu = Status.GAME_OVER;
                return;
            }
        }

        //移动
        Body body = BODY.removeLast();
        body.setX(x);
        body.setY(y);
        BODY.addFirst(body);
    }

    public void proessRunningKey(KeyEvent e){
        switch (e.getKeyCode()){
            case KeyEvent.VK_UP:
                if(moveStatus != MoveStatus.DOWN) {
                    moveStatus = MoveStatus.UP;
                }
                break;
            case KeyEvent.VK_DOWN:
                if(moveStatus != MoveStatus.UP) {
                    moveStatus = MoveStatus.DOWN;
                }
                break;
            case KeyEvent.VK_LEFT:
                if(moveStatus != MoveStatus.RIGHT) {
                    moveStatus = MoveStatus.LEFT;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if(moveStatus != MoveStatus.LEFT) {
                    moveStatus = MoveStatus.RIGHT;
                }
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_P:
                statu = Status.PAUSE;
                break;
            default:
        }

    }

    public void proessPauseKey(KeyEvent e){
        switch (e.getKeyCode()){
            case KeyEvent.VK_S:
                statu = Status.RUNNING;
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            default:
        }
    }

    public void proessGameOverKey(KeyEvent e){
        switch (e.getKeyCode()){
            case KeyEvent.VK_C:
                generateMeat();
                resetBody();
                statu = Status.RUNNING;
                moveStatus = MoveStatus.UP;
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            default:
        }
    }

    public void generateMeat(){
        if (BODY.size() == ConfigConsts.COL*ConfigConsts.ROW){
            statu = Status.CLEARANCE;
            meat = null;
            return;
        }
        boolean flag;
        int x,y;
        do {
            x = random.nextInt(ConfigConsts.COL);
            y = random.nextInt(ConfigConsts.ROW);

            flag = false;
            for(Cell b : BODY){
                if(b.getX() == x && b.getY() == y){
                    flag = true;
                    break;
                }
            }
        } while(flag);
        meat.setX(x);
        meat.setY(y);
    }

    public void resetBody(){
        BODY.clear();
        int x = ConfigConsts.COL/2;
        int y = ConfigConsts.ROW/2;
        int i = ConfigConsts.ROW - y;

        if(i>1) {
            BODY.add(new Body(x, y++));
        }
        if(i>2) {
            BODY.add(new Body(x, y++));
        }
        if(i>3) {
            BODY.add(new Body(x, y++));
        }
        if(i>4) {
            BODY.add(new Body(x, y));
        }
    }

    @Override
    public void paint(Graphics g) {
        Image bg = Img.resize(Img.bg,ConfigConsts.WIDTH,ConfigConsts.HEIGHT);

        g.drawImage(bg,0,0,null);
        paintBody(g);
        paintMeat(g);
        paintStatu(g);
    }

    public void paintBody(Graphics g){
        Image img = Img.resize(Img.body,ConfigConsts.SIZE,ConfigConsts.SIZE);
        for(Cell b : BODY) {
            g.drawImage(img, ConfigConsts.SIZE * b.getX(), ConfigConsts.SIZE * b.getY(),null);
        }
    }

    public void paintMeat(Graphics g){
        if(meat !=null) {
            Image img = Img.resize(meat.getImage(), ConfigConsts.SIZE, ConfigConsts.SIZE);
            g.drawImage(img, ConfigConsts.SIZE * meat.getX(), ConfigConsts.SIZE * meat.getY(), null);
        }
    }

    public void paintStatu(Graphics g){
        int size = 30;
        g.setFont(new Font("楷体",Font.BOLD,size));
        g.setColor(Color.blue);
        menus.clear();
        switch (statu){
            case MENU:
                menus.addFirst("按'Q'退出游戏");
                menus.addFirst("按'P'暂停游戏");
                menus.addFirst("按'S'开始游戏");
                break;
            case PAUSE:
                menus.addFirst("按'Q'退出游戏");
                menus.addFirst("按'S'继续游戏");
                menus.addFirst("暂停游戏");
                break;
            case GAME_OVER:
                menus.addFirst("按'Q'退出游戏");
                menus.addFirst("按'C'重新开始");
                menus.addFirst("游戏结束");
                break;
            case CLEARANCE:
                menus.addFirst("按'Q'退出游戏");
                menus.addFirst("按'C'重新开始");
                menus.addFirst("恭喜通关");
                break;
            default:
        }

        for(int i = 0; i < menus.size(); i++){
            String s = menus.get(i);
            int y = (i+1)*(size+10);
            g.drawString(s, 0, y);
        }

    }

    public static void main(String[] args) {

        JFrame game = new JFrame("snake");
        Snake snake = new Snake();
        game.add(snake);
        game.setSize(ConfigConsts.WIDTH,ConfigConsts.HEIGHT);
        game.setLocationRelativeTo(null);
        game.setResizable(false);
        game.setUndecorated(true);
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setVisible(true);

        snake.action();

    }

}
