package fr.isae.mae.ss.y2024;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class CustomSliderUI extends BasicSliderUI {
    public CustomSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new Color(255, 255, 255, 150)); // Semi-transparent white
        g2d.fillRoundRect(trackRect.x, trackRect.y + trackRect.height / 3, trackRect.width, trackRect.height / 3, 10, 10);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new Color(200, 200, 200, 200)); // Semi-transparent light gray
        int thumbWidth = 12;
        int thumbHeight = 12;
        g2d.fillOval(thumbRect.x + thumbRect.width / 2 - thumbWidth / 2,
                (int) (thumbRect.y + thumbRect.height / 2 - thumbHeight / 1.8),
                thumbWidth, thumbHeight);
    }
}
