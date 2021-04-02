package fr.nico.sqript.blocks;

import fr.nico.sqript.compiling.*;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptInstance;
import fr.nico.sqript.types.ScriptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ScriptBlock extends IScript {

    private ScriptLineBlock mainField;
    private IScript root;
    private ScriptLine head;
    private ScriptInstance scriptInstance;

    public ScriptLine getHead() {
        return head;
    }

    public ScriptBlock() {
    }

    /***
     * @param head The very first line of the block (e.g : "on jump:")
     * @throws ScriptException.ScriptSyntaxException When the head of the block doesn't match the required pattern
     */
    public ScriptBlock(ScriptLine head) throws ScriptException.ScriptSyntaxException {
        this.head = head;
        scriptInstance = head.scriptInstance;
    }

    public ScriptLineBlock getMainField() {
        return mainField;
    }

    public void setMainField(ScriptLineBlock mainField) {
        this.mainField = mainField;
    }

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    public ScriptLineBlock getSubBlock(String label) {
        for (ScriptLineBlock f : mainField.getSubBlocks()) {
            if (f.getLabel().equalsIgnoreCase(label))
                return f;
        }
        return null;
    }


    public boolean fieldDefined(String label) {
        return getSubBlock(label) != null;
    }

    /**
     * Should not be overridden, except for very special behaviors.
     *
     * @param baseIndent The tab level of this block
     * @param block The full ScriptLineBlock of the block
     * @throws Exception
     */
    public void init(int baseIndent, ScriptLineBlock block) throws Exception {
        groupFields(baseIndent, block.content);
        load();
    }

    public String shiftIndentation(String str, int lvl) {
        for (int i = 0; i < lvl; i++) {
            if (str.charAt(0) == '\t') {
                str = str.substring(1);
            }
        }
        return str;
    }

    /***
     * Generates the sub-blocks within this block
     *
     * @param baseIndent The indent of this block TODO ADDED BY AYM
     * @param block The complete block to work with
     * @throws ScriptException.ScriptIndentationErrorException When a line is indented while it shouldn't
     * @throws ScriptException.ScriptMissingTokenException When a sub-block is empty
     */
    protected void groupFields(int baseIndent, List<ScriptLine> block) throws ScriptException.ScriptIndentationErrorException, ScriptException.ScriptMissingTokenException {
        Pattern getLabel = Pattern.compile("\\s*([\\w ]*)(?: )?:(.*)");
        String currentLabel = "";
        ScriptLine next;
        setMainField(new ScriptLineBlock(currentLabel, new ArrayList<>()));
        //System.out.println("Loading with block : " + block);
        while (!block.isEmpty()) {
            next = block.remove(0);
            //System.out.println("Next is : "+next);
            //Indentation error
            if (ScriptDecoder.getTabLevel(next.text) > baseIndent+1) {
                throw new ScriptException.ScriptIndentationErrorException(next);
            }

            Matcher m = getLabel.matcher(next.text);
            if (m.find() && Arrays.asList(this.getClass().getAnnotation(Block.class).fields()).contains(currentLabel = m.group(1))) {
                //Found the description of a sub-block

                List<ScriptLine> content = new ArrayList<>();

                if (m.groupCount() < 2 || !m.group(2).isEmpty())
                    content.add(next.with(m.group(2)));

                while (!block.isEmpty() && ScriptDecoder.getTabLevel(block.get(0).text) > baseIndent+1) {
                    next = block.remove(0);
                    next = next.with(shiftIndentation(next.text, 2));
                    content.add(next);
                }

                if (content.size() == 0) {
                    throw new ScriptException.ScriptMissingTokenException(next);
                } else {
                    ScriptLineBlock field = new ScriptLineBlock(currentLabel, content);
                    currentLabel = "";
                    getMainField().addSubBlock(field);
                }
            }else {
                //Found a block which is not in a field, it's the mainField
                List<ScriptLine> content = new ArrayList<>();
                content.add(next.with(shiftIndentation(next.text, 1)));
                //System.out.println("Added : "+next);
                while (!block.isEmpty()) {
                    next = block.remove(0);
                    next = next.with(shiftIndentation(next.text, 1));
                    content.add(next);
                    //System.out.println("Added : "+next);
                }
                getMainField().setContent(content);
                //System.out.println("r:"+content);
                return;
            }

        }
    }


    protected void load() throws Exception {
    }

    public IScript getRoot() {
        return root;
    }

    public void setRoot(IScript root) {
        this.root = root;
    }

    @Override
    public IScript run(ScriptContext context) {
        return getRoot();
    }

    @Override
    public void execute(ScriptContext context) {
    }

    @Override
    public IScript getNext(ScriptContext context) {
        return null;
    }

    @Override
    public IScript getParent() {
        return null;
    }

    public static class ScriptLineBlock {

        private final List<ScriptLineBlock> subBlocks = new ArrayList<>();
        private List<ScriptLine> content;
        private String label;

        public ScriptLineBlock(String label, List<ScriptLine> block) {
            setLabel(label);
            setContent(block);
        }

        public List<ScriptLineBlock> getSubBlocks() {
            return subBlocks;
        }

        public void addSubBlock(ScriptLineBlock subBlock) {
            this.subBlocks.add(subBlock);
        }

        public List<ScriptLine> getContent() {
            return content;
        }

        public void setContent(List<ScriptLine> content) {
            this.content = content;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public ScriptType evaluate(ScriptCompileGroup group, ScriptContext context) throws Exception {
            return ScriptDecoder.getExpression(content.get(0), group).get(context);
        }

        public String getRawContent() {
            return content.get(0).text.trim();
        }

        public ScriptType evaluate() throws Exception {
            return evaluate(new ScriptCompileGroup(), new ScriptContext());
        }

        public IScript compile(ScriptBlock parent) throws Exception {
            return compile(parent, new ScriptCompileGroup());
        }

        public IScript compile(ScriptCompileGroup compileGroup) throws Exception {
            return compile(null, compileGroup);
        }

        public IScript compile(ScriptBlock parent, ScriptCompileGroup compileGroup) throws Exception {
            //System.out.println(content);
            return ScriptDecoder.group(parent, content, compileGroup);
        }
    }
}
