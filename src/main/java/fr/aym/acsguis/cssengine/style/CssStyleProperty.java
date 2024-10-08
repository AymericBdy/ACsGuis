package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;

import javax.annotation.Nullable;

/**
 * A css style property
 * @param <T> The type of the value
 */
public class CssStyleProperty<T>
{
    private final EnumCssStyleProperty property;
    private final T value;
    private final EnumStylePropertyType type;

    /**
     * @param value Raw string value, automatically parsed
     * @throws IllegalArgumentException if it cannot be parsed
     */
    public CssStyleProperty(EnumCssStyleProperty property, CssValue value) {
        this.property = property;
        if(value.getUnit() == CssValue.Unit.STRING) {
            this.type = EnumStylePropertyType.getTypeIfSpecial(value.stringValue());
            if (type.isNormal()) {
                this.value = (T) property.parser.getValue(value.stringValue());
                if (this.value == null)
                    throw new IllegalArgumentException("Cannot parse " + value + " for property " + property);
            } else {
                this.value = null;
            }
        } else {
            this.type = EnumStylePropertyType.NORMAL;
            this.value = (T) value;
        }
    }

    /**
     * @return The value, null if the {@link EnumStylePropertyType} is not "normal"
     */
    @Nullable
    public T getValue() {
        return value;
    }

    /**
     * Tries to apply this value
     *
     * @return true we should let the default behavior happen : inherit
     */
    public boolean apply(EnumSelectorContext context, ComponentStyleManager to) {
        if(getType().isNormal()) {
            ((CssStyleApplier<T>) property.applyFunction).apply(context, this, to);
            return false;
        }
        else if(getType().isNone()) {
            if(property.acceptsNullValue) {
                ((CssStyleApplier<T>) property.applyFunction).apply(context, this, to);
            }
            return false;
        }
        else if(getType().isAuto())
        {
            for(AutoStyleHandler.Priority p : AutoStyleHandler.Priority.values())
            {
                for(AutoStyleHandler a : to.getAutoStyleHandlers())
                {
                    if(a.getPriority(to) == p && a.getModifiedProperties(to).contains(property))
                    {
                        if(a.handleProperty(property, context, to))
                            return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return The type of this value
     */
    public EnumStylePropertyType getType() {
        return type;
    }

    /**
     * @return The target property
     */
    public EnumCssStyleProperty getProperty() {
        return property;
    }

    @Override
    public String toString() {
        return "CssStyleProperty{" +
                "value=" + value +
                ", type=" + type +
                '}';
    }

    /**
     * All possible values types :
     * <ul>
     *     <li>NORMAL : explicit value, "usable as it is"</li>
     *     <li>INHERIT : does nothing and use parent's value</li>
     *     <li>AUTO : compute the value automatically, not supported for all properties and elements (default behavior is inheriting)</li>
     *     <li>NONE : does nothing, doesn't inherits</li>
     * </ul>
     */
    public enum EnumStylePropertyType
    {
        NORMAL, INHERIT, AUTO, NONE;

        public boolean isNone() { return this == NONE; }
        public boolean isNormal() { return this == NORMAL; }
        public boolean isAuto() { return this == AUTO; }

        /**
         * Parses a type, returning NORMAL if it is not another type
         */
        public static EnumStylePropertyType getTypeIfSpecial(String value) {
            if(value.equalsIgnoreCase(INHERIT.name()))
                return INHERIT;
            if(value.equalsIgnoreCase(AUTO.name()))
                return AUTO;
            if(value.equalsIgnoreCase(NONE.name()))
                return NONE;
            return NORMAL;
        }
    }
}
