package cn.ksmcbrigade.zoom.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class Zoom {
    @Inject(method = "getFov",at = @At("RETURN"), cancellable = true)
    public void fov(Camera p_109142_, float p_109143_, boolean p_109144_, CallbackInfoReturnable<Double> cir){
        if(cn.ksmcbrigade.zoom.Zoom.EnabledTick){
            cir.setReturnValue(cn.ksmcbrigade.zoom.Zoom.getZoom(cir.getReturnValue()));
        }
    }
}
