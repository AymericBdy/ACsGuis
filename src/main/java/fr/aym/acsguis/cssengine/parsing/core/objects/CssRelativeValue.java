package fr.aym.acsguis.cssengine.parsing.core.objects;

public class CssRelativeValue implements CssValue
{
    private final int value;
    private final Unit unit;

    public CssRelativeValue(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public String stringValue() {
        return value+"%";
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return "CssRelativeValue{" +
                "value=" + value +
                ", unit=" + unit +
                '}';
    }
}
