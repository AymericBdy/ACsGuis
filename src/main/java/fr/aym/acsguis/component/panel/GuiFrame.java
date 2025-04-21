package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.style.CssPanelStyle;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.cssengine.v2.GuiOrchestrator;
import fr.aym.acsguis.event.listeners.IKeyboardListener;
import fr.aym.acsguis.utils.ACsScaledResolution;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public abstract class GuiFrame extends GuiPanel implements IKeyboardListener {

    /**
     * The instance of the GuiScreen linked to this GuiFrame
     */
    protected final APIGuiScreen guiScreen;

    private ACsScaledResolution resolution = new ACsScaledResolution(GuiComponent.mc);

    protected boolean pauseGame = true;
    protected boolean enableRepeatEvents = true;
    protected boolean escapeQuit = true;
    /**
     * You can disable Minecraft vanilla guiScale scaling here
     */
    protected boolean applyMcScale = true;

    protected GuiType guiType = GuiType.ON_SCREEN;

    /**
     * Either to reload all css sheets when the gui is loaded, only for creating/debugging the gui
     */
    protected boolean needsCssReload = false;
    /**
     * Enables show debug option by pressing 'K' on the keyboard while hovering a component
     */
    protected boolean enableDebugPanel = false;

    public long lastClickTime;
    public int mouseX, mouseY;
    public int mouseButton;
    public int lastMouseX, lastMouseY;
    public int lastPressedX, lastPressedY;
    public List<String> hoveringText;

    public static boolean debugInfoCompiled;
    private static GuiScrollPane debugPane;

    public static void setupDebug(ComponentStyle parent, List<String> hoveringDebugText) {
        debugPane.removeAllChildren();
        hoveringDebugText.forEach(s -> debugPane.add(new GuiLabel(s)));
        if (parent != null) {
            debugPane.add(new GuiButton("Parent").addClickListener((x, y, b) -> {
                parent.getOwner().displayComponentOnDebugPane();
            }));
        }
        debugPane.updateSlidersVisibility();
        debugInfoCompiled = true;
    }

    /**
     * @since 22/09/2020 by Aym'
     */
    private final GuiScaler scale;

    public GuiFrame(GuiScaler scale) {
        this.scale = scale;
        this.guiScreen = new APIGuiScreen(this);
        getStyleCustomizer().withAutoStyle(EnumCssStyleProperty.WIDTH, t -> t.getWidth().setRelative(1, CssValue.Unit.RELATIVE_TO_PARENT))
                .withAutoStyle(EnumCssStyleProperty.HEIGHT, t -> t.getHeight().setRelative(1, CssValue.Unit.RELATIVE_TO_PARENT));
        setFocused(true);
        addKeyboardListener(this);

        debugInfoCompiled = true;
        debugPane = new GuiScrollPane();
        debugPane.setGui(this.guiScreen);
        debugPane.setCssId("css_debug_pane");
    }

    /**
     * @return The list of css style sheets used by the gui, default style must not be included
     */
    public abstract List<ResourceLocation> getCssStyles();

    /**
     * @return True to add default style sheet to this gui, recommended
     */
    public boolean usesDefaultStyle() {
        return true;
    }

    /**
     * If you return true, you should use the ACsGuiApi class to show your gui, in order to make the reload effective
     *
     * @return True to reload all css sheets when the gui is loaded, only for creating/debugging the gui
     */
    public boolean needsCssReload() {
        return needsCssReload;
    }

    public void setNeedsCssReload(boolean needsCssReload) {
        this.needsCssReload = needsCssReload;
    }

    /**
     * @deprecated Replaced by isEnableDebugPanel()
     */
    @Deprecated
    public boolean allowDebugInGui() {
        return enableDebugPanel;
    }

    /**
     * @return True to enable show debug option by pressing 'K' on the keyboard while hovering a component
     */
    public boolean isEnableDebugPanel() {
        return enableDebugPanel;
    }

    public void setEnableDebugPanel(boolean enableDebugPanel) {
        this.enableDebugPanel = enableDebugPanel;
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            if (debugPane != null && debugPane.getChildComponents().size() > 2) {
                debugPane.removeAllChildren();
            } else if (doesEscapeQuit()) {
                GuiComponent.mc.displayGuiScreen(null);
            }
        }
    }

    public GuiFrame enableRepeatEvents(boolean enableRepeatEvents) {
        this.enableRepeatEvents = enableRepeatEvents;
        return this;
    }

    /**
     * Directly handles keyboard events from GuiScreen.handleKeyboardInput
     *
     * @param keyPressed If the key is pressed (or released)
     * @param keyCode    The key code
     * @param typedChar  The typed char
     */
    public void onKeyboardEvent(boolean keyPressed, int keyCode, char typedChar) {
        if (isEnableDebugPanel() && keyCode == Keyboard.KEY_K) {
            debugInfoCompiled = false;
            return;
        }
        if (keyCode == 0 && typedChar >= ' ' || keyPressed) {
            this.keyTyped(typedChar, keyCode);
        }
    }

    public boolean doubleClick() {
        return Minecraft.getSystemTime() - lastClickTime <= 500;
    }

    public boolean press() {
        return Minecraft.getSystemTime() - lastClickTime <= 500;
    }

    public GuiScaler getScale() {
        return scale;
    }

    public float getScaledX() {
        return guiScreen.scaleX;
    }

    public float getScaleY() {
        return guiScreen.scaleY;
    }

    @Override
    protected InternalComponentStyle createStyleManager() {
        InternalComponentStyle s = new CssPanelStyle(this) {
            @Override
            public void updateComponentPosition(int screenWidth, int screenHeight) {
                //Re-compute scales after computation of width and height
                float[] scale = getScale().getScale(getResolution(), mc.displayWidth, getResolution().getScaledWidth(), getStyle().getRenderWidth(),
                        mc.displayHeight, getResolution().getScaledHeight(), getStyle().getRenderHeight());
                getGuiScreen().scaleX = scale[0];
                getGuiScreen().scaleY = scale[1];

                //And adapt component position (for relative positions, eg : center of the screen)
                //Fix 23.05.23 : don't use given screenWidth and screenHeight because the old scale was applied
                int parentWidth = (int) (getResolution().getScaledWidth() / getGuiScreen().scaleX);
                int parentHeight = (int) (getResolution().getScaledHeight() / getGuiScreen().scaleY);

                computedX = getXPos().computeValue(this, parentWidth, parentHeight, parentWidth, getRenderWidth());
                computedY = getYPos().computeValue(this, parentWidth, parentHeight, parentHeight, getRenderHeight());
            }
        };
        s.getCustomizer().withAutoStyles(this, EnumCssStyleProperty.HEIGHT);
        return s;
    }

    public ACsScaledResolution getResolution() {
        return resolution;
    }

    public void setResolution(ACsScaledResolution resolution) {
        this.resolution = resolution;
    }

    /**
     * Instance of the GuiScreen linked to this GuiFrame
     */
    public class APIGuiScreen extends GuiScreen {
        private float scaleX = 1, scaleY = 1;
        protected final GuiFrame frame;
        private final GuiOrchestrator orchestrator = new GuiOrchestrator(this);

        APIGuiScreen(GuiFrame frame) {
            this.frame = frame;
            frame.guiOpen();
        }

        public GuiOrchestrator getOrchestrator() {
            return orchestrator;
        }

        @Override
        public void setWorldAndResolution(Minecraft mc, int width, int height) {
            super.setWorldAndResolution(mc, width, height);
            setResolution(new ACsScaledResolution(mc,
                    guiType == GuiType.IN_WORLD ? width : mc.displayWidth, guiType == GuiType.IN_WORLD ? height : mc.displayHeight,
                    isApplyMcScale()));
            frame.resize(this, width, height);
            debugPane.resize(this, width, height);

            //Needed for scale
            getStyle().update(this);
            debugPane.getStyle().update(this);
        }

        @Override
        public void updateScreen() {
            frame.tick();
            debugPane.tick();
        }

        @Override
        public void initGui() {
            flushComponentsQueue();
            debugPane.flushComponentsQueue();
            flushRemovedComponents();
            debugPane.flushRemovedComponents();
            Keyboard.enableRepeatEvents(enableRepeatEvents);
            ((InternalComponentStyle) frame.getStyle()).setVisible(true);
        }

        @Override
        public void onGuiClosed() {
            enableRepeatEvents(false);
            ((InternalComponentStyle) frame.getStyle()).setVisible(false);
            frame.guiClose();
            debugPane.guiClose();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawScreen(mouseX, mouseY, partialTicks, new ComponentRenderContext(getFrame(), true, guiType,
                    mc.displayHeight, scaleX * getResolution().getScaleFactor(), scaleY * getResolution().getScaleFactor()));
        }

        public void drawScreen(int mouseX, int mouseY, float partialTicks, ComponentRenderContext renderContext) {
            orchestrator.processQueue();

            hoveringText = null;

            int scaledMouseX = (int) (mouseX / scaleX);
            int scaledMouseY = (int) (mouseY / scaleY);

            GuiFrame.this.mouseX = scaledMouseX;
            GuiFrame.this.mouseY = scaledMouseY;
            if (scaledMouseX != lastMouseX || scaledMouseY != lastMouseY) {
                lastMouseX = scaledMouseX;
                lastMouseY = scaledMouseY;
                frame.mouseMoved(scaledMouseX, scaledMouseY, true);
                if (debugPane.getChildComponents().size() > 2) {
                    debugPane.mouseMoved(mouseX, mouseY, true);
                }
            }

            GlStateManager.scale(scaleX, scaleY, 1);
            frame.scale.onApplyScale(scaleX, scaleY);
            frame.render(scaledMouseX, scaledMouseY, partialTicks, renderContext);
            frame.scale.onRemoveScale(scaleX, scaleY);
            GL11.glScalef(1 / scaleX, 1 / scaleY, 1);

            if (hoveringText != null && !hoveringText.isEmpty())
                GuiAPIClientHelper.drawHoveringText(getResolution(), hoveringText, mouseX, mouseY);

            if (debugPane.getChildComponents().size() > 2) {
                debugPane.render(mouseX, mouseY, partialTicks, renderContext);
            }
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            if (debugPane.getChildComponents().size() > 2 && debugPane.isHovered()) {
                debugPane.mouseWheel(Mouse.getEventDWheel());
            } else {
                frame.mouseWheel(Mouse.getEventDWheel());
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            int scaledMouseX = (int) (mouseX / scaleX);
            int scaledMouseY = (int) (mouseY / scaleY);

            if (debugPane.getChildComponents().size() > 2 && debugPane.isHovered()) {
                debugPane.mouseClicked(mouseX, mouseY, mouseButton, true);
            } else {
                frame.mouseClicked(scaledMouseX, scaledMouseY, mouseButton, true);
            }

            GuiFrame.this.mouseButton = mouseButton;
            lastClickTime = Minecraft.getSystemTime();
            lastPressedX = scaledMouseX;
            lastPressedY = scaledMouseY;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {
            if (debugPane.getChildComponents().size() > 2 && debugPane.isHovered()) {
                debugPane.mouseReleased(mouseX, mouseY, mouseButton);
            } else {
                mouseX /= scaleX;
                mouseY /= scaleY;
                frame.mouseReleased(mouseX, mouseY, mouseButton);
                // update hover state, maybe some components changed during the click
                frame.mouseMoved(mouseX, mouseY, true);
            }
        }

        @Override
        public void handleKeyboardInput() throws IOException {
            frame.onKeyboardEvent(Keyboard.getEventKeyState(), Keyboard.getEventKey(), Keyboard.getEventCharacter());
            this.mc.dispatchKeypresses();
        }

        @Override
        public boolean doesGuiPauseGame() {
            return frame.doesPauseGame();
        }

        public GuiFrame getFrame() {
            return frame;
        }

        public float getScaleX() {
            return scaleX;
        }

        public float getScaleY() {
            return scaleY;
        }
    }

    public boolean doesPauseGame() {
        return pauseGame;
    }

    public GuiFrame setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
        return this;
    }

    public boolean doesEscapeQuit() {
        return escapeQuit;
    }

    public GuiFrame setEscapeQuit(boolean escapeQuit) {
        this.escapeQuit = escapeQuit;
        return this;
    }

    public boolean isApplyMcScale() {
        return applyMcScale;
    }

    public GuiFrame setApplyMcScale(boolean applyMcScale) {
        this.applyMcScale = applyMcScale;
        return this;
    }

    public GuiType getGuiType() {
        return guiType;
    }

    public void setGuiType(GuiType type) {
        this.guiType = type;
        setApplyMcScale(guiType != GuiType.IN_WORLD);
    }

    public APIGuiScreen getGuiScreen() {
        return guiScreen;
    }

    @Override
    public APIGuiScreen getGui() {
        return getGuiScreen();
    }

    public enum GuiType {
        ON_SCREEN,
        OVERLAY,
        IN_WORLD
    }
}
