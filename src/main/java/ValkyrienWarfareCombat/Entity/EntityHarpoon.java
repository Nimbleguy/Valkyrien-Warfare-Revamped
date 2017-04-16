package ValkyrienWarfareCombat.Entity;

import java.util.Random;

import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import ValkyrienWarfareCombat.Network.PacketHarpoon;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityHarpoon extends EntityArrow{

	//TODO make below two vars configurable
	public static final int MAX_DMG = 20;//10 hearts
	public static final int MIN_DMG = 5;//2.5 hearts

	public EntityHarpoonGun origin;

	public EntityHarpoon(World worldIn) {
		super(worldIn);
	}

	public EntityHarpoon(World worldObj, Vector velocityVector, EntityHarpoonGun origin){
		super(worldObj);
		origin.harpoon = this;
		this.origin = origin;
		this.setVelocity(velocityVector.X, velocityVector.Y, velocityVector.Z);
		this.setRotation(origin.rotationYaw, origin.rotationPitch);
		prevRotationYaw = origin.rotationYaw;
		prevRotationPitch = origin.rotationPitch;
		this.setPosition(origin.posX, origin.posY, origin.posZ);
	}

	@Override
	public void onHit(RayTraceResult raytrace){

		Entity entity = raytrace.entityHit;

		if(!this.worldObj.isRemote && (this.origin == null || this.origin.isDead)){
			this.kill();
		}

		if(entity == null){//its a block
			if(this.motionX != 0 && this.motionY != 0 && this.motionZ != 0 && !this.worldObj.isRemote){
				ValkyrienWarfareCombatMod.instance.network.sendToAll(new PacketHarpoon(this.origin.getEntityId(), this.getEntityId()));
			}

			BlockPos blockpos = raytrace.getBlockPos();
			IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
			this.motionX = (double)((float)(raytrace.hitVec.xCoord - this.posX));
			this.motionY = (double)((float)(raytrace.hitVec.yCoord - this.posY));
			this.motionZ = (double)((float)(raytrace.hitVec.zCoord - this.posZ));
			float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
			this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
			this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
			this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
			this.inGround = true;
			this.arrowShake = 7;
			this.setIsCritical(false);

			if (iblockstate.getMaterial() != Material.AIR)
			{
				worldObj.getBlockState(raytrace.getBlockPos()).getBlock().onEntityCollidedWithBlock(this.worldObj, blockpos, iblockstate, this);
			}

			this.setVelocity(0, 0, 0);
		}
		else if(!(entity instanceof EntityHarpoonGun || entity.equals(this))){//it hit an entity that isn't a harpoon gun
			DamageSource damagesource;

			if (shootingEntity == null){
				damagesource = DamageSource.causeArrowDamage(this, this);
			}else{
				damagesource = DamageSource.causeArrowDamage(this, shootingEntity);
			}

			if (entity.attackEntityFrom(damagesource, new Random().nextInt(MAX_DMG+1-MIN_DMG)+MIN_DMG)){

				if (entity instanceof EntityLivingBase){

					EntityLivingBase liveEnt = (EntityLivingBase) entity;

					if (shootingEntity instanceof EntityLivingBase){
						EnchantmentHelper.applyThornEnchantments(liveEnt, shootingEntity);
						EnchantmentHelper.applyArthropodEnchantments(liveEnt, shootingEntity);
					}

					arrowHit(liveEnt);
				}
			}
		}
	}

	@Override
	protected ItemStack getArrowStack(){
		return null;
	}

}
