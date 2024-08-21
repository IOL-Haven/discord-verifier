package org.iolhaven.discord_verifier.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.iolhaven.discord_verifier.UserManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(at = @At("RETURN"), method = "checkCanJoin", cancellable = true)
	private void init(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
		boolean discordVerified = UserManager.getInstance().userIsVerified(profile);

		if(cir.getReturnValue() == null && !discordVerified) {
			cir.setReturnValue(Text.translatable("discord_verifier.not_verified"));
		}
	}
}