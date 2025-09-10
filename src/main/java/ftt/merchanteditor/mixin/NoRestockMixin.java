package ftt.merchanteditor.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class NoRestockMixin {

    @Inject(method = "restock", at = @At("HEAD"), cancellable = true)
    private void merchanteditor$cancelRestock(CallbackInfo ci) {
        if (((VillagerEntity) (Object) this).getCommandTags().contains("official")) {
            ci.cancel();
        }
    }

    @Inject(method = "canRestock", at = @At("HEAD"), cancellable = true)
    private void merchanteditor$denyCanRestock(CallbackInfoReturnable<Boolean> cir) {
        if (((VillagerEntity) (Object) this).getCommandTags().contains("official")) {
            cir.setReturnValue(false);
        }
    }
}
