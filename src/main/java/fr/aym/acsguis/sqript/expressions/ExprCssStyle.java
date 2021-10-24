package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.InjectedStyleList;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.sqript.component.ComponentProperties;
import fr.aym.acsguis.sqript.component.ParseableComponent;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;

@Expression(name = "Css style manipulation expression",
        features = {@Feature(
                name = "Set other properties of gui components",
                description = "Sets other properties of gui components, list in the doc",
                examples = "cset checked_state of this component to true",
                pattern = "css {string} of {gui_component}",
                side = Side.CLIENT),
                @Feature(
                        name = "Set other properties of the current component",
                        description = "Sets other properties of the current component, list in the doc. You MUST be in a component block, and not in a component event !",
                        examples = "cset \"color\" to \"green\"",
                        pattern = "css {string}",
                        side = Side.CLIENT)}
)
public class ExprCssStyle extends ScriptExpression {
    @Override
    public ScriptType<String> get(ScriptContext context, ScriptType[] parameters) {
        ScriptType<GuiComponent<?>> param = getMatchedIndex() == 1 ?
                (ScriptType<GuiComponent<?>>) context.getVariable("this_component") : parameters[1];
        String optn = parameters[0].getObject().toString();
        //System.out.println("Try change " + optn + " : WIP");

        ComponentProperties<?, ?> property = findComponentProperty(param.getObject(), optn);
        if (property != null) {
            return new TypeString(property.getValueFromComponent(param.getObject()).toString());
        }

        EnumCssStyleProperties properties = findCssProperty(param.getObject(), optn);
        if (properties != null) {
            InjectedStyleList list = param.getObject().getStyle().getInjectedStyleList();
            if (list != null) {
                return new TypeString(list.getPropertyMap().get(properties).getValue().toString());
            }
        }
        return null;
    }

    private ComponentProperties<?, Object> findComponentProperty(GuiComponent<?> component, String optn) {
        ParseableComponent componentType = ParseableComponent.find(component);
        for (ComponentProperties<?, ?> property : componentType.getProperties()) {
            if (property.getName().equals(optn)) {
                return (ComponentProperties<?, Object>) property;
            }
        }
        return null;
    }

    private EnumCssStyleProperties findCssProperty(GuiComponent<?> component, String optn) {
        for (EnumCssStyleProperties properties : EnumCssStyleProperties.values()) {
            if (properties.key.equals(optn)) {
                return properties;
            }
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptUndefinedReferenceException {
        ScriptType<GuiComponent<?>> param = getMatchedIndex() == 1 ?
                (ScriptType<GuiComponent<?>>) context.getVariable("this_component") : parameters[1];
        String optn = parameters[0].getObject().toString();
        //System.out.println("Try change " + optn + " : WIP");

        ComponentProperties<?, Object> property = findComponentProperty(param.getObject(), optn);
        if (property != null) {
            property.setValueOnComponent(param.getObject(), to.getObject());
            return true;
        }

        EnumCssStyleProperties properties = findCssProperty(param.getObject(), optn);
        if (properties != null) {
            param.getObject().getStyle().injectStyle(properties, to.getObject().toString());
            //System.out.println("Injection success on " + properties.key+" on "+param.getObject());
            param.getObject().getStyle().refreshCss(true, "injection");
            return true;
        }

        return false;
    }
}
