package fr.aym.acsguis.component.style;

import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.positionning.Position;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.aym.acsguis.utils.IGuiTexture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Handles style of a {@link GuiComponent}
 *
 * @see fr.aym.acsguis.cssengine.style.CssComponentStyleManager
 * @see TextComponentStyleManager
 */
public interface ComponentStyleManager
{
    /**
     * @return The parent style manager
     */
    ComponentStyleManager getParent();

    /**
     * @return The owner of this style manager
     */
    GuiComponent<?> getOwner();

    /**
     * @return The applicable computed css properties (depends on the id, class, parents and custom css of this element)
     */
    CssStackElement getCssStack();

    /**
     * Sets custom css properties and reloads the css stack
     */
    void setCustomParsedStyle(@Nullable Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> data);

    /**
     * @return The applied custom css properties, or null
     */
    @Nullable
    Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> getCustomParsedStyle();

    /**
     * Adds an auto style handler
     * @return this
     */
    ComponentStyleManager addAutoStyleHandler(AutoStyleHandler<?> handler);
    /**
     * Removes an auto style handler
     * @return this
     */
    ComponentStyleManager removeAutoStyleHandler(AutoStyleHandler<?> handler);
    /**
     * @return The auto styles applied to this_component
     */
    Collection<AutoStyleHandler<?>> getAutoStyleHandlers();

    /**
     * Adds this style to the element <br>
     * The style will persist after reloading the element style
     *
     * @param property The property name
     * @param value The new property value
     * @return this
     */
    ComponentStyleManager injectStyle(EnumCssStyleProperties property, String value);
    /**
     * Adds this style to the element <br>
     * The style will persist after reloading the element style
     *
     * @param property The property to add
     * @return this
     */
    ComponentStyleManager injectStyle(CssStyleProperty<?> property);
    /**
     * @return The list of injected style properties, or null if there are none
     */
    @Nullable
    InjectedStyleList getInjectedStyleList();

    /**
     * Refreshes the css of this element
     *
     * @param reloadCssStack If css stack should be reloaded (heavy)
     * @param reason Used for debug, identifies the caller of this function
     */
    void refreshCss(boolean reloadCssStack, String reason);
    /**
     * Reloads the css stack (heavy)
     */
    void reloadCssStack();

    /**
     * @return Return the state of the component
     */
    EnumSelectorContext getContext();

    /**
     * Detects state changes to update the component style (fired each tick)
     */
    void update();

    /**
     * @return The x pos of this component
     */
    Position getXPos();

    /**
     * @return The y pos of this component
     */
    Position getYPos();

    /**
     * @return The computed X render pos of this component
     */
    int getRenderX();

    /**
     * @return The computed y render pos of this component
     */
    int getRenderY();

    /**
     * @return The width of this component
     */
    Size getWidth();

    /**
     * @return The height of this component
     */
    Size getHeight();

    /**
     * @return The computed render width of this component
     */
    int getRenderWidth();

    /**
     * @return The computed render height of this component
     */
    int getRenderHeight();

    /**
     * Fired when the display screen is resized <br>
     * Refreshes the css state
     *
     * @param screenWidth The new screen width
     * @param screenHeight The new screen height
     */
    void resize(int screenWidth, int screenHeight);

    ComponentStyleManager setForegroundColor(int color);
    int getForegroundColor();

    ComponentStyleManager setBorderRadius(CssValue radius);
    int getBorderRadius();

    enum BORDER_POSITION {INTERNAL, EXTERNAL }

    /**
     * @return Return the {@link GuiTextureSprite} for render depending
     * on the component state.
     */
    IGuiTexture getTexture();
    ComponentStyleManager setTexture(IGuiTexture texture);

    int getBackgroundColor();
    ComponentStyleManager setBackgroundColor(int backgroundColor);

    int getZLevel();
    ComponentStyleManager setZLevel(int zLevel);

    boolean isRepeatBackgroundX();
    ComponentStyleManager setRepeatBackgroundX(boolean repeatBackgroundX);

    boolean isRepeatBackgroundY();
    ComponentStyleManager setRepeatBackgroundY(boolean repeatBackgroundY);

    int getOffsetX();
    ComponentStyleManager setOffsetX(int offsetX);
    int getOffsetY();
    ComponentStyleManager setOffsetY(int offsetY);

    ComponentStyleManager setBorderPosition(BORDER_POSITION borderPosition);
    BORDER_POSITION getBorderPosition();

    ComponentStyleManager setBorderSize(CssValue borderSize);
    int getBorderSize();

    /**
     * Allows keeping thin border visible when the gui has a little scale (see {@link fr.aym.acsguis.component.layout.GuiScaler}) <br>
     * API : the border system will be refactored in near future
     * @param inverseScreenScale True to rescale the border bigger, according to the gui scale
     * @return this
     */
    ComponentStyleManager setShouldRescaleBorder(boolean inverseScreenScale);
    /**
     * Allows keeping thin border visible when the gui has a little scale (see {@link fr.aym.acsguis.component.layout.GuiScaler}) <br>
     * API : the border system will be refactored in near future
     * @return True to rescale the border bigger, according to the gui scale
     */
    boolean shouldRescaleBorder();

    ComponentStyleManager setBorderColor(int borderColor);
    int getBorderColor();

    ComponentStyleManager setVisible(boolean enabled);
    boolean isVisible();

    GuiConstants.ENUM_SIZE getTextureVerticalSize();
    ComponentStyleManager setTextureVerticalSize(GuiConstants.ENUM_SIZE textureVerticalSize);

    GuiConstants.ENUM_SIZE getTextureHorizontalSize();
    ComponentStyleManager setTextureHorizontalSize(GuiConstants.ENUM_SIZE textureHorizontalSize);

    int getTextureHeight();
    ComponentStyleManager setTextureHeight(int textureHeight);

    int getTextureWidth();
    ComponentStyleManager setTextureWidth(int textureWidth);
}
