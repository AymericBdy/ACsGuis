package fr.aym.acsguis.component.textarea;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.list.GuiDropdownListSkeleton;
import net.minecraft.command.CommandBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Text field with auto-completion
 */
public abstract class GuiSearchField extends GuiDropdownListSkeleton<GuiTextField> {
    private final GuiTextField field;
    private List<String> availableNames;
    private boolean multiSearch;

    /**
     * @param maxElementCount if > 0: limits the number of auto-completions
     */
    public GuiSearchField(int maxElementCount) {
        super(new GuiTextField());
        this.field = getComponent();

        field.addKeyboardListener((typedChar, keyCode) -> {
            String txt = field.getText();
            if (isMultiSearch() && txt.contains(",")) {
                String[] temp = txt.split(",");
                txt = temp[temp.length - 1];
            }
            List<String> names = CommandBase.getListOfStringsMatchingLastWord(new String[]{txt}, getAvailableNames());
            names.remove(txt);
            if(maxElementCount > 0) {
                names = names.stream().limit(maxElementCount).collect(Collectors.toList());
            }
            setOptions(names);
            setPanelVisible(!names.isEmpty() && !txt.isEmpty());

            //label.setCssClasses("search_bar_match");
        });
    }

    @Override
    public void setSelectedElement(String selectedElement) {
        super.setSelectedElement(selectedElement);
        if (!isMultiSearch() || !field.getText().contains(","))
            field.setText(selectedElement);
        else {
            field.setText(field.getText().substring(0, field.getText().lastIndexOf(",") + 1) + selectedElement);
        }
    }

    public void setMultiSearch(Pattern pattern, boolean multiSearch) {
        this.multiSearch = multiSearch;
        setRegexPattern(pattern);
    }

    public boolean isMultiSearch() {
        return multiSearch;
    }

    public void setRegexPattern(Pattern pattern) {
        field.setRegexPattern(pattern);
    }

    public void setAvailableNames(@Nullable List<String> avaibleNames) {
        this.availableNames = avaibleNames;
    }

    public List<String> getAvailableNames() {
        if (availableNames == null) {
            setAvailableNames(generateAvailableNames());
        }
        return availableNames;
    }

    public abstract List<String> generateAvailableNames();

    public void setText(String text) {
        field.setText(text);
        field.getKeyboardListeners().get(1).onKeyTyped(' ', -70); //Will update the suggestions of results
    }

    public String getText() {
        return field.getText();
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.SEARCH_FIELD;
    }
}
