package fr.aym.acsguis.test;

import fr.aym.acsguis.api.ACsGuiApi;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import fr.aym.acsguis.utils.GuiCssError;

@Mod(modid = "acsguis_test", name = "ACsGuis test mod", version = "1.0.0")
public class ExampleGuiMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //ACsGuiApi.registerStyleSheetToPreload(GuiDnxDebug.RESOURCE_LOCATION);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPress(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
            ACsGuiApi.asyncLoadThenShowGui("example_gui", ExampleGui::new);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            //ACsGuiApi.asyncLoadThenShowGui("error_gui", GuiCssError::new);
            ACsGuiApi.getErrorTracker().getAllErrors().forEach((s, list) -> {
                System.out.println("Error in " + s);
                list.getErrors().forEach((error) -> {
                    System.out.println("  " + error.getCategory()+" "+error.getObject()+" "+error.getMessage());
                });
            });
        }
    }
}
