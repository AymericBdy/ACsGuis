package fr.aym.acsguis.sqript.events;

import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import net.minecraftforge.fml.relauncher.SideOnly;

@Event(feature = @Feature(name = "acsguis load",
        description = "Called when ACsGuis is loaded",
        examples = "on acsguis load:",
        pattern = "acsguis load[ed]"),
        accessors = {})
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class EventOnCssInit extends ScriptEvent {
}
