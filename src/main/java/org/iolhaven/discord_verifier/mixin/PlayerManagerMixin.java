package org.iolhaven.discord_verifier.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.iolhaven.discord_verifier.DiscordVerifier;
import org.iolhaven.discord_verifier.UserManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

// Gets injected into the class which checks server authentication, and adds an extra check to make sure that the player verified with the bot.
// It will only check for verification if all other conditions for connecting are met.
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(at = @At("RETURN"), method = "checkCanJoin", cancellable = true)
	private void init(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
		// Checks with the UserManager singleton to see whether there is an entry associating this profile with a Discord account that verified it.
		boolean discordVerified = UserManager.getInstance().userIsVerified(profile);

		if(cir.getReturnValue() == null && !discordVerified) {
			cir.setReturnValue(Text.literal("To gain access, you need to verify your Minecraft account using the Discord bot!"));
			DiscordVerifier.LOGGER.trace("Denied access to user {}", profile);
			return;
		}

		DiscordVerifier.LOGGER.trace("Allowed access to user {}", profile);
	}
}