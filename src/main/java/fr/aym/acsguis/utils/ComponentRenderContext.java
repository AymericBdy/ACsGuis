package fr.aym.acsguis.utils;

import fr.aym.acsguis.component.panel.GuiFrame;

// TODO DOC
public class ComponentRenderContext {
    private final GuiFrame parentGui;
    private final boolean enableScissors;
    private final GuiFrame.GuiType guiType;

    /**
     * Used for the scissors
     */
    private final int screenHeight;

    /**
     * Used for the scissors
     */
    private final float screenScaleX, screenScaleY;

    public ComponentRenderContext(GuiFrame parentGui, boolean enableScissors, GuiFrame.GuiType guiType, int screenHeight, float screenScaleX, float screenScaleY) {
        this.parentGui = parentGui;
        this.enableScissors = enableScissors;
        this.guiType = guiType;
        this.screenHeight = screenHeight;
        this.screenScaleX = screenScaleX;
        this.screenScaleY = screenScaleY;
    }

    public GuiFrame getParentGui() {
        return parentGui;
    }

    public boolean enableScissors() {
        return enableScissors;
    }

    public GuiFrame.GuiType getGuiType() {
        return guiType;
    }

    public float getScreenScaleX() {
        return screenScaleX;
    }

    public float getScreenScaleY() {
        return screenScaleY;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
