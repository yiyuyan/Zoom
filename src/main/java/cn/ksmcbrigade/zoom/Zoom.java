package cn.ksmcbrigade.zoom;

import cn.ksmcbrigade.vmr.module.Config;
import cn.ksmcbrigade.vmr.module.Module;
import cn.ksmcbrigade.vmr.uitls.ModuleUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static cn.ksmcbrigade.vmr.module.Config.configDir;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("zoom")
@Mod.EventBusSubscriber
public class Zoom {

    public static File config = new File("config/zoom.json");

    public static boolean EnabledTick = false;

    public static double zoom = 25.0D;

    public static double nowZoom = zoom;

    public static KeyMapping keyMapping = new KeyMapping("zoom.name", GLFW.GLFW_KEY_B,"key.categories.gameplay");

    public Zoom() throws IOException {
        MinecraftForge.EVENT_BUS.register(this);

        //zoom
        if(!config.exists()){
            save();
        }
        zoom = JsonParser.parseString(Files.readString(config.toPath())).getAsJsonObject().get("zoom").getAsDouble();

        //other module
        ModuleUtils.add(new Module("hack.name.hj"){
            @Override
            public void enabled(Minecraft MC) throws Exception {
                if(getConfig()==null){
                    JsonObject object = new JsonObject();
                    object.addProperty("block",6.0f);
                    setConfig(new Config(new File("HighJump"),object));
                }
                File pathFile = new File(configDir,getConfig().file.getPath()+".json");
                getConfig().setData(JsonParser.parseString(Files.readString(pathFile.toPath())).getAsJsonObject());
            }
        });
    }

    @SubscribeEvent
    public void key(RegisterKeyMappingsEvent event){
        event.register(keyMapping);
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event){
        EnabledTick = keyMapping.isDown();

        if(nowZoom==0.0D || !EnabledTick){
            nowZoom = zoom;
        }
    }

    @SubscribeEvent
    public void mouseEvent(InputEvent.MouseScrollingEvent event){

        if(EnabledTick){
            if(nowZoom==0.0D){
                nowZoom = zoom;
            }

            if(event.getDeltaY()>0){
                nowZoom*=1.1;
            }
            else if(event.getDeltaY()<0){
                nowZoom*=0.9;

            }
        }
    }

    @SubscribeEvent
    public void change(RegisterClientCommandsEvent event){
        event.getDispatcher().register(Commands.literal("zoom").executes(context -> {
            Entity entity = context.getSource().getEntity();
            if(entity!=null){
                entity.sendSystemMessage(Component.nullToEmpty(I18n.get("zoom.command").replace("{v}",String.valueOf(zoom))));
            }
            return 0;
        }).then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes(context -> {
            zoom = DoubleArgumentType.getDouble(context,"value");
            if(nowZoom==0.0D){
                nowZoom = zoom;
            }
            try {
                save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 0;
        })));
    }

    public static void save() throws IOException{
        JsonObject object = new JsonObject();
        object.addProperty("zoom",zoom);
        Files.write(config.toPath(),object.toString().getBytes());
    }

    public static double getZoom(Double fov){

        if(nowZoom==0.0D){
            nowZoom = zoom;
        }

        return fov / nowZoom;
    }
}
