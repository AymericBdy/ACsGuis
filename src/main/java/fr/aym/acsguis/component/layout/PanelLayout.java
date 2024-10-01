package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Layouts automatically compute position, width and height of all elements added into the GuiPanel using this layout <br>
 *     The computed properties must be set to "auto" in the css sheets
 *
 * @see GridLayout
 */
public interface PanelLayout<T extends ComponentStyleManager> extends AutoStyleHandler<T>
{
    @Override
    default Priority getPriority(T forT) {
        return Priority.LAYOUT;
    }

    @Override
    default boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, T target) {
        if(property == EnumCssStyleProperty.TOP)
        {
            float val = getY(target);
            if(val != target.getRenderY())
            {
                target.getYPos().setAbsolute(val, GuiConstants.ENUM_RELATIVE_POS.START);
            }
            return true;
        }
        else if(property == EnumCssStyleProperty.LEFT)
        {
            float val = getX(target);
            if(val != target.getRenderX())
            {
                target.getXPos().setAbsolute(val, GuiConstants.ENUM_RELATIVE_POS.START);
            }
            return true;
        }
        else if(property == EnumCssStyleProperty.WIDTH)
        {
            float val = getWidth(target);
            if(val != target.getRenderWidth())
            {
                target.getWidth().setAbsolute(val);
            }
            return true;
        }
        else if(property == EnumCssStyleProperty.HEIGHT)
        {
            float val = getHeight(target);
            if(val != target.getRenderHeight())
            {
                target.getHeight().setAbsolute(val);
            }
            return true;
        }
        return false;
    }

    List<EnumCssStyleProperty> modifiedProperties = Arrays.asList(EnumCssStyleProperty.TOP, EnumCssStyleProperty.LEFT, EnumCssStyleProperty.WIDTH, EnumCssStyleProperty.HEIGHT);

    @Override
    default Collection<EnumCssStyleProperty> getModifiedProperties(T target) {
        return modifiedProperties;
    }

    float getX(T target);
    float getY(T target);
    float getWidth(T target);
    float getHeight(T target);

    /**
     * Resets the layouts
     */
    void clear();

    void setContainer(GuiPanel container);
}
