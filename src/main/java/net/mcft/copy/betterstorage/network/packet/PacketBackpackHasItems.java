package net.mcft.copy.betterstorage.network.packet;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/** Updates the "hasItems" status for equipped backpacks. */
public class PacketBackpackHasItems extends AbstractPacket {
	
	public boolean hasItems;
	
	public PacketBackpackHasItems() {  }
	public PacketBackpackHasItems(boolean hasItems) {
		this.hasItems = hasItems;
	}
	
	@Override
	public void encode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(hasItems);
	}
	
	@Override
	public void decode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException {
		hasItems = buffer.readBoolean();
	}
	
	@Override
	public void handleClientSide(EntityPlayer player) {
		ItemBackpack.getBackpackData(player).hasItems = hasItems;
	}
	
}