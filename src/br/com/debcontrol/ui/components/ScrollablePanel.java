package br.com.debcontrol.ui.components;

import javax.swing.*;
import java.awt.*;

/** Adapta o conteúdo ao espaço disponível e mantém rolagem em áreas pequenas. */
public final class ScrollablePanel extends JPanel implements Scrollable {
    private final Dimension contentMinimum;

    public ScrollablePanel(JComponent content, int minimumWidth, int minimumHeight) {
        super(new BorderLayout());
        contentMinimum = new Dimension(minimumWidth, minimumHeight);
        setOpaque(false);
        add(content, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferred = super.getPreferredSize();
        return new Dimension(Math.max(contentMinimum.width, preferred.width),
                Math.max(contentMinimum.height, preferred.height));
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle visible, int direction, int orientation) { return 24; }
    @Override public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
        return Math.max(24, (orientation == SwingConstants.VERTICAL ? visible.height : visible.width) - 24);
    }
    @Override public boolean getScrollableTracksViewportWidth() {
        return getParent() instanceof JViewport viewport && viewport.getWidth() >= getPreferredSize().width;
    }
    @Override public boolean getScrollableTracksViewportHeight() {
        return getParent() instanceof JViewport viewport && viewport.getHeight() >= getPreferredSize().height;
    }
}
