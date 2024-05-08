package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.component.style.PanelStyleManager;
import fr.aym.acsguis.component.style.TextComponentStyleManager;
import fr.aym.acsguis.component.textarea.GuiProgressBar;
import fr.aym.acsguis.cssengine.CssHelper;
import fr.aym.acsguis.cssengine.DefinitionType;
import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.util.text.TextFormatting;
import org.newdawn.slick.font.effects.ShadowEffect;

import java.awt.*;

import static fr.aym.acsguis.cssengine.parsing.core.objects.CssValue.Unit.ABSOLUTE_INT;
import static fr.aym.acsguis.cssengine.parsing.core.objects.CssValue.Unit.RELATIVE_INT;

public enum EnumCssStyleProperties {
    BACKGROUND_COLOR(CssHelper.COLOR, (ctx, p, c) -> c.setBackgroundColor(p.getValue()), "background-color", false, false, true),
    COLOR(CssHelper.COLOR, (ctx, p, c) -> c.setForegroundColor(p.getValue()), "color", false, false, true),
    BORDER_COLOR(CssHelper.COLOR, (ctx, p, c) -> c.setBorderColor(p.getValue()), "border-color"),
    TEXTURE(CssHelper.TEXTURE_SPRITE, (ctx, p, c) -> c.setTexture(p.getValue()), "background-image", false, true),
    VISIBILITY(CssHelper.STRING, (ctx, p, c) -> c.setVisible(!p.getValue().equals("hidden")), "visibility"),
    FONT_SIZE(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            ((TextComponentStyleManager) c).setFontSize(p.getValue().intValue());
        }
    }, "font-size"),
    FONT_STYLE(CssHelper.STRING, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            switch (p.getValue()) {
                case "italic":
                    ((TextComponentStyleManager) c).setFontColor(TextFormatting.ITALIC);
                    break;
                case "normal":
                    ((TextComponentStyleManager) c).setFontColor(null);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported font style " + p.getValue());
            }
        }
    }, "font-style"),
    FONT_FAMILY(CssHelper.RESOURCE_LOCATION, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            ((TextComponentStyleManager) c).setFontFamily(p.getValue());
        }
    }, "font-family", false, false, true),
    BORDER_WIDTH(CssHelper.CSS_INT, (ctx, p, c) -> c.setBorderSize(p.getValue()), "border-width"),
    BORDER_STYLE(CssHelper.STRING, (ctx, p, c) -> {
        if (!p.getValue().equals("solid")) {
            throw new IllegalArgumentException("Border style " + p.getValue() + " is not supported !");
        }
    }, "border-style"),
    BORDER_POSITION(CssHelper.STRING, (ctx, p, c) -> {
        if (p.getValue().equals("internal")) {
            c.setBorderPosition(ComponentStyleManager.BORDER_POSITION.INTERNAL);
        } else {
            c.setBorderPosition(ComponentStyleManager.BORDER_POSITION.EXTERNAL);
        }
    }, "border-position", false, false, true),
    BACKGROUND_REPEAT(CssHelper.STRING, (ctx, p, c) -> {
        switch (p.getValue()) {
            case "repeat":
                c.setRepeatBackgroundX(true);
                c.setRepeatBackgroundY(true);
                break;
            case "repeat-x":
                c.setRepeatBackgroundX(true);
                c.setRepeatBackgroundY(false);
                break;
            case "repeat-y":
                c.setRepeatBackgroundX(false);
                c.setRepeatBackgroundY(true);
                break;
            case "none":
                c.setRepeatBackgroundX(false);
                c.setRepeatBackgroundY(false);
                break;
            default:
                throw new IllegalArgumentException("Background repeat " + p.getValue() + " is not supported !");
        }
    }, "background-repeat"),
    BACKGROUND_POSITION(CssHelper.STRING, (ctx, p, c) -> {
        if (!p.getValue().equals("top left")) {
            throw new IllegalArgumentException("Background position property is not supported ! It should be defined in the texture property");
        }
    }, "background-position"),
    BACKGROUND_ATTACHMENT(CssHelper.STRING, (ctx, p, c) -> {/* ignore */}, "background-attachment"),
    BACKGROUND_ORIGIN(CssHelper.STRING, (ctx, p, c) -> {/* ignore */}, "background-origin"),
    BACKGROUND_SIZE(CssHelper.STRING, (ctx, p, c) -> {
        if (!p.getValue().equals("auto auto")) {
            throw new IllegalArgumentException("Background size property is not supported ! It should be defined in the texture property");
        }
    }, "background-size"),
    BACKGROUND_CLIP(CssHelper.STRING, (ctx, p, c) -> {/* ignore */}, "background-clip"),
    BORDER_RADIUS(CssHelper.CSS_INT, (ctx, p, c) -> c.setBorderRadius(p.getValue()), "border-radius"),
    TEXT_SHADOW(CssHelper.STRING, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            if (p.getValue().equals("enable")) { //TODO IMPROVE SHADOWS
                ShadowEffect effect = new ShadowEffect();
                effect.setColor(Color.BLACK);
                ((TextComponentStyleManager) c).addEffect(effect);
            } else {
                ((TextComponentStyleManager) c).removeEffect(ShadowEffect.class);
            }
        }
    }, "text-shadow"),
    Z_INDEX(CssHelper.INT, (ctx, p, c) -> c.setZLevel(p.getValue()), "z-index"),
    PADDING_LEFT(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() != ABSOLUTE_INT) {
                ((TextComponentStyleManager) c).setPaddingLeft((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
            } else {
                ((TextComponentStyleManager) c).setPaddingLeft(p.getValue().intValue());
            }
        }
    }, "padding-left"),
    PADDING_TOP(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() != ABSOLUTE_INT) {
                ((TextComponentStyleManager) c).setPaddingTop((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
            } else { //FIXME IMPROVE RELATIVE THINGS
                ((TextComponentStyleManager) c).setPaddingTop(p.getValue().intValue());
            }
        }
    }, "padding-top"),
    PADDING_RIGHT(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() != ABSOLUTE_INT) {
                ((TextComponentStyleManager) c).setPaddingRight((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
            } else {
                ((TextComponentStyleManager) c).setPaddingRight(p.getValue().intValue());
            }
        }
    }, "padding-right"),
    PADDING_BOTTOM(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() != ABSOLUTE_INT) {
                ((TextComponentStyleManager) c).setPaddingBottom((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
            } else {
                ((TextComponentStyleManager) c).setPaddingBottom(p.getValue().intValue());
            }
        }
    }, "padding-bottom"),
    TEXT_ALIGN_HORIZONTAL(CssHelper.STRING, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            switch (p.getValue()) {
                case "left":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.LEFT);
                    break;
                case "right":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.RIGHT);
                    break;
                case "center":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
                    break;
                case "justify":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.JUSTIFY);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown (horizontal) text align : " + p.getValue());
            }
        } else if (c instanceof GuiProgressBar.ProgressBarStyleManager) {
            switch (p.getValue()) {
                case "left":
                    ((GuiProgressBar.ProgressBarStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.LEFT);
                    break;
                case "right":
                    ((GuiProgressBar.ProgressBarStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.RIGHT);
                    break;
                case "center":
                    ((GuiProgressBar.ProgressBarStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
                    break;
                case "justify":
                    ((GuiProgressBar.ProgressBarStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.JUSTIFY);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown (horizontal) text align : " + p.getValue());
            }
        }
    }, "text-align"),
    TEXT_ALIGN_VERTICAL(CssHelper.STRING, (ctx, p, c) -> {
        if (c instanceof TextComponentStyleManager) {
            switch (p.getValue()) {
                case "top":
                    ((TextComponentStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.TOP);
                    break;
                case "bottom":
                    ((TextComponentStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.BOTTOM);
                    break;
                case "center":
                    ((TextComponentStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.CENTER);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown vertical text align : " + p.getValue());
            }
        } else if (c instanceof GuiProgressBar.ProgressBarStyleManager) {
            switch (p.getValue()) {
                case "top":
                    ((GuiProgressBar.ProgressBarStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.TOP);
                    break;
                case "bottom":
                    ((GuiProgressBar.ProgressBarStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.BOTTOM);
                    break;
                case "center":
                    ((GuiProgressBar.ProgressBarStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.CENTER);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown vertical text align : " + p.getValue());
            }
        }
    }, "text-align-vertical"),
    PANEL_LAYOUT(CssHelper.PANEL_LAYOUT, (ctx, p, c) -> {
        if (c instanceof PanelStyleManager) {
            ((PanelStyleManager) c).setLayout(p.getValue());
        }
    }, "component-layout", false, true),
    //NOTE : for the positioning, the order is important ! And other properties as padding must have been set before for labels
    WIDTH(CssHelper.CSS_INT, (ctx, p, c) -> {
        //System.out.println("Set width of "+c.getOwner()+" to "+p.getValue());
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getWidth().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getWidth().setAbsolute(p.getValue().intValue());
        }
    }, "width", true),
    MAX_WIDTH(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getWidth().getMaxValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getWidth().getMaxValue().setAbsolute(p.getValue().intValue());
        }
    }, "max-width"),
    MIN_WIDTH(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getWidth().getMinValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getWidth().getMinValue().setAbsolute(p.getValue().intValue());
        }
    }, "min-width"),
    HEIGHT(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getHeight().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getHeight().setAbsolute(p.getValue().intValue());
        }
    }, "height", true),
    MAX_HEIGHT(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getHeight().getMaxValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getHeight().getMaxValue().setAbsolute(p.getValue().intValue());
        }
    }, "max-height"),
    MIN_HEIGHT(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getHeight().getMinValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getHeight().getMinValue().setAbsolute(p.getValue().intValue());
        }
    }, "min-height"),
    LEFT(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getXPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.START);
        } else {
            c.getXPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.START);
        }
    }, "left", true),
    RIGHT(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getXPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.END);
        } else {
            c.getXPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.END);
        }
    }, "right", true),
    TOP(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getYPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.START);
        } else {
            c.getYPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.START);
        }
    }, "top", true),
    BOTTOM(CssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getYPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.END);
        } else {
            c.getYPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.END);
        }
    }, "bottom", true),
    HORIZONTAL_POSITION(CssHelper.STRING, (ctx, p, c) -> {
        if (p.getValue().equals("center")) {
            c.getXPos().setRelative(0, RELATIVE_INT, GuiConstants.ENUM_RELATIVE_POS.CENTER);
        }
    }, "horizontal-position"),
    VERTICAL_POSITION(CssHelper.STRING, (ctx, p, c) -> {
        if (p.getValue().equals("center")) {
            c.getYPos().setRelative(0, RELATIVE_INT, GuiConstants.ENUM_RELATIVE_POS.CENTER);
        }
    }, "vertical-position"),
    PROGRESS_FULL(CssHelper.TEXTURE_SPRITE, (ctx, p, c) -> {
        if (c instanceof GuiProgressBar.ProgressBarStyleManager) {
            ((GuiProgressBar.ProgressBarStyleManager) c).setFullTexture(p.getValue());
        }
    }, "progress-bar-full-image"),
    PROGRESS_FULL_COLOR(CssHelper.COLOR, (ctx, p, c) -> {
        if (c instanceof GuiProgressBar.ProgressBarStyleManager) {
            ((GuiProgressBar.ProgressBarStyleManager) c).setFullProgressBarColor(p.getValue());
        }
    }, "progress-bar-full-color"),
    PROGRESS_TEXT_COLOR(CssHelper.COLOR, (ctx, p, c) -> {
        if (c instanceof GuiProgressBar.ProgressBarStyleManager) {
            ((GuiProgressBar.ProgressBarStyleManager) c).setProgressTextColor(p.getValue());
        }
    }, "progress-bar-text-color"),
    DISPLAY(CssHelper.STRING, (ctx, p, c) -> {
        if (p.getType().isNone() || p.getValue().equals("none")) {
            c.setDisplay(GuiConstants.COMPONENT_DISPLAY.NONE);
        } else if (p.getValue().equals("block")) {
            c.setDisplay(GuiConstants.COMPONENT_DISPLAY.BLOCK);
        } else if (p.getValue().equals("inline")) {
            c.setDisplay(GuiConstants.COMPONENT_DISPLAY.INLINE);
        } else {
            throw new IllegalArgumentException("Unsupported display value : " + p.getValue());
        }
    }, "display", false, true);

    /*
    overflow, border style, asymetric borders.....
     */

    public final String key;
    public final DefinitionType<?> parser;
    public final CssStyleApplier<?> applyFunction;
    public final boolean isDefaultAuto;
    public final boolean acceptsNullValue;
    public final boolean inheritable;

    <T> EnumCssStyleProperties(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key) {
        this(parser, applyFunction, key, false);
    }

    <T> EnumCssStyleProperties(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key, boolean isDefaultAuto) {
        this(parser, applyFunction, key, isDefaultAuto, isDefaultAuto, false);
    }

    <T> EnumCssStyleProperties(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key, boolean isDefaultAuto, boolean acceptsNullValue) {
        this(parser, applyFunction, key, isDefaultAuto, acceptsNullValue, false);
    }

    <T> EnumCssStyleProperties(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key, boolean isDefaultAuto, boolean acceptsNullValue, boolean inheritable) {
        this.applyFunction = applyFunction;
        this.key = key;
        this.parser = parser;
        this.isDefaultAuto = isDefaultAuto;
        this.acceptsNullValue = acceptsNullValue;
        this.inheritable = inheritable;
    }

    public static EnumCssStyleProperties fromKey(String property) {
        for (EnumCssStyleProperties prop : values()) {
            if (prop.key.equals(property))
                return prop;
        }
        return null;
    }
}
