package org.example.e.untitled5.Module.Modules;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.example.e.untitled5.Module.Aim.Utils.Modules;

import java.util.List;

public class Aim extends Modules {

    private float kickYaw = 0;
    private float kickPitch = 0;
    private boolean phase1 = false;
    private int hitCounter = 0;
    private Entity missTarget = null;

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event){
        if(event.getPlayer() != mc.player) return;

        hitCounter++;
        int missInterval = 10;
        if(hitCounter % missInterval == 0){
            missTarget = event.getTarget();
            kickYaw = (float)(Math.random() * 10 - 5);
            kickPitch = (float)(Math.random() * 4 - 2);
            phase1 = true;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END || mc.player == null || mc.level == null) return;

        double radius = 5.0;
        AxisAlignedBB box = new AxisAlignedBB(mc.player.getX() - radius, mc.player.getY() - radius, mc.player.getZ() - radius, mc.player.getX() + radius, mc.player.getY() + radius, mc.player.getZ() + radius);

        List<Entity> entities = mc.level.getEntitiesOfClass(Entity.class, box, e -> e != mc.player && e.isAlive());
        if(entities.isEmpty()) return;

        Entity target = (phase1 && missTarget != null && missTarget.isAlive()) ? missTarget : entities.get(0);

        applyAim(target);
    }

    private void applyAim(Entity target){
        double dx = target.getX() - mc.player.getX();
        double dy = (target.getY() + target.getEyeHeight()) - (mc.player.getY() + mc.player.getEyeHeight());
        double dz = target.getZ() - mc.player.getZ();
        double dist = Math.sqrt(dx*dx + dz*dz);

        float targetYaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        if(phase1){
            mc.player.yRot = smooth(mc.player.yRot, targetYaw + kickYaw, 0.3f);
            mc.player.xRot = smooth(mc.player.xRot, targetPitch + kickPitch, 0.3f);

            if(Math.abs(wrapAngle(mc.player.yRot - (targetYaw + kickYaw))) < 1f){
                phase1 = false;
                missTarget = null;
            }
        } else {
            mc.player.yRot = smooth(mc.player.yRot, targetYaw, 0.3f);
            mc.player.xRot = smooth(mc.player.xRot, targetPitch, 0.3f);
        }
    }

    private float smooth(float current, float target, float factor){
        float diff = wrapAngle(target - current);
        return current + diff * factor;
    }

    private float wrapAngle(float angle){
        angle %= 360f;
        if(angle >= 180f) angle -= 360f;
        if(angle < -180f) angle += 360f;
        return angle;
    }

    public Aim(int key, String name) {
        super(key, name);
    }
}
