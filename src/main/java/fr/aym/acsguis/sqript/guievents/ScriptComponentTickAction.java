package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_on_tick",
        pattern = "on component tick:",
        side = Side.CLIENT
)
public class ScriptComponentTickAction extends GuiActionScriptLoop {
    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent<?> component) {
        component.addTickListener(() -> {
            try {
                executeAction(contextProvider.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
