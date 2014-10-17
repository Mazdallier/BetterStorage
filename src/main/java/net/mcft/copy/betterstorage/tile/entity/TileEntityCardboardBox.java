package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.inventory.InventoryCardboardBox;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.item.tile.ItemCardboardBox;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityCardboardBox extends TileEntityContainer {
	
	/** Whether this cardboard box was picked up and placed down again. <br>
	 *  If so, the box won't drop any more, as it's only usable once. */
	public boolean moved = false;
	public int color = -1;
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerCardboardBox; }
	
	@Override
	public int getRows() { return ItemCardboardBox.getRows(); }
	
	@Override
	public InventoryTileEntity makePlayerInventory() {
		return new InventoryTileEntity(this, new InventoryCardboardBox(contents));
	}
	
	@Override
	public void onBlockPlaced(EntityLivingBase player, ItemStack stack) {
		super.onBlockPlaced(player, stack);
		// If the cardboard box item has items, set the container contents to them.
		if (StackUtils.has(stack, "Items")) {
			ItemStack[] itemContents = StackUtils.getStackContents(stack, contents.length);
			System.arraycopy(itemContents, 0, contents, 0, itemContents.length);
			if (!ItemCardboardBox.isReusable())
				moved = true;
		}
		color = StackUtils.get(stack, -1, "display", "color");
	}
	
	@Override
	public void onBlockDestroyed() {
		if (!moved) {
			boolean empty = StackUtils.isEmpty(contents);
			ItemStack stack = new ItemStack(BetterStorageTiles.cardboardBox);
			if (!empty) StackUtils.setStackContents(stack, contents);
			if (color >= 0) StackUtils.set(stack, color, "display", "color");
			// Don't drop an empty cardboard box in creative.
			if (!empty || !brokenInCreative)
				WorldUtils.dropStackFromBlock(this, stack);
		}
		super.onBlockDestroyed();
	}
	
	@Override
	public void dropContents() {
		if (moved) super.dropContents();
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		if (color >= 0) compound.setInteger("color", color);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound compound = packet.func_148857_g();
		color = (compound.hasKey("color") ? compound.getInteger("color") : -1);
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		moved = compound.getBoolean("moved");
		color = (compound.hasKey("color") ? compound.getInteger("color") : -1);
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (moved) compound.setBoolean("moved", true);
		if (color >= 0) compound.setInteger("color", color);
	}
	
}
