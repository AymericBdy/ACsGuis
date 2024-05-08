package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.utils.GuiConstants;

import java.util.*;

public class FlowLayout implements PanelLayout<ComponentStyleManager> {
    private final Map<GuiComponent<?>, ComponentPosition> cache = new HashMap<>();
    private int currentX;
    private int lastWidth;
    private int currentY;
    private int lastHeight;
    private GuiPanel container;

    public void placeElement(ComponentStyleManager target) {
        switch (target.getDisplay()) {
            case BLOCK:
                currentX = 0;
                currentY += lastHeight;
                break;
            case INLINE:
                currentX += lastWidth;
                //TODO LINE WRAPPING
                //TODO C QUOI LA DIFF AVEC INLINE_BLOCK ??
                break;
        }
        if (target.getDisplay() != GuiConstants.COMPONENT_DISPLAY.NONE) {
            lastWidth = target.getRenderWidth();
            lastHeight = target.getRenderHeight();
        }
        ComponentPosition pos = new ComponentPosition(currentX, currentY);
        cache.put(target.getOwner(), pos);
    }

    @Override
    public int getX(ComponentStyleManager target) {
        if (!cache.containsKey(target.getOwner()))
            placeElement(target);
        return cache.get(target.getOwner()).x;
    }

    @Override
    public int getY(ComponentStyleManager target) {
        if (!cache.containsKey(target.getOwner()))
            placeElement(target);
        return cache.get(target.getOwner()).y;
    }

    @Override
    public int getWidth(ComponentStyleManager target) {
        throw new UnsupportedOperationException("Flow layout does not support width computation");
    }

    @Override
    public int getHeight(ComponentStyleManager target) {
        throw new UnsupportedOperationException("Flow layout does not support height computation");
    }

    private final List<EnumCssStyleProperties> modifiedProperties = Arrays.asList(EnumCssStyleProperties.TOP, EnumCssStyleProperties.LEFT);

    @Override
    public Collection<EnumCssStyleProperties> getModifiedProperties(ComponentStyleManager target) {
        return modifiedProperties;
    }

    @Override
    public void clear() {
        cache.clear();
        currentX = 0;
        currentY = 0;
        lastWidth = 0;
        lastHeight = 0;
    }

    @Override
    public void setContainer(GuiPanel container) {
        if (this.container != null)
            throw new IllegalArgumentException("Layout already used in " + this.container);
        this.container = container;
    }

    public static class ComponentPosition {
        public int x, y;
        public int width, height;

        public ComponentPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
