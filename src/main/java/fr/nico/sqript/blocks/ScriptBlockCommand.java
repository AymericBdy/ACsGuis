package fr.nico.sqript.blocks;

import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import fr.nico.sqript.compiling.*;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Block(
        feature = @Feature(name = "Command",
                description = "Define a new command that can be executed and have some action.",
                examples = "command /randomplayer:\n" +
                        "    usage: /randomplayer\n" +
                        "    description: returns a random player\n" +
                        "    send a random element of all players to sender",
                regex = "^command /.*"),
        fields = {
                @Feature(name = "side"),
                @Feature(name = "description"),
                @Feature(name = "usage"),
                @Feature(name = "aliases")
        }
)
public class ScriptBlockCommand extends ScriptBlock implements ICommand {

    ScriptParameterDefinition[][] argumentsDefinitions;
    private final String name;


    public ScriptBlockCommand(ScriptToken head) {
        Feature[] features = new Feature[]{new Feature(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String[] examples() {
                return new String[0];
            }

            @Override
            public String pattern() {
                return null;
            }

            @Override
            public String regex() {
                return null;
            }

            @Override
            public String type() {
                return null;
            }

            @Override
            public boolean settable() {
                return false;
            }

            @Override
            public fr.nico.sqript.structures.Side side() {
                return null;
            }

            @Override
            public int priority() {
                return 0;
            }
        }};

        //System.out.println("Loading block command:"+head);
        final String def = ScriptDecoder.splitAtDoubleDot(head.getText().replaceFirst("command\\s+/", ""))[0];
        Matcher m = Pattern.compile("<(.*?)>").matcher(def);
        final List<ScriptParameterDefinition[]> parameterDefinitions = new ArrayList<>();
        while (m.find()) {
            try {
                //System.out.println("Adding argument : "+m.group(1));
                Collections.addAll(parameterDefinitions, Arrays.stream(m.group(1).split("\\|")).map(ScriptDecoder::parseType).map(ScriptParameterDefinition::new).toArray(ScriptParameterDefinition[]::new));
                //System.out.println("Now : "+parameterDefinitions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.name = def.split("<(.*?)>")[0].trim();
        this.argumentsDefinitions = parameterDefinitions.toArray(new ScriptParameterDefinition[0][0]);
    }

    @Override
    protected void load() throws Exception {

        if (fieldDefined("side"))
            this.setSide(fr.nico.sqript.structures.Side.from(getSubBlock("side").getRawContent()));

        ScriptCompilationContext compileGroup = new ScriptCompilationContext();
        //Adding the "arg" expression to the compile group to prevent false-positive errors
        for (int j = 0; j < argumentsDefinitions.length; j++) {
            compileGroup.add("arg[ument] " + (j + 1), ScriptElement.class);
        }
        compileGroup.add("(sender|player|console|server)", ScriptElement.class);


        this.setRoot(getMainField().compile(compileGroup));

        if (fieldDefined("description"))
            this.setDescription(getSubBlock("description").getRawContent());

        if (fieldDefined("usage"))
            this.setUsage(getSubBlock("usage").getRawContent());

        if (fieldDefined("aliases"))
            this.setAliases(getSubBlock("aliases").getContent().stream().map(ScriptToken::getText).toArray(String[]::new));

        if (side == fr.nico.sqript.structures.Side.BOTH || (side == fr.nico.sqript.structures.Side.CLIENT)) {
            SqriptForge.addClientCommand(this);
        }

        if (side == fr.nico.sqript.structures.Side.BOTH || (side == fr.nico.sqript.structures.Side.SERVER)){
            SqriptForge.addServerCommand(this);
        }

        getScriptInstance().registerBlock(this);
    }


    private fr.nico.sqript.structures.Side side = fr.nico.sqript.structures.Side.SERVER;

    private String[] aliases = new String[0];
    private String description;
    private String usage;

    public fr.nico.sqript.structures.Side getSide() {
        return side;
    }

    public void setSide(fr.nico.sqript.structures.Side side) {
        this.side = side;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return usage;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(aliases);
    }

    @SideOnly(Side.CLIENT)
    public void executeOnServer(String[] strings){
        String args = "";
        for(String s: strings)args+=s+" ";
        Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacket(new CPacketChatMessage("/"+getName()+" "+args));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        //System.out.println("executing");

        if((side != fr.nico.sqript.structures.Side.CLIENT && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)){
            executeOnServer(strings);
            if(side == fr.nico.sqript.structures.Side.SERVER)
                return;
        }

        ScriptContext c = new ScriptContext(ScriptManager.GLOBAL_CONTEXT);

        //Adding arguments to the context
        //Arguments can only be numbers, strings or player.
        for (int i = 0; i < argumentsDefinitions.length && i < strings.length; i++) {
            //Todo : Dynamic command parameters type check
            Class p = argumentsDefinitions[i][0].getTypeClass();
            if(i== argumentsDefinitions.length-1 && p== TypeString.class){
                String r = "";
                for(int j = i;j<strings.length;j++){
                    r+=strings[j]+" ";
                }
                r=r.substring(0,r.length()-1);
                c.put(new ScriptTypeAccessor(new TypeString(r),"arg[ument] "+(i+1)));
            }else{
                if(p == TypeString.class)
                {
                    c.put(new ScriptTypeAccessor(new TypeString(strings[i]),"arg[ument] "+(i+1)));
                }
                else if(p == TypeNumber.class)
                {
                    c.put(new ScriptTypeAccessor(new TypeNumber(strings[i]),"arg[ument] "+(i+1)));
                }
                else if(p == TypePlayer.class)
                {
                    c.put(new ScriptTypeAccessor(new TypePlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(strings[i])),"arg[ument] "+(i+1)));
                }
            }
        }
        //System.out.println("ICOMMANDSENDER NULL : "+(iCommandSender==null));
        if(iCommandSender instanceof EntityPlayer){
            c.put(new ScriptTypeAccessor(new TypePlayer((EntityPlayer) iCommandSender), "(sender|player)","(sender|player|console|server)".hashCode()));
        }else if(iCommandSender instanceof MinecraftServer){
            c.put(new ScriptTypeAccessor(new TypeConsole((MinecraftServer) iCommandSender), "(sender|console|server)","(sender|player|console|server)".hashCode()));
        }

        //Running the associated script
        ScriptClock k = new ScriptClock(c);
        try {
            //System.out.println("Running the command");
            k.start(this);
        } catch (Exception e) {
            iCommandSender.sendMessage(new TextComponentString("\247cAn error occured while executing Sqript command : "));
            if(e instanceof ScriptException.ScriptWrappedException){
                Exception wrapped = ((ScriptException.ScriptWrappedException)(e)).getWrapped();
                iCommandSender.sendMessage(new TextComponentString(((ScriptException.ScriptWrappedException)(e)).getLine()+" : "+wrapped).setStyle(new Style().setColor(TextFormatting.RED)));
                wrapped.printStackTrace();
            }else{
                iCommandSender.sendMessage(new TextComponentString("\247c"+e.getMessage()).setStyle(new Style().setColor(TextFormatting.RED)));
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer minecraftServer, ICommandSender iCommandSender) {
        //Going to add a permission system
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings, @Nullable BlockPos blockPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    public ScriptParameterDefinition[][] getArgumentsDefinitions() {
        return argumentsDefinitions;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}