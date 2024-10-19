package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.component.panel.GuiFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles hud guis display
 */
public class CssHudHandler {
    private final List<GuiFrame.APIGuiScreen> displayedHuds = new ArrayList<>();
    private int displayWidth, displayHeight;

    public List<GuiFrame.APIGuiScreen> getDisplayedHuds() {
        return displayedHuds;
    }

    public void closeAllHudGuis() {
        for (GuiFrame.APIGuiScreen hud : displayedHuds) {
            hud.onGuiClosed();
        }
        displayedHuds.clear();
    }

    public void closeHudGui(GuiFrame hud) {
        displayedHuds.remove(hud.getGuiScreen());
        hud.getGuiScreen().onGuiClosed();
    }

    public void showHudGui(GuiFrame hud) {
        showHudGui(displayedHuds.size(), hud);
    }

    public void showHudGui(int index, GuiFrame hud) {
        GuiFrame.APIGuiScreen currentHUD = hud.getGuiScreen();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        currentHUD.setWorldAndResolution(Minecraft.getMinecraft(), sr.getScaledWidth(), sr.getScaledHeight());
        currentHUD.setFocused(false);
        displayedHuds.add(index, currentHUD);
    }

    @SubscribeEvent
    public void drawHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if(displayedHuds.isEmpty())
                return;
            displayedHuds.forEach(hud -> hud.drawScreen(Integer.MIN_VALUE, Integer.MIN_VALUE, event.getPartialTicks()));
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if(displayedHuds.isEmpty())
                return;
            displayedHuds.forEach(GuiFrame.APIGuiScreen::updateScreen);
            if (Minecraft.getMinecraft().displayWidth != displayWidth || Minecraft.getMinecraft().displayHeight != displayHeight) {
                displayWidth = Minecraft.getMinecraft().displayWidth;
                displayHeight = Minecraft.getMinecraft().displayHeight;
                displayedHuds.forEach(hud -> hud.onResize(Minecraft.getMinecraft(), displayWidth, displayHeight));
            }
        }
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        closeAllHudGuis();
    }
}
