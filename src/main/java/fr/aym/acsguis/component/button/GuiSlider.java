package fr.aym.acsguis.component.button;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiSlider extends GuiPanel implements IMouseClickListener, IMouseExtraClickListener {
    protected final List<ISliderListener> sliderListeners = new ArrayList<>();
    protected final GuiSliderButton sliderButton;
    protected final boolean horizontal;

    protected double min = 0, max = 1, value = 0;
    protected float step = 0.1F, wheelStep = 0.1F;

    public GuiSlider(boolean horizontal) {
        this.horizontal = horizontal;

        add(sliderButton = new GuiSliderButton());
        getStyleCustomizer()
                .withAutoStyle(EnumCssStyleProperty.BACKGROUND_COLOR, t -> t.setBackgroundColor(new Color(0, 0, 0, 0.5f).getRGB()));

        addClickListener(this);
        addExtraClickListener(this);
    }

    @Override
    public EnumComponentType getType() {
        return horizontal ? EnumComponentType.SLIDER_HORIZONTAL : EnumComponentType.SLIDER_VERTICAL;
    }

    public GuiSlider setValue(double value) {
        value = Math.round(value / step) * step;
        this.value = MathHelper.clamp(value, min, max);
        if (horizontal) {
            sliderButton.getStyleCustomizer().setPosition((float) (getRelativeValue() * (getWidth() - sliderButton.getWidth())), 0);
        } else {
            sliderButton.getStyleCustomizer().setPosition(0, (float) (getRelativeValue() * (getHeight() - sliderButton.getHeight())));
        }
        for (ISliderListener lis : sliderListeners) {
            lis.onSliderChanged(this.value);
        }
        return this;
    }

    protected void changeValue(int mouseX, int mouseY) {
        double relValue;
        if (horizontal) {
            relValue = (mouseX - getScreenX() - sliderButton.getWidth() / 2) / (double) (getWidth() - sliderButton.getWidth());
        } else {
            relValue = (mouseY - getScreenY() - sliderButton.getHeight() / 2) / (double) (getHeight() - sliderButton.getHeight());
        }
        setValue(min + MathHelper.clamp(relValue, 0, 1) * (max - min));
    }

    @Override
    public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isHovered()) {
            return;
        }
        changeValue(mouseX, mouseY);
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    protected class GuiSliderButton extends GuiButton implements IMouseMoveListener {
        protected GuiSliderButton() {
            super();
            addMoveListener(this);
            getStyleCustomizer().withAutoStyles((property, context, target) -> {
                if (property == EnumCssStyleProperty.LEFT && horizontal) {
                    target.getXPos().setAbsolute((int) (getRelativeValue() * (GuiSlider.this.getWidth() - sliderButton.getWidth())));
                    return true;
                }
                if (property == EnumCssStyleProperty.WIDTH && horizontal) {
                    target.getWidth().setAbsolute((float) Math.max(20, GuiSlider.this.getWidth() - getMax()));
                    return true;
                }
                if (property == EnumCssStyleProperty.TOP && !horizontal) {
                    target.getYPos().setAbsolute((int) (getRelativeValue() * (GuiSlider.this.getHeight() - sliderButton.getHeight())));
                    return true;
                }
                if (property == EnumCssStyleProperty.HEIGHT && !horizontal) {
                    target.getHeight().setAbsolute((float) Math.max(20, GuiSlider.this.getHeight() - getMax()));
                    return true;
                }
                return false;
            }, EnumCssStyleProperty.LEFT, EnumCssStyleProperty.TOP, EnumCssStyleProperty.WIDTH, EnumCssStyleProperty.HEIGHT);
        }

        @Override
        public void onMouseMoved(int mouseX, int mouseY) {
            if (isPressed()) {
                changeValue(mouseX, mouseY);
            }
        }

        @Override
        public void onMouseHover(int mouseX, int mouseY) {
        }

        @Override
        public void onMouseUnhover(int mouseX, int mouseY) {
        }
    }

    public interface ISliderListener {
        void onSliderChanged(double value);
    }

    public GuiSlider addSliderListener(ISliderListener lis) {
        sliderListeners.add(lis);
        return this;
    }

    public double getValue() {
        return value;
    }

    public double getRelativeValue() {
        if (max <= min) {
            return 0;
        }
        return (this.value - min) / (max - min);
    }

    public GuiSlider setMin(double min) {
        this.min = min;
        setValue(value);
        return this;
    }

    public GuiSlider setMax(double max) {
        this.max = max;
        setValue(value);
        return this;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public GuiSlider setStep(float step) {
        this.step = step;
        return this;
    }

    public GuiSlider setWheelStep(float wheelStep) {
        this.wheelStep = wheelStep;
        return this;
    }

    public float getWheelStep() {
        return wheelStep;
    }

    public static class InternalPanelSlider extends GuiSlider {
        public InternalPanelSlider(boolean horizontal) {
            super(horizontal);
        }
    }
}
