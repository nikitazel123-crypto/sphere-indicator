import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Генерирует placeholder PNG-текстуры.
 * Вызывается из Gradle task через project.javaexec или напрямую.
 */
public class GenerateTextures {

    public static void generate(File outDir) throws IOException {
        outDir.mkdirs();
        saveTexture(new File(outDir, "sphere_uron3.png"),
                new Color(220, 60, 30), new Color(255, 160, 120));
        saveTexture(new File(outDir, "sphere_bronya3.png"),
                new Color(30, 80, 220), new Color(120, 160, 255));
    }

    private static void saveTexture(File file, Color main, Color highlight) throws IOException {
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Прозрачный фон
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, size, size);
        g.setComposite(AlphaComposite.SrcOver);

        // Основной круг
        g.setColor(main);
        g.fillOval(1, 1, size - 3, size - 3);

        // Контур
        g.setColor(main.darker());
        g.drawOval(1, 1, size - 3, size - 3);

        // Блик
        g.setColor(new Color(highlight.getRed(), highlight.getGreen(), highlight.getBlue(), 180));
        g.fillOval(4, 3, 4, 3);

        g.dispose();
        ImageIO.write(img, "PNG", file);
    }
}
