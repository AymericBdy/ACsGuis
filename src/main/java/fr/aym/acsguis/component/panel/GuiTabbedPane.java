package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.sqript.SqriptCompatiblity;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class GuiTabbedPane extends GuiPanel {
    protected List<GuiTabbedPaneButton> tabsButtons = new ArrayList<>();
    protected List<GuiPanel> tabsContainers = new ArrayList<>();

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.TABBED_PANE;
    }

    @Override
    public GuiPanel add(GuiComponent component) {
        if (ACsGuiApi.getSqriptSupport().isSqriptLoaded() && component instanceof GuiPanel) {
            addTab(SqriptCompatiblity.nextPannedTabName, (GuiPanel) component);
        } else {
            super.add(component);
        }
        return this;
    }

    public void addTab(String tabName, GuiPanel tabContainer) {
        GuiTabbedPaneButton tabButton = new GuiTabbedPaneButton(tabsContainers.size());
        tabButton.setText(tabName).getStyleCustomizer().withAutoStyles(this, EnumCssStyleProperty.WIDTH, EnumCssStyleProperty.HEIGHT, EnumCssStyleProperty.LEFT, EnumCssStyleProperty.COLOR);
        add(tabButton);
        tabsButtons.add(tabButton);

        tabContainer.setParent(this);
        tabsContainers.add(tabContainer);
        tabContainer.getStyle().setOffsetY(20);
        super.add(tabContainer);

        selectTab(tabsContainers.size() - 1);
    }

    public GuiPanel getTabContainer(int tabIndex) {
        if (tabsContainers.size() < tabIndex) {
            return tabsContainers.get(tabIndex);
        } else {
            return null;
        }
    }

    public GuiTabbedPaneButton getTabButton(int tabIndex) {
        if (tabIndex < tabsButtons.size()) {
            return tabsButtons.get(tabIndex);
        } else {
            return null;
        }
    }

    private int selectedTab;

    public void selectTab(int tabIndex) {
        tabIndex = MathHelper.clamp(tabIndex, 0, tabsContainers.size());
        selectedTab = tabIndex;
        for (int i = 0; i < tabsContainers.size(); i++) {
            if (i == tabIndex) {
                ((InternalComponentStyle) tabsContainers.get(i).getStyle()).setVisible(true);
            } else {
                ((InternalComponentStyle) tabsContainers.get(i).getStyle()).setVisible(false);
            }
        }
        /*for (GuiComponent child : getChildComponents()) {
            child.getStyle().refreshStyle(getGui());
        }*/
    }

    public int getSelectedTabIndex() {
        return selectedTab;
    }

    public GuiPanel getSelectedTab() {
        return tabsContainers.get(selectedTab);
    }

    public class GuiTabbedPaneButton extends GuiButton {
        protected final int index;

        protected GuiTabbedPaneButton(int index) {
            this.index = index;
        }

        @Override
        public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
            super.onMouseClicked(mouseX, mouseY, mouseButton);
            //GuiTabbedPane.this.getStyle().refreshStyle(false);
            selectTab(index);
        }

        @Override
        public EnumSelectorContext getState() {
            if (GuiTabbedPane.this.selectedTab == index) {
                return EnumSelectorContext.ACTIVE;
            } else {
                return super.getState();
            }
        }
    }

    @Override
    public boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, InternalComponentStyle target) {
        if (target.getOwner() instanceof GuiTabbedPaneButton) {
            switch (property) {
                case WIDTH: {
                    target.getWidth().setSizeFunction(((style, size) -> {
                        int c = tabsButtons.size();
                        float w = (getWidth() - 10) / c;
                        size.setAbsolute(w);
                        System.out.println("Width " + getWidth());
                    }));
                    return true;
                }
                case LEFT: {
                    target.getXPos().setPositionFunction(((style, position) -> {
                        int c = tabsButtons.size();
                        float w = (getWidth() - 10) / c;
                        position.setAbsolute(5 + ((GuiTabbedPaneButton) target.getOwner()).index * w);
                    }));
                    return true;
                }
                case COLOR:
                    if (((GuiTabbedPaneButton) target.getOwner()).index == selectedTab) {
                        target.setForegroundColor(tabsContainers.get(((GuiTabbedPaneButton) target.getOwner()).index).getStyle().getForegroundColor());
                    } else
                        target.setForegroundColor(14737632);
                    return true;
                default:
                    return false;
            }
        } else {
            return super.handleProperty(property, context, target);
        }
    }

	/*@Override
	public Priority getPriority(InternalComponentStyle forT) {
		return forT.getOwner() instanceof GuiTabbedPaneButton ? Priority.PARENT : super.getPriority(forT);
	}*/
}
