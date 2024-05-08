package fr.aym.acsguis.utils;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextArea;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Automatically shown when failing to show a gui
 */
public class GuiCssError extends GuiFrame {
    /**
     * Displays the given error
     *
     * @param guiName Name of the faulty gui
     * @param e       The error
     */
    public GuiCssError(String guiName, Throwable e) {
        super(new GuiScaler.Identity());
        style.setBackgroundColor(Color.TRANSLUCENT);
        style.getXPos().setAbsolute(10);
        style.getYPos().setAbsolute(10);
        style.getWidth().setAbsolute(2000);
        style.getHeight().setAbsolute(900);

        ScaledResolution r = new ScaledResolution(mc);
        GuiLabel error = new GuiLabel("");
        error.getStyle().getWidth().setAbsolute(r.getScaledWidth() - 20);
        error.getStyle().getHeight().setAbsolute(r.getScaledHeight() - 20);
        error.setMaxTextLength(Integer.MAX_VALUE);
        error.addResizeListener((w, h) -> {
            error.getStyle().getWidth().setAbsolute(w - 20);
            error.getStyle().getHeight().setAbsolute(h - 20);
        });
        error.setText("Cannot display gui " + guiName + " " + "\n " + "\n" + "CSS error " + e.toString());
        while (e.getCause() != null) {
            e = e.getCause();
            error.setText(error.getText() + " " + "\n " + "\n" + " \t Caused by : " + e.toString());
        }
        add(error.getStyle().setForegroundColor(0xFFAAAA).setBackgroundColor(Integer.MIN_VALUE).getOwner());

        getStyle().getWidth().setAbsolute(r.getScaledWidth() - 20);
        getStyle().getHeight().setAbsolute(r.getScaledHeight() - 20);
        addResizeListener((w, h) -> {
            getStyle().getWidth().setAbsolute(w - 20);
            getStyle().getHeight().setAbsolute(h - 20);
        });
    }

    private GuiPanel summary;
    private GuiComponent<?> displayed;

    /**
     * Displays all parsed data
     */
    public GuiCssError() {
        super(new GuiScaler.Identity());
        style.setBackgroundColor(Color.TRANSLUCENT);
        style.getXPos().setAbsolute(10);
        style.getYPos().setAbsolute(10);
        style.getWidth().setAbsolute(2000);
        style.getHeight().setAbsolute(900);

        ScaledResolution r = new ScaledResolution(mc);

        int width = r.getScaledWidth() - 20;
        summary = new GuiScrollPane();
        summary.getStyle().getWidth().setAbsolute(width);
        summary.getStyle().getHeight().setAbsolute(r.getScaledHeight() - 20);

        GuiLabel lab;
        summary.add((lab = new GuiLabel("Click on any css sheet to view it, then press escape to go back")).getStyle().setPaddingLeft(2).setPaddingTop(2).getOwner());
        lab.getStyle().getWidth().setAbsolute(width);
        lab.getStyle().getHeight().setAbsolute(20);
        int i = 1;
        for (Map.Entry<ResourceLocation, Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>>> entry : ACsGuisCssParser.getCssStyleSheets().entrySet()) {
            ResourceLocation res = entry.getKey();
            Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> m = entry.getValue();
            summary.add((lab = new GuiLabel("+ Style sheet : " + res)).getStyle().setPaddingLeft(2).setPaddingTop(2).getOwner().addClickListener((x, y, b) -> {
                remove(summary);

                GuiTextArea error = new GuiTextArea();
                error.setMaxTextLength(Integer.MAX_VALUE);
                error.getStyle().setPaddingTop(4).setPaddingLeft(4);
                error.getStyle().getWidth().setAbsolute(r.getScaledWidth() - 20);
                error.getStyle().getHeight().setAbsolute(r.getScaledHeight() - 20);
                error.addResizeListener((w, h) -> {
                    error.getStyle().getWidth().setAbsolute(w - 20);
                    error.getStyle().getHeight().setAbsolute(h - 20);
                });

                final StringBuilder text = new StringBuilder("Loaded CSS data :" + "\n" + " \n");

                text.append("Style sheet : " + res.toString() + "\n");
                m.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
                    text.append("====> Selector : " + TextFormatting.GOLD).append(e.getKey());
                    text.append(TextFormatting.RESET + " has ");
                    e.getValue().forEach((st, prop) -> {
                        text.append("\n").append("\t -->" + TextFormatting.DARK_AQUA).append(st.key).append(TextFormatting.RESET + " = " + TextFormatting.AQUA);
                        if (prop.getType().isNormal())
                            text.append(prop.getValue());
                        else
                            text.append(prop.getType().toString().toLowerCase());
                    });
                    text.append(TextFormatting.RESET + "\n");
                });
                text.append(" \n \n");

                displayed = error;
                error.setFocused(true);
                error.setText(text.toString());
                add(error.getStyle().setForegroundColor(0x88FF88).setBackgroundColor(Integer.MIN_VALUE).getOwner());
            }));
            //TODO THIS IS BAD
            lab.getStyle().getYPos().setAbsolute(i * 22);
            lab.getStyle().getWidth().setAbsolute(width);
            lab.getStyle().getHeight().setAbsolute(20);
            i++;
        }

        summary.setFocused(true);
        add(summary.getStyle().setForegroundColor(0x88FF88).setBackgroundColor(Integer.MIN_VALUE).getOwner());
        getStyle().getWidth().setAbsolute(r.getScaledWidth() - 20);
        getStyle().getHeight().setAbsolute(r.getScaledHeight() - 20);
        addResizeListener((w, h) -> {
            getStyle().getWidth().setAbsolute(w - 20);
            getStyle().getHeight().setAbsolute(h - 20);
        });
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 && displayed != null) {
            remove(displayed);
            add(summary);
            summary.setFocused(true);
            displayed = null;
        } else
            super.onKeyTyped(typedChar, keyCode);
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean usesDefaultStyle() {
        return false;
    }
}
