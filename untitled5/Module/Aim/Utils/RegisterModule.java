package org.example.e.untitled5.Module.Aim.Utils;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.example.e.untitled5.Module.Modules.Aim;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CopyOnWriteArrayList;

public class RegisterModule {
    public static CopyOnWriteArrayList<Modules> m = new CopyOnWriteArrayList<>();
    public static void register(){
        m.add(new Aim(GLFW.GLFW_KEY_M,"K"));
    }
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event){
     if (!(event.getAction() == GLFW.GLFW_PRESS))return;
        for (Modules module : m){
            if(event.getKey() == module.getKey()){
                module.isToggle();
            }
        }

    }
}
