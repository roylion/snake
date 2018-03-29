package cn.roylion.snake.resources;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Roylion on 2018/3/28.
 */
public class Img {

    public static Image bg;
    public static Image body;
    public static Image meat;

    static{
        try {
            bg = getImg("image/snake.jpg");
            body = getImg("image/body.png");
            meat = getImg("image/meat.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片资源
     * @param filePath  文件路径
     * @return
     * @throws IOException
     */
    private static Image getImg(String filePath) throws IOException {
        return ImageIO.read(Img.class.getClassLoader().getResourceAsStream(filePath));
    }

    public static Image resize(Image image, int width, int height){
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        newImage.getGraphics().drawImage(image,0,0,width,height,null);
        return newImage;
    }
}
