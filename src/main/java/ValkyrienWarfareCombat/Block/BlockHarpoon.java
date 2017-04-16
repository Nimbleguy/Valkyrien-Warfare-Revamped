package ValkyrienWarfareCombat.Block;

import ValkyrienWarfareBase.API.IBlockForceProvider;
import ValkyrienWarfareBase.API.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHarpoon extends Block implements IBlockForceProvider{
	public BlockHarpoon(Material mat){
		super(mat);
	}

	@Override
	public Vector getBlockForce(World world, BlockPos pos, IBlockState state, Entity shipEntity, double secondsToApply){
		return null; // TODO: make it go to harpoon
	}

	@Override
	public boolean isForceLocalCoords(World world, BlockPos pos, IBlockState state, double secondsToApply){
		return true;
	}

	@Override
	public Vector getBlockForcePosition(World world, BlockPos pos, IBlockState state, Entity shipEntity, double secondsToApply){
		return null;
	}
}
