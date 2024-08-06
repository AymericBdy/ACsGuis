package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.api.WorldGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class InWorldGuisManager
{
    private final Map<UUID, WorldGui> worldGuis = new HashMap<>();

    public void addWorldGui(WorldGui gui)
    {
        if(worldGuis.containsKey(gui.getId()))
            throw new IllegalArgumentException("A gui with the same id is already registered !");
        worldGuis.put(gui.getId(), gui);
    }

    public void removeWorldGui(UUID guiId)
    {
        worldGuis.remove(guiId);
    }

    public Map<UUID, WorldGui> getWorldGuis()
    {
        return worldGuis;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        worldGuis.values().forEach(gui -> gui.render(event.getPartialTicks()));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().player != null) {
            worldGuis.values().forEach(WorldGui::tick);
        }
    }

    @SubscribeEvent
    public void onMouseClick(MouseEvent event) {
        for(WorldGui gui : worldGuis.values()) {
            if(gui.handleMouseEvent(event))
                break;
        }
    }

    @SubscribeEvent
    public void drawCursor(RenderGameOverlayEvent.Pre event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            for(WorldGui gui : worldGuis.values()) {
                if(gui.drawHudCrossHairs(event))
                    break;
            }
        }
    }
}
