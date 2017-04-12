package ValkyrienWarfareCombat.Entity;

import java.util.Random;

import ValkyrienWarfareBase.API.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
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
		this.origin=origin;
		motionX = velocityVector.X;
		motionY = velocityVector.Y;
		motionZ = velocityVector.Z;
		prevRotationYaw = rotationYaw = origin.rotationYaw;
		prevRotationPitch = rotationPitch = origin.rotationPitch;
		prevPosX = lastTickPosX = posX = origin.posX;
		prevPosY = lastTickPosY = posY = origin.posY;
		prevPosZ = lastTickPosZ = posZ = origin.posZ;
	}

	@Override
	public void onHit(RayTraceResult raytrace){

		Entity entity = raytrace.entityHit;

		if(entity == null){
			this.setVelocity(0, 0, 0);
		}
		else if(!(entity instanceof EntityHarpoonGun)){//it hit an entity that isn't a harpoon gun
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
	public void onUpdate(){
		super.onUpdate();
		//if (!this.isEntityAlive()){
			System.out.print(this.posX + ",");
			System.out.print(this.posY + ",");
			System.out.println(this.posZ);
		//}
	}

	@Override
	protected ItemStack getArrowStack(){
		return null;
	}

}
