package bossware.mixin.mixins;

import net.minecraft.network.NettyCompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={NettyCompressionDecoder.class})
public abstract class MixinNettyCompressionDecoder {
}

