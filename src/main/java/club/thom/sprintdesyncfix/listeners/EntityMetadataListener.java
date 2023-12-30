package club.thom.sprintdesyncfix.listeners;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.DataWatcher;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

@ChannelHandler.Sharable
public class EntityMetadataListener extends ChannelInboundHandlerAdapter {
    private final Field serverSprintState;

    public EntityMetadataListener() {
        String[] fieldNames = new String[] {"serverSprintState", "field_175171_bO"};
        serverSprintState = ReflectionHelper.findField(EntityPlayerSP.class, fieldNames);
        serverSprintState.setAccessible(true);
    }


    @SubscribeEvent
    public void connect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        // Add this class to the pipeline
        ChannelPipeline pipeline = event.manager.channel().pipeline();
        pipeline.addBefore("packet_handler", this.getClass().getName(), this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof S1CPacketEntityMetadata) {
            processPacket((S1CPacketEntityMetadata) msg);
        }

        ctx.fireChannelRead(msg);
    }

    private void processPacket(S1CPacketEntityMetadata packet) {
        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().thePlayer.getEntityId() != packet.getEntityId()) {
            // This packet is for another entity.
            return;
        }

        boolean sprintingState = false;
        boolean foundSprintingState = false;

        for (DataWatcher.WatchableObject watchedObject : packet.func_149376_c()) {
            // The watched object that Minecraft stores the sprinting state in
            if (watchedObject.getDataValueId() == 0) {
                sprintingState = ((Byte) watchedObject.getObject() & 1 << 3) != 0;
                foundSprintingState = true;
                break;
            }
        }

        if (!foundSprintingState) {
            return;
        }

        try {
            serverSprintState.set(Minecraft.getMinecraft().thePlayer, sprintingState);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
