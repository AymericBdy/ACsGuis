package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.ScriptTimer;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;

@Block(
        feature = @Feature(name = "Time loop",
                description = "Define a piece of code that will be run periodically every time a specific amount of time is reached.",
                examples = "every 1 minute:\n" +
                        "    ...",
                regex = "every .*")
        )
public class ScriptBlockTimeLoop extends ScriptBlock {

    public long delay;

    public ScriptBlockTimeLoop(ScriptToken head) {
        try{
            this.delay = getDelay(head);
        }catch(Exception e){
            if(ScriptManager.FULL_DEBUG)e.printStackTrace();
        }
    }

    public long getDelay(ScriptToken line) throws Exception {
        line.setText(line.getText().replaceAll("every\\s+", "").replaceAll(":", ""));
        if(line.getText().endsWith("server")){
            line.setText(line.getText().replaceAll("server",""));

        }
        ScriptExpression expr = ScriptDecoder.parse(line,new ScriptCompilationContext());
        //System.out.println("Loading time looping block :");
        //System.out.println(expr.getClass());
        //System.out.println(expr.get(new ScriptCongetText()()).getClass());
        //System.out.println(expr.get(new ScriptCongetText()()).getObject());
        return (((Long)expr.get(new ScriptContext()).getObject()));
    }

    @Override
    public void init(ScriptLineBlock scriptLineBlock) throws Exception {
        setRoot(scriptLineBlock.compile());
        //System.out.println("Putting in loop with delay : "+delay);
        ScriptTimer.loopIScript(this,delay);
    }

}