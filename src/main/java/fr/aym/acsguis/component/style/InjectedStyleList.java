package fr.aym.acsguis.component.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.parsing.core.objects.*;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;

import java.util.HashMap;
import java.util.Map;

public class InjectedStyleList
{
    private final Map<EnumCssStyleProperties, CssStyleProperty<?>> propertyMap = new HashMap<>();

    public void addProperty(EnumCssStyleProperties property, String value) {
        value = value.trim();
        CssValue cssValue;
        if (!value.contains(" ") && (value.equals("0") || value.endsWith("px"))) {
            cssValue = new CssIntValue(Integer.parseInt(value.replace("px", "")));
        } else if (!value.contains(" ") && value.endsWith("%")) {
            cssValue = new CssRelativeValue(Integer.parseInt(value.replace("%", "")), CssValue.Unit.RELATIVE_INT);
        } else if (!value.contains(" ") && value.endsWith("vw")) {
            cssValue = new CssRelativeValue(Integer.parseInt(value.replace("vw", "")), CssValue.Unit.RELATIVE_TO_WINDOW_WIDTH);
        } else if (!value.contains(" ") && value.endsWith("vh")) {
            cssValue = new CssRelativeValue(Integer.parseInt(value.replace("vh", "")), CssValue.Unit.RELATIVE_TO_WINDOW_HEIGHT);
        } else {
            if(value.startsWith("\""))
                value = value.substring(1);
            if(value.endsWith("\""))
                value = value.substring(0, value.length()-1);
            //System.out.println("SET VALUE OF "+data[0]+" to "+value);
            cssValue = new CssStringValue(value);
        }
        //System.out.println("Property: "+value+" // "+cssValue);
        addProperty(new CssStyleProperty<>(property, cssValue));
    }

    public void addProperty(CssStyleProperty<?> property) {
        propertyMap.put(property.getProperty(), property);
    }

    public Map<EnumCssStyleProperties, CssStyleProperty<?>> getPropertyMap() {
        return propertyMap;
    }

    public void inject(GuiComponent<?> component, CssStackElement inStack) {
        for(Map.Entry<EnumCssStyleProperties, CssStyleProperty<?>> property : propertyMap.entrySet()) {
            inStack.injectProperty(component, property.getKey(), property.getValue());
        }
    }
}
