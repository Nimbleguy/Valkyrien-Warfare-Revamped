package ValkyrienWarfareBase.Block;

import ValkyrienWarfareBase.ValkyrienWarfareMod;
import ValkyrienWarfareBase.Capability.IAirshipCounterCapability;
import ValkyrienWarfareBase.Interaction.ShipNameUUIDData;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareBase.PhysicsManagement.WorldPhysObjectManager;
import ValkyrienWarfareBase.Relocation.DetectorManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockPhysicsInfuserCreative extends Block {

	public BlockPhysicsInfuserCreative(Material materialIn) {
		super(materialIn);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			WorldPhysObjectManager manager = ValkyrienWarfareMod.physicsManager.getManagerForWorld(worldIn);
			if (manager != null) {
				PhysicsWrapperEntity wrapperEnt = manager.getManagingObjectForChunk(worldIn.getChunkFromBlockCoords(pos));
				if (wrapperEnt != null) {
					wrapperEnt.wrapping.doPhysics = !wrapperEnt.wrapping.doPhysics;
					return true;
				}
			}
			
			if (ValkyrienWarfareMod.canChangeAirshipCounter(true, playerIn))	{
				PhysicsWrapperEntity wrapper = new PhysicsWrapperEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), playerIn, DetectorManager.DetectorIDs.BlockPosFinder.ordinal());
				worldIn.spawnEntityInWorld(wrapper);

				IAirshipCounterCapability counter = playerIn.getCapability(ValkyrienWarfareMod.airshipCounter, null);
				counter.onCreate();
				
				wrapper.setCustomNameTagInitial(playerIn.getName() + ":" + counter.getAirshipCountEver());
 				ShipNameUUIDData.get(worldIn).placeShipInRegistry(wrapper, wrapper.getCustomNameTag());
				//playerIn.addChatMessage(new TextComponentString("You've made " + counter.getAirshipCount() + " airships!"));
			} else {
				playerIn.addChatMessage(new TextComponentString("You've made too many airships! The limit per player is " + ValkyrienWarfareMod.maxAirships));
			}
		}
		return true;
	}

}
