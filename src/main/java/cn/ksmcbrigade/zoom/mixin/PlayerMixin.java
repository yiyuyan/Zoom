package cn.ksmcbrigade.zoom.mixin;

import cn.ksmcbrigade.vmr.uitls.ModuleUtils;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Unique
    public float getJumpBoostPower() {
        float r = super.getJumpBoostPower();
        //System.out.println(r);
        if(ModuleUtils.enabled("hack.name.hj")){
            if(ModuleUtils.get("hack.name.hj").getConfig()==null){
                r+=0.1F*6.0F;
            }
            else{
                JsonElement config = ModuleUtils.get("hack.name.hj").getConfig().get("block");
                //System.out.println(config);
                if(config==null){
                    r+=0.1F*6.0F;
                }
                else{
                    r+= config.getAsFloat()*0.1F;
                }
            }
        }
        //System.out.println(r);
        return r;
    }
}
