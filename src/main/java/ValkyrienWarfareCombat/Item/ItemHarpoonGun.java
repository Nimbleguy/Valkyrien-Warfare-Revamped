package ValkyrienWarfareCombat.Item;

import ValkyrienWarfareCombat.Entity.EntityHarpoonGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHarpoonGun extends Item {

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
			return EnumActionResult.PASS;

		EnumFacing playerFacing = playerIn.getHorizontalFacing();
		EntityHarpoonGun gun = new EntityHarpoonGun(worldIn);
		gun.setFacing(playerFacing);
		gun.setPosition(pos.getX() + .5D, pos.getY() + 1D, pos.getZ() + .5D);
		worldIn.spawnEntityInWorld(gun);
		stack.stackSize--;
		return EnumActionResult.SUCCESS;
	}
}
