package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.ScriptInstance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Block(name = "import",
        description = "Import blocks",
        examples = "import:",
        regex = "^imports:\\s*")
public class ScriptBlockImport extends ScriptBlock {

    public ScriptBlockImport(ScriptLine head) throws ScriptException.ScriptSyntaxException {
        super(head);
    }

    @Override
    public void init(int tabLevel, ScriptLineBlock scriptLineBlock) throws Exception {
        Pattern p = Pattern.compile("\\s+(.*) from (.*)");
        for(ScriptLine s : scriptLineBlock.getContent()){
            Matcher m = p.matcher(s.text);
            if(m.find()){
                String function = m.group(1);
                String script = m.group(2);
                ScriptInstance from = ScriptManager.getScriptFromName(script);
                if(from!=null){
                    ScriptFunctionalBlock imported;
                    if(function.equals("*")){
                        getScriptInstance().getBlocksOfClass(ScriptBlockFunction.class).addAll(from.getBlocksOfClass(ScriptBlockFunction.class));
                    }else if((imported=from.getFunction(function))!=null){
                        getScriptInstance().getBlocksOfClass(ScriptBlockFunction.class).add(imported);
                    }else{
                        throw new ScriptException.ScriptUnknownFunctionException(s.with(function));

                    }
                }else{
                    throw new ScriptException.ScriptUnknownInstanceException(s.with(script));
                }
            }
        }
    }
}
