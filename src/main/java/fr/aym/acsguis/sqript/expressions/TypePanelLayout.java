package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.layout.PanelLayout;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import net.minecraftforge.fml.relauncher.SideOnly;

@Type(
        name = "panel_layout",
        parsableAs = {}
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class TypePanelLayout extends ScriptType<PanelLayout<?>>
{
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    public String toString() {
        return this.getObject() == null ? "null" : this.getObject().toString();
    }

    public TypePanelLayout(PanelLayout file) {
        super(file);
    }
}
