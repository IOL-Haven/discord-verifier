package org.iolhaven.discordverifier.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(at = @At("RETURN"), method = "checkCanJoin", cancellable = true)
	private void init(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
		boolean out = cir.getReturnValue();

		boolean discordVerified = false;

		cir.setReturnValue(out && discordVerified);
	}
}