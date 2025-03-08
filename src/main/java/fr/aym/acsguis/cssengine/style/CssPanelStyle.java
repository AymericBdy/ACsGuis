package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.style.PanelStyle;

public class CssPanelStyle extends CssComponentStyle implements PanelStyle {
    protected final GuiPanel panel;

    public CssPanelStyle(GuiPanel component) {
        super(component);
        panel = component;
    }

    @Override
    public PanelLayout<?> getLayout() {
        return panel.getLayout();
    }

    @Override
    public PanelStyle setLayout(PanelLayout<?> panelLayout) {
        panel.setLayout(panelLayout);
        return this;
    }

    @Override
    public void resetCssStack() {
        super.resetCssStack();
        for(GuiComponent c : panel.getChildComponents()) {
            c.getStyle().resetCssStack();
        }
    }

    @Override
    public void refreshStyle(GuiFrame.APIGuiScreen gui, EnumCssStyleProperty... properties) {
        super.refreshStyle(gui, properties);
        // Schedule children refresh AFTER
        for (GuiComponent c : panel.getChildComponents()) {
            if (!panel.getToRemoveComponents().contains(c)) {
                c.getStyle().refreshStyle(getOwner().getGui(), properties);
            }
        }
    }

    @Override
    public void notifyOfChildSizeChange(InternalComponentStyle child) {
        if (panel.getLayout() != null) {
            ((PanelLayout<InternalComponentStyle>) panel.getLayout()).onChildSizeChange(child);
        }
        // this will update auto style for auto sized panels
        refreshStyle(getOwner().getGui(), EnumCssStyleProperty.WIDTH, EnumCssStyleProperty.HEIGHT);
    }

    public static class CssScrollPanelStyle extends CssPanelStyle {
        public CssScrollPanelStyle(GuiScrollPane panel) {
            super(panel);
        }

        @Override
        public boolean updateComponentSize(int screenWidth, int screenHeight) {
            boolean change = super.updateComponentSize(screenWidth, screenHeight);
            if (change) {
                ((GuiScrollPane) panel).setSlidersNeedsUpdate();
            }
            return change;
        }

        @Override
        public void notifyOfChildSizeChange(InternalComponentStyle child) {
            super.notifyOfChildSizeChange(child);
            ((GuiScrollPane) panel).setSlidersNeedsUpdate();
        }
    }
}
