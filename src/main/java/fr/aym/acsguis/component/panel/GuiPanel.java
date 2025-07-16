package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.FlowLayout;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.CssPanelStyle;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.utils.ComponentRenderContext;

import java.util.*;

public class GuiPanel extends GuiComponent implements AutoStyleHandler<InternalComponentStyle> {
    protected List<GuiComponent> childComponents = new ArrayList<>();

    protected List<GuiComponent> queuedComponents = new ArrayList<>();
    protected List<GuiComponent> toRemoveComponents = new ArrayList<>();

    protected PanelLayout<?> layout;

    public GuiPanel withFlowLayout() {
        this.setLayout(new FlowLayout());
        return this;
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.PANEL;
    }

    @Override
    protected InternalComponentStyle createStyleManager() {
        CssPanelStyle s = new CssPanelStyle(this);
        s.getCustomizer().withAutoStyles(this, EnumCssStyleProperty.HEIGHT);
        return s;
    }

    /**
     * Changes the layout of this panel <br>
     * If the panel has elements, it automatically recomputes their position and size
     *
     * @see PanelLayout
     */
    public GuiPanel setLayout(PanelLayout<?> layout) {
        boolean dif = this.layout != layout;
        if (this.layout != null) {
            this.layout.clear();
        }
        if (!dif) {
            return this;
        }
        for (GuiComponent c : queuedComponents) {
            if (layout != null)
                c.getStyleCustomizer().withAutoStyles(layout, layout.getModifiedProperties());
            if (this.layout != null)
                c.getStyleCustomizer().removeAutoStyles(this.layout, this.layout.getModifiedProperties());
        }
        for (GuiComponent c : childComponents) {
            if (!toRemoveComponents.contains(c)) {
                if (layout != null)
                    c.getStyleCustomizer().withAutoStyles(layout, layout.getModifiedProperties());
                if (this.layout != null)
                    c.getStyleCustomizer().removeAutoStyles(this.layout, this.layout.getModifiedProperties());
            }
        }
        this.layout = layout;
        if (layout != null) {
            layout.setContainer(this);
        }
        return this;
    }

    public PanelLayout<?> getLayout() {
        return layout;
    }

    @Override
    public boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, InternalComponentStyle target) {
        if (property == EnumCssStyleProperty.HEIGHT) {
            float height = 0;
            for (GuiComponent c : queuedComponents) {
                height = Math.max(height, c.getY() + c.getStyle().getOffsetY() + c.getHeight());
            }
            for (GuiComponent c : childComponents) {
                if (!toRemoveComponents.contains(c))
                    height = Math.max(height, c.getY() + c.getStyle().getOffsetY() + c.getHeight());
            }
            target.getHeight().setAbsolute(height);
            return true;
        }
        return false;
    }

    /**
     * Add a child component to this GuiPanel.
     * The child component will be updated, rendered, etc,
     * with its parent.
     *
     * @param component The child component
     */
    public GuiPanel add(GuiComponent component) {
        component.setParent(this);
        if (layout != null) {
            component.getStyleCustomizer().withAutoStyles(layout, layout.getModifiedProperties());
        }
        queuedComponents.add(component);
        return this;
    }

    public GuiPanel remove(GuiComponent component) {
        if (layout != null) {
            component.getStyleCustomizer().removeAutoStyles(layout, layout.getModifiedProperties());
        }
        toRemoveComponents.add(component);
        return this;
    }

    public void removeAllChildren() {
        if (layout != null) {
            layout.clear();
        }
        queuedComponents.clear();
        toRemoveComponents.addAll(childComponents);
    }

    public List<GuiComponent> getQueuedComponents() {
        return queuedComponents;
    }

    public List<GuiComponent> getToRemoveComponents() {
        return toRemoveComponents;
    }

    public boolean flushComponentsQueue() {
        if (queuedComponents.isEmpty()) {
            return false;
        }
        Iterator<GuiComponent> queuedComponentsIterator = queuedComponents.iterator();
        while (queuedComponentsIterator.hasNext()) {
            GuiComponent component = queuedComponentsIterator.next();
            component.getStyle().resetCssStack();
            GuiFrame frame = getGui().getFrame();
            if (getGui() != null) {
                component.resize(getGui(), frame.getResolution().getScaledWidth(), frame.getResolution().getScaledHeight());
            }
            getChildComponents().add(component);
            //the resize already refresh the style component.getStyle().refreshCss(false);
            queuedComponentsIterator.remove();
            if (component instanceof GuiPanel) {
                ((GuiPanel) component).flushComponentsQueue();
            }
        }
        Collections.sort((List) getChildComponents());
        return true;
    }

    @Override
    public void resize(GuiFrame.APIGuiScreen gui, int screenWidth, int screenHeight) {
        if (getLayout() != null) {
            getLayout().clear();
        }
        super.resize(gui, screenWidth, screenHeight);
        this.getChildComponents().forEach(component -> component.resize(gui, screenWidth, screenHeight));
    }

    public boolean flushRemovedComponents() {
        if (toRemoveComponents.isEmpty()) {
            return false;
        }
        Iterator<GuiComponent> toRemoveComponentsIterator = toRemoveComponents.iterator();
        while (toRemoveComponentsIterator.hasNext()) {
            GuiComponent component = toRemoveComponentsIterator.next();
            getChildComponents().remove(component);
            toRemoveComponentsIterator.remove();
            if (component instanceof GuiPanel) {
                ((GuiPanel) component).flushRemovedComponents();
            }
        }
        if (getLayout() != null) {
            getLayout().clear();
        }
        return true;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {
        for (GuiComponent component : getChildComponents()) {
            component.render(mouseX, mouseY, partialTicks, enableScissor);
        }
        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
    }

    @Override
    public boolean tick() {
        if (!super.tick()) {
            return false;
        }
        this.flushRemovedComponents();
        this.flushComponentsQueue();
        this.getChildComponents().forEach(GuiComponent::tick);
        return true;
    }

    public List<GuiComponent> getChildComponents() {
        return childComponents;
    }

    public List<GuiComponent> getOrderedChildComponents() {
        if (getChildComponents() == null) {
            return Collections.emptyList();
        }
        List<GuiComponent> components = new ArrayList<>(getChildComponents());
        // Sort by z index
        Collections.sort(components);
        // Reverse order so components added after other ones are on top of the list (even if at the same z index)
        Collections.reverse(components);
        return components;
    }
}
