package fr.nico.sqript;

import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ISerialisable;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;
import javafx.scene.Parent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import sun.reflect.ReflectionFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ScriptDataManager {

    public static void load() throws Exception {
        File f = new File(ScriptManager.scriptDir,"data.dat");
        if(!f.exists())return;
        NBTTagCompound nbt = CompressedStreamTools.read(f);
        assert nbt != null : "Wasn't able to read the data.dat file";
        for(String s : nbt.getKeySet()){
            NBTTagCompound n = nbt.getCompoundTag(s);
            String typeName = n.getString("type");
            NBTTagCompound value = n.getCompoundTag("value");
            ScriptType t = instanciateWithData(typeName,value);
            ScriptManager.GLOBAL_CONTEXT.put(new ScriptAccessor(t,s)); //We add the variable to the context
        }
    }

    public static void save() throws Exception {
        NBTTagCompound total = new NBTTagCompound();
        for(ScriptAccessor s : ScriptManager.GLOBAL_CONTEXT.getAccessors()){
            if(s.element instanceof ISerialisable){
                ISerialisable savable = (ISerialisable) s.element;
                String key = s.pattern.pattern();
                NBTTagCompound value = savable.write(new NBTTagCompound());
                String typeName = ScriptDecoder.getNameForType(s.element.getClass());
                NBTTagCompound toAdd = new NBTTagCompound();
                toAdd.setTag("value",value);
                toAdd.setString("type",typeName);
                total.setTag(key,toAdd);
                //System.out.println("Saved variable "+key+" as a "+typeName+" with value "+value);
            }else{
                throw new ScriptException.TypeNotSavableException(s.element.getClass());
            }
        }
        File f = new File(ScriptManager.scriptDir,"data.dat");
        CompressedStreamTools.write(total,f);
    }

    public static ScriptType instanciateWithData(String typeName, NBTTagCompound tag) throws Exception {
        Class<? extends ScriptElement> typeClass = ScriptDecoder.getType(typeName);
        assert typeClass != null;
        if(typeClass != TypeString.class){
                try{
                    ScriptElement t = rawInstantiation(ScriptElement.class,typeClass);
                    if (!(t instanceof ISerialisable))
                        throw new ScriptException.TypeNotSavableException(t.getClass());
                    ISerialisable savable = (ISerialisable) t;
                    savable.read(tag);
                    return (ScriptType) t;
                }catch (Exception e){
                    e.printStackTrace();
                    throw new Exception("Could not instantiate type : "+typeName);
                }
        }else{
                TypeString s = new TypeString("");
                s.read(tag);
                return s;
        }
    }

    public static <T> T rawInstantiation(Class<?> parent, Class<T> child) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor objDef = parent.getDeclaredConstructor();
        Constructor intConstr = rf.newConstructorForSerialization(child, objDef);
        return child.cast(intConstr.newInstance());
    }

}
