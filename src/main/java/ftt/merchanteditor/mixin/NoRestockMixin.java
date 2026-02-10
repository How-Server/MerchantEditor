package ftt.merchanteditor.mixin;

import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class NoRestockMixin {

    @Inject(method = "restock", at = @At("HEAD"), cancellable = true)
    private void merchanteditor$cancelRestock(CallbackInfo ci) {
        if (((Villager) (Object) this).getTags().contains("official")) {
            ci.cancel();
        }
    }

    @Inject(method = "allowedToRestock", at = @At("HEAD"), cancellable = true)
    private void merchanteditor$denyCanRestock(CallbackInfoReturnable<Boolean> cir) {
        if (((Villager) (Object) this).getTags().contains("official")) {
            cir.setReturnValue(false);
        }
    }
}
