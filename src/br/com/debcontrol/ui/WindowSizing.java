package br.com.debcontrol.ui;

import java.awt.*;

/** Usa as dimensões lógicas fornecidas pelo Java, inclusive com escala do Windows. */
public final class WindowSizing {
    private WindowSizing() { }

    public static Rectangle fitBounds(Rectangle available, Dimension preferred) {
        int margin = Math.min(16, Math.max(0, Math.min(available.width, available.height) / 20));
        int width = Math.min(Math.max(1, preferred.width), Math.max(1, available.width - margin * 2));
        int height = Math.min(Math.max(1, preferred.height), Math.max(1, available.height - margin * 2));
        return new Rectangle(available.x + (available.width - width) / 2,
                available.y + (available.height - height) / 2, width, height);
    }

    public static void fit(Window window, Dimension preferred, Dimension minimum) {
        GraphicsConfiguration config = window.getOwner() == null
                ? window.getGraphicsConfiguration() : window.getOwner().getGraphicsConfiguration();
        Rectangle available = new Rectangle(config.getBounds());
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
        available.x += insets.left;
        available.y += insets.top;
        available.width -= insets.left + insets.right;
        available.height -= insets.top + insets.bottom;
        Rectangle bounds = fitBounds(available, preferred);
        window.setMinimumSize(new Dimension(Math.min(minimum.width, bounds.width),
                Math.min(minimum.height, bounds.height)));
        window.setBounds(bounds);
    }
}
