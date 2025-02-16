package fr.aym.acsguis.component.textarea;

import java.util.List;
import java.util.function.Function;

public class UpdatableGuiLabel extends GuiLabel {
    private final Function<String, String> formatter;
    private final LabelValueFunction valueFunction;
    private final LabelValue labelValue;
    private String lastText;

    /**
     * @deprecated Will be removed. Use the other constructors
     */
    @Deprecated
    public UpdatableGuiLabel(String pattern, Function<String, String> formatter) {
        super(pattern);
        assert formatter != null;
        this.formatter = formatter;
        this.valueFunction = null;
        this.labelValue = null;
    }

    public UpdatableGuiLabel(String pattern, LabelValueFunction valueFunction) {
        super(pattern);
        this.formatter = null;
        this.valueFunction = valueFunction;
        this.labelValue = new LabelValue();
    }

    @Override
    public String getText() {
        if (formatter != null) {
            return formatter.apply(super.getText());
        }
        valueFunction.get(labelValue);
        return String.format(super.getText(), labelValue.values);
    }

    @Override
    public List<String> getCachedTextLines() {
        String text = getRenderedText();
        if (!text.equals(lastText)) {
            lastText = text;
            cachedTextLines = null;
        }
        return super.getCachedTextLines();
    }

    public static final class LabelValue {
        private Object[] values;

        public void set(Object... values) {
            this.values = values;
        }
    }

    public interface LabelValueFunction {
        void get(LabelValue value);
    }
}
