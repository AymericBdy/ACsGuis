package fr.aym.acsguis.cssengine.v2;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GuiOrchestrator {
    private final GuiFrame.APIGuiScreen screen;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private final Queue<CssRefreshEntry> componentReloadQueue = new ArrayDeque<>();
    private final Queue<CssRefreshEntry> workQueue = new ArrayDeque<>();

    public GuiOrchestrator(GuiFrame.APIGuiScreen screen) {
        this.screen = screen;
    }

    public void processQueue() {
        if (componentReloadQueue.isEmpty()) {
            return;
        }
        System.out.println("Processing " + componentReloadQueue.size() + " components");
       // System.out.println("First " + componentReloadQueue.element().style.getOwner());

        CssRefreshEntry entry;
        isProcessing.set(true);
        while ((entry = componentReloadQueue.poll()) != null) {
            if (!entry.valid) {
                continue;
            }
            entry.style.refreshStyleInternal(screen, entry.properties);
            workQueue.add(entry);
        }
        isProcessing.set(false);
        int sx = screen != null ? (int) (screen.getFrame().getResolution().getScaledWidth() / screen.getScaleX()) : 1;
        int sy = screen != null ? (int) (screen.getFrame().getResolution().getScaledHeight() / screen.getScaleY()) : 1;
       // System.out.println("SE2 " + sx + " " + sy);
        while ((entry = workQueue.poll()) != null) {
           // System.out.println("SIZE " + entry.style.getOwner());
            // Here, if some layouts change, the other elements in that layout will be added to the componentReloadQueue, and their position will later be updated
            entry.style.updateComponentSize(sx, sy);
            componentReloadQueue.add(entry);
        }
       // System.out.println("SE3");
        while ((entry = componentReloadQueue.poll()) != null) {
            if (!entry.valid) {
                continue;
            }
           // System.out.println("POS " + entry.style.getOwner());
            entry.style.updateComponentPosition(sx, sy);
        }
    }

    public void scheduleRefresh(InternalComponentStyle style, EnumCssStyleProperty... properties) {
        if (isProcessing.get()) {
            ACsGuiApi.log.warn("Trying to schedule refresh of {} while already processing queue!", style.getOwner());
            return;
        }
        CssRefreshEntry entry = new CssRefreshEntry(style, properties);
        /*if (componentReloadQueue.contains(entry)) {
            //System.out.println("Preventing DUP¨reload " + style.getOwner());
            return;
        }*/
        //TODO OPTIMIZE, goal is to cancel previous refresh in order of the queue, and only keep the last with all properties to refresh
        List<CssRefreshEntry> others = componentReloadQueue.stream().filter(other -> other.style.equals(style)).collect(Collectors.toList());
        if (!others.isEmpty()) {
           // System.out.println("FOUND OTHERS: " + others.size());
            List<EnumCssStyleProperty> propertyList = new ArrayList<>(Arrays.asList(properties));
            for (CssRefreshEntry other : others) {
                other.valid = false;
                if (Arrays.equals(other.properties, properties)) {
                    continue;
                }
              //  System.out.println("Complementary :O");
                propertyList.addAll(Arrays.asList(other.properties));
            }
            entry.properties = propertyList.toArray(new EnumCssStyleProperty[0]);
        }
       // System.out.println("REFRESH " + style.getOwner());
        componentReloadQueue.add(entry);
    }

    public boolean hasUpdates() {
        return !componentReloadQueue.isEmpty();
    }

    private static class CssRefreshEntry {
        private final InternalComponentStyle style;
        private EnumCssStyleProperty[] properties;
        private boolean valid;

        private CssRefreshEntry(InternalComponentStyle style, EnumCssStyleProperty[] properties) {
            this.style = style;
            this.properties = properties;
            this.valid = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CssRefreshEntry)) return false;
            CssRefreshEntry that = (CssRefreshEntry) o;
            return Objects.equals(style, that.style) && Objects.deepEquals(properties, that.properties);
        }

        @Override
        public int hashCode() {
            return Objects.hash(style, Arrays.hashCode(properties));
        }
    }
}
