package fr.nico.sqript.forge.common;

import com.google.common.collect.Lists;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.network.ScriptReloadMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class SqriptCommand extends CommandBase {

    @Override
    public String getName() {
        return "sqript";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Run /sqript help to get help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length>0){
            if(args[0].equalsIgnoreCase("reload")){
                if(args.length == 2 && args[1].equalsIgnoreCase("all")){
                    SqriptForge.channel.sendToAll(new ScriptReloadMessage());
                }
                long t = System.currentTimeMillis();
                SqriptUtils.sendMessage("Reloading all scripts",sender);
                try{
                    ScriptManager.reload();
                }catch(Throwable e){
                    if (e instanceof ScriptException.ScriptExceptionList) {
                        SqriptUtils.sendError("\247cError while reloading the scripts : ",sender);
                        for(Throwable ex : ((ScriptException.ScriptExceptionList) e).exceptionList){
                            SqriptUtils.sendError("\247c"+ex.getLocalizedMessage()+" (\2478"+ex.getStackTrace()[0]+"\247c)",sender);
                            ex.printStackTrace();
                        }
                    }
                    else{
                        SqriptUtils.sendError("\247cError while reloading scripts, see stacktrace for more information.",sender);
                        SqriptUtils.sendError("\247c"+e.getLocalizedMessage(),sender);
                        e.printStackTrace();
                    }

                }
                SqriptUtils.sendMessage("Done in "+((System.currentTimeMillis()-t)/1000d)+" seconds",sender);
            }
            else if(args[0].equalsIgnoreCase("generateDoc")){
                try {
                    SqriptUtils.generateDoc();
                    SqriptUtils.sendMessage("Generated documentation at scripts/doc.md",sender);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                sendHelp(sender);
            }
        }else{
            sendHelp(sender);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length==1){
            return getListOfStringsMatchingLastWord(args,Lists.newArrayList("reload","help","generateDoc"));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }



    private void sendHelp(ICommandSender sender) {
        SqriptUtils.sendMessage("Here are the Sqript commands : ",sender);
        SqriptUtils.sendMessage("/sqript reload \2477Reloads all the scripts on the logical server side.",sender);
        SqriptUtils.sendMessage("/sqript reload all \2477Reloads all the scripts on the server and on all connected clients.",sender);
        SqriptUtils.sendMessage("/sqript generateDoc \2477Generate a .md file to list all actions, expressions, blocks, and events.",sender);
        SqriptUtils.sendMessage("/sqript help \2477Displays this.",sender);
    }
}
