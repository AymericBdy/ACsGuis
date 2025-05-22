package fr.aym.acsguis.cssengine.selectors;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiSlider;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static fr.aym.acsguis.cssengine.style.EnumCssStyleProperty.*;

/**
 * Chooses which style to apply, depending on the state of the element
 */
public class CssStackElement {
    private final CssStackElement parent;
    private final Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> propertyMap;
    /* Not multi-thread compatible */
    private final List<CssStyleProperty<?>> matchingProperties = new ArrayList<>();
    private CompoundCssSelector universalSelector;

    public CssStackElement(CssStackElement parent, Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> propertyMap) {
        this.parent = parent;
        this.propertyMap = propertyMap;
    }

    public CssStackElement getParent() {
        return parent;
    }

    public void injectProperty(GuiComponent component, EnumCssStyleProperty property, CssStyleProperty<?> value) {
        if (universalSelector == null) {
            universalSelector = new CompoundCssSelector(new CssSelector<>(CssSelector.EnumSelectorType.A_COMPONENT, component), null, null);
            universalSelector.setId(Integer.MAX_VALUE); //Max id to override everything
            if (!propertyMap.containsKey(universalSelector)) {
                propertyMap.put(universalSelector, new HashMap<>());
            }
        }
        propertyMap.get(universalSelector).put(property, value);
    }

    private boolean hasProp(EnumSelectorContext context, EnumCssStyleProperty equivalentProperty, InternalComponentStyle shouldApplyTo) {
        AtomicBoolean equivalentFound = new AtomicBoolean();
        for (Map.Entry<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> entry : propertyMap.entrySet()) {
            CssStyleProperty<?> p = entry.getValue().get(equivalentProperty);
            if(p == null) {
                continue;
            }
            if (!entry.getKey().applies(shouldApplyTo, context)) {
                continue;
            }
            equivalentFound.set(true);
            break;
        }
        return equivalentFound.get();
    }

    public void applyProperty(EnumSelectorContext context, EnumCssStyleProperty property, InternalComponentStyle to) {
        boolean out = false;//to.getOwner() instanceof GuiButton && ((GuiButton)to.getOwner()).getText().equals("Vehicles") && property == EnumCssStyleProperties.TEXTURE && context == EnumSelectorContext.NORMAL;//to.getOwner() instanceof GuiLabel && property == EnumCssStyleProperties.PADDING_LEFT;//(property == EnumCssStyleProperties.VISIBILITY) && to.getOwner() instanceof GuiButton;
        propertyMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
            if (out)
                System.out.println("Try stack " + property + " " + context + " to " + to.getOwner() + " " + e.getKey());
            if (!e.getKey().applies(to, context)) {
                return;
            }
            CssStyleProperty<?> p = e.getValue().get(property);
            if (out)
                System.out.println("Able to do it ! Found prop " + p);
            if (p != null) {
                matchingProperties.add(p);
            }
        });
        if (!matchingProperties.isEmpty()) {
            for (int i = (matchingProperties.size() - 1); i >= 0; i--) {
                if (matchingProperties.get(i).apply(context, to)) {
                    matchingProperties.clear();
                    return;
                }
                //Else, inherit
            }
            matchingProperties.clear();
        }
        if (out)
            System.out.println("Parents : " + getParent() + " " + to.getParent() + " " + matchingProperties);
        if (getParent() != null && to.getParent() != null && property.inheritable) {
            CssStackElement parentStack = this;
            ComponentStyle parentTo = to;
            while (parentStack.getParent() != null && parentTo.getParent() != null) {
                parentStack = parentStack.getParent();
                parentTo = parentTo.getParent();
                ComponentStyle finalParentTo = parentTo;
                parentStack.propertyMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
                    if (out)
                        System.out.println("PAR Try stack " + property + " " + context + " to " + finalParentTo.getOwner() + " " + e.getKey());
                    if (e.getKey().applies(finalParentTo, context)) {
                        CssStyleProperty<?> p = e.getValue().get(property);
                        if (out)
                            System.out.println("PAR Able to do it ! Found prop " + p + " for " + to.getOwner());
                        if (p != null) {
                            matchingProperties.add(p);
                        }
                    }
                });
                if (!matchingProperties.isEmpty()) {
                    for (int i = (matchingProperties.size() - 1); i >= 0; i--) {
                        if (matchingProperties.get(i).apply(context, to)) {
                            matchingProperties.clear();
                            return;
                        }
                        //Else, inherit
                    }
                }
                matchingProperties.clear();
            }
        }
        matchingProperties.clear();
        // If we are there, the property was neither applied, neither inherited. Let's see if there is an AutoStyleHandler for it.
        if(!property.isDefaultAuto) {
            return;
        }
        EnumCssStyleProperty equivalent = null;
        switch (property) {
            case LEFT:
                equivalent = RIGHT;
                break;
            case RIGHT:
                equivalent = LEFT;
                break;
            case TOP:
                equivalent = BOTTOM;
                break;
            case BOTTOM:
                equivalent = TOP;
                break;
        }
        if(equivalent != null && hasProp(context, equivalent, to)) {
            return;
        }
        List<AutoStyleHandler<?>> styleHandlers = to.getCustomizer().getAutoStyleHandlers(property);
        if (styleHandlers == null) {
            return;
        }
        for (AutoStyleHandler.Priority p : AutoStyleHandler.Priority.values()) {
            for (AutoStyleHandler a : styleHandlers) {
                // Fix: make sure layouts aren't applied to sliders (unless described as "auto" in the .css
                if (a.getPriority(to) != p ||(p == AutoStyleHandler.Priority.LAYOUT && to.getOwner() instanceof GuiSlider.InternalPanelSlider)) {
                    continue;
                }
                if (a.handleProperty(property, context, to)) {
                    return;
                }
            }
        }
    }

    public void applyProperties(EnumSelectorContext context, InternalComponentStyle to, EnumCssStyleProperty... properties) {
        for (EnumSelectorContext context1 : EnumSelectorContext.values()) {
            if (context1 != context && !context1.isParent(context)) {
                continue;
            }
            for (EnumCssStyleProperty property : properties) {
                to.getCustomizer().removeChange(property);
                AutoStyleHandler.SimpleStyleFunction styleFunction = to.getCustomizer().getStyleOverride(property);
                if (styleFunction != null) {
                    styleFunction.apply(to);
                    continue;
                }
                applyProperty(context1, property, to);
            }
        }
    }

    public List<String> getProperties(EnumSelectorContext context, ComponentStyle to) {
        List<String> l = new ArrayList<>();
        propertyMap.forEach((sel, props) -> {
            l.add((sel.applies(to, context) ? TextFormatting.GREEN : TextFormatting.RED) + "Selector : ");
            sel.addProperties(l);
            l.add("Properties : ");
            props.forEach((prop, value) -> {
                if (value.getValue() == null)
                    l.add(prop.key + " = auto/none");
                else
                    l.add(prop.key + " = " + value.getValue().toString());
            });
            l.add(TextFormatting.GOLD + "========");
        });
        return l;
    }
}
