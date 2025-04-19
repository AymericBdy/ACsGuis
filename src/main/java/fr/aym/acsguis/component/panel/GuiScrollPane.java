package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiSlider;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.style.CssPanelStyle;
import fr.aym.acsguis.event.listeners.mouse.IMouseWheelListener;
import fr.aym.acsguis.utils.ComponentRenderContext;

public class GuiScrollPane extends GuiPanel implements IMouseWheelListener {
    protected int lastScrollAmountX;
    protected int lastScrollAmountY;
    protected final GuiSlider xSlider;
    protected final GuiSlider ySlider;

    private boolean autoScroll;

    private boolean slidersNeedsUpdate;

    public GuiScrollPane() {
        xSlider = new GuiSlider(true);
        ySlider = new GuiSlider(false);

        add(xSlider);
        add(ySlider);

        addWheelListener(this);

        withFlowLayout();
    }

    @Override
    protected InternalComponentStyle createStyleManager() {
        return new CssPanelStyle.CssScrollPanelStyle(this);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.SCROLL_PANE;
    }

    public GuiScrollPane setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
        return this;
    }

    public boolean hasAutoScroll() {
        return autoScroll;
    }

    @Override
    public void onMouseWheel(int dWheel) {
        if (isHovered()) {
            GuiSlider slider = xSlider.isVisible() && (xSlider.isHovered() || !ySlider.isVisible()) ? xSlider : ySlider;
            slider.setValue(slider.getValue() + (dWheel / -120.0D * slider.getWheelStep()) * (slider.getMax() - slider.getMin()));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, ComponentRenderContext renderContext) {
        if (slidersNeedsUpdate) {
            //  System.out.println("slide visibles?");
            updateSlidersVisibility();
        }
        super.render(mouseX, mouseY, partialTicks, renderContext);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {
        if (-xSlider.getValue() != lastScrollAmountX || -ySlider.getValue() != lastScrollAmountY) {
            for (GuiComponent component : getChildComponents()) {
                if (component == xSlider || component == ySlider) continue;
                component.getStyle().setOffsetX((int) -xSlider.getValue());
                component.getStyle().setOffsetY((int) -ySlider.getValue());
            }
            lastScrollAmountX = (int) -xSlider.getValue();
            lastScrollAmountY = (int) -ySlider.getValue();
        }

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
    }

    @Override
    public boolean flushRemovedComponents() {
        if (!super.flushRemovedComponents()) {
            return false;
        }
        setSlidersNeedsUpdate();
        return true;
    }

    public void setSlidersNeedsUpdate() {
        slidersNeedsUpdate = true;
    }

    /**
     * Hide the sliders if the maximum effective size is inferior to the rendered size
     * {@link #getMaxWidth()}, {@link #getMaxHeight()}
     */
    public void updateSlidersVisibility() {
        float mxh = getMaxHeight();
        //System.out.println("Updating " + mxh + " " + getHeight() + " " + this + " " + hashCode());
        // System.out.println(childComponents + " // " + queuedComponents);
        xSlider.setMax(getMaxWidth() - getWidth());
        ySlider.setMax(mxh - getHeight());
        //    System.out.println(getWidth() + "/" + getMaxWidth() + " " + getHeight() + "/" + mxh);
        ((InternalComponentStyle) xSlider.getStyle()).setVisible(getMaxWidth() - getWidth() > 0);
        ((InternalComponentStyle) ySlider.getStyle()).setVisible(mxh - getHeight() > 0);
        xSlider.getStyle().refreshStyle(getGui(), EnumCssStyleProperty.WIDTH);
        ySlider.getStyle().refreshStyle(getGui(), EnumCssStyleProperty.HEIGHT);
        slidersNeedsUpdate = false;
    }

    /**
     * @return Return the maximum effective width by by summing the child components' width
     */
    protected float getMaxWidth() {
        float maxWidth = getWidth();
        for (GuiComponent component : getChildComponents()) {
            if (!(component instanceof GuiSlider)) {
                float width = component.getX() + component.getWidth();
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }
        return maxWidth;
    }

    /**
     * @return Return the maximum effective height by by summing the child components' height
     */
    protected float getMaxHeight() {
        float maxHeight = getHeight();
        for (GuiComponent component : getChildComponents()) {
            if (!(component instanceof GuiSlider)) {
                float height = component.getY() + component.getHeight();
                if (height > maxHeight) {
                    maxHeight = height;
                }
            }
        }
        return maxHeight;
    }

    @Override
    public boolean flushComponentsQueue() {
        if (!super.flushComponentsQueue()) {
            return false;
        }
        this.setSlidersNeedsUpdate();
        if (autoScroll) {
            xSlider.setValue(xSlider.getMax());
            ySlider.setValue(ySlider.getMax());
        }
        return true;
    }

    @Override
    public void removeAllChildren() {
        super.removeAllChildren();
        add(xSlider);
        add(ySlider);
    }

    public void scrollXBy(double d) {
        xSlider.setValue(xSlider.getValue() + d);
    }

    public void scrollYBy(double amount) {
        ySlider.setValue(ySlider.getValue() + amount);
    }

    public GuiSlider getxSlider() {
        return xSlider;
    }

    public GuiSlider getySlider() {
        return ySlider;
    }
}
