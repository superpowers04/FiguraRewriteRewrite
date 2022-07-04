package org.moon.figura.mixin.gui;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.utils.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    @Inject(at = @At("RETURN"), method = "getSystemInformation")
    protected void getSystemInformation(CallbackInfoReturnable<List<String>> cir) {
        if (AvatarManager.panic) return;

        List<String> lines = cir.getReturnValue();

        int i = 0;
        for (; i < lines.size(); i++) {
            if (lines.get(i).equals(""))
                break;
        }

        lines.add(++i, "§b[" + FiguraMod.MOD_NAME + "]§r");
        lines.add(++i, "Version: " + FiguraMod.VERSION);

        Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        if (avatar != null && avatar.nbt != null) {
            lines.add(++i, String.format("Model Complexity: %d", avatar.complexity));

            //if (avatar.renderer != null)
            //    lines.add(++i, String.format("Animations Complexity: %d", animRendered));

            //has script
            if (avatar.luaState != null) {
                lines.add(++i, String.format("Script Memory: %s", MathUtils.asFileSize(avatar.getScriptMemory()).toUpperCase()));

                if (FiguraMod.DO_OUR_NATIVES_WORK) {
                    lines.add(++i, String.format("Init instructions: %d", avatar.initInstructions));

                    lines.add(++i, String.format("Tick instructions: %d (W: %d E: %d)", avatar.accumulatedTickInstructions, avatar.worldTickInstructions, avatar.entityTickInstructions));
                    lines.add(++i, String.format("Render instructions: %d (W: %d E: %d PE: %d PW: %d)",
                            avatar.accumulatedEntityRenderInstructions + avatar.accumulatedWorldRenderInstructions,
                            avatar.worldRenderInstructions,
                            avatar.entityRenderInstructions,
                            avatar.postEntityRenderInstructions,
                            avatar.postWorldRenderInstructions)
                    );
                } else {
                    lines.add(++i, "Sorry, but instruction counts are ");
                    lines.add(++i, "only supported on Windows (Maybe Linux?)");
                    lines.add(++i, "right now :( We're working on it!");
                }
            }
        }
        //lines.add(++i, String.format("Pings per second: ↑%d, ↓%d", pingSent, pingReceived));

        lines.add(++i, "");
    }
}
