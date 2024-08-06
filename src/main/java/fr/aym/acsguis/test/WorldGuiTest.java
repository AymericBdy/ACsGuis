package fr.aym.acsguis.test;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.api.WorldGui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid=TestMod.MOD_ID)
public class WorldGuiTest {
    public static WorldGui worldGui;

    /*
    TODO :
    - système de z-level propre
     */

    @SubscribeEvent
    public static void onRender(RenderWorldLastEvent event) {
        if(worldGui != null && MinecraftForgeClient.getRenderPass() == 2)
            worldGui.render(event.getPartialTicks());
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null) {
            if(Keyboard.isKeyDown(Keyboard.KEY_M) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                ACsGuiApi.getInWorldGuisManager().getWorldGuis().clear();
                ACsGuiApi.getInWorldGuisManager().addWorldGui(new WorldGui(new GuiDnxDebug(), new Vec3d(0.5, 10.5, 0), 45, 25, 2, 1, 500, 250, true));
            }
            /*if(Keyboard.isKeyDown(Keyboard.KEY_M) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                boolean reloadCss = gui.needsCssReload();
                CssReloadEvent.Pre evt = null;
                if (reloadCss) {
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading css...", true);
                    evt = new CssReloadEvent.Pre(new CssReloadOrigin.HotCssReloadOrigin(new CssGuisManager(), gui));
                    if (MinecraftForge.EVENT_BUS.post(evt)) return;
                    evt.getReloadOrigin().loadStyles();
                }
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading inworld gui...", true);
                CssReloadEvent.Pre finalEvent = evt;

                //Don't use ThreadedLoader scheduler : it's not updated after mc launch
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    if (reloadCss) {
                        ACsGuisCssParser.loadFonts((r, e) -> {
                            log.error("Error while loading css font " + r.toString(), e);
                            ACsGuiApi.getErrorTracker().addError("ACsGuis reload", ACsGuiApi.getCssErrorType(), "css_font_load_error", ErrorLevel.LOW, r.toString(), null, e);
                        });
                        finalEvent.getReloadOrigin().postLoad();
                    }
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("", false);

                    gui = null;
                });
            }
            if(gui == null) {
                gui = new GuiDnxDebug();
                gui.getGui().setWorldAndResolution(Minecraft.getMinecraft(), 300, 200);
            }*/
        }
        /*if(event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().player != null) {
            if(worldGui == null) {
                worldGui = new WorldGui(new GuiDnxDebug(), new Vec3d(0.5, 10.5, 0), 45, 25, 2, 1, 1000, 500, true);
            }
            //worldGui = new WorldGui(new GuiDnxDebug(), new Vec3d(0.5, 10.5, 0), 0, 0, 2, 1, 1000, 500, true);
            worldGui.tick();
        }*/
    }

    @SubscribeEvent
    public static void onMouseClick(MouseEvent event) {
        if(worldGui != null) {
            worldGui.handleMouseEvent(event);
        }
    }

    @SubscribeEvent
    public static void drawCursor(RenderGameOverlayEvent.Pre event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && worldGui != null) {
            worldGui.drawHudCrossHairs(event);
        }
    }
}
