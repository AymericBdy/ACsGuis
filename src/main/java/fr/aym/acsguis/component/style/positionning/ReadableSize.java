package fr.aym.acsguis.component.style.positionning;

import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.utils.GuiConstants;

public interface ReadableSize {
    float getRawValue();

    ReadableSizeValue getValue();

    ReadableSizeValue getMinValue();

    ReadableSizeValue getMaxValue();

    float computeValue(InternalComponentStyle componentStyle, int screenWidth, int screenHeight, float parentSize);

    boolean isDirty();

    interface ReadableSizeValue {
        float getRawValue();

        GuiConstants.ENUM_SIZE type();

        float computeValue(float screenWidth, float screenHeight, float parentSize);
    }
}
