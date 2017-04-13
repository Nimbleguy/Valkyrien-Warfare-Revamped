package ValkyrienWarfareCombat.Entity;

import java.util.ArrayList;
import java.util.List;

import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityHarpoonGun extends EntityMountingWeaponBase{

	//TODO make below two vars configurable
	private final static int POWDER_NEEDED = 4;//amount of powder pouches needed per shot
	private final static int TIME_COOLDOWN_TICKS = 60;//3 sec

	private long lastFireTick = -TIME_COOLDOWN_TICKS;

	public EntityHarpoon harpoon;

	public EntityHarpoonGun(World worldIn){
		super(worldIn);
		this.height = 3;
		this.width = 2;
	}

	public void fire(EntityPlayer actor, ItemStack stack, EnumHand hand){
		if(harpoon != null){
			harpoon.setDead();
			harpoon = null;
		}

		Vec3d velocityNormal = getVectorForRotation(rotationPitch, rotationYaw);
		Vector velocityVector = new Vector(velocityNormal);

		PhysicsWrapperEntity wrapper = getParentShip();
		if(wrapper != null){
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWRotation, velocityVector);
		}

		velocityVector.multiply(5D);
		EntityHarpoon projectile = new EntityHarpoon(worldObj, velocityVector, this);

		projectile.shootingEntity = actor;

		Vector projectileSpawnPos = new Vector(0,.5,0);

		if(wrapper != null){
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWRotation, projectileSpawnPos);
		}

		projectile.setPosition(projectile.posX + projectileSpawnPos.X, projectile.posY, projectile.posZ);
		projectile.setPosition(projectile.posX, projectile.posY + projectileSpawnPos.Y, projectile.posZ);
		projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ + projectileSpawnPos.Z);

		worldObj.spawnEntityInWorld(projectile);

		lastFireTick = worldObj.getTotalWorldTime();
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		if (!worldObj.isRemote){
			if (worldObj.getBlockState(new BlockPos(posX,((int)posY)-1,posZ)).getMaterial().equals(Material.AIR)){
				this.kill();
				this.doItemDrops();
			}
		}

		if (getRider() != null){
			double yaw = ((rotationYaw-90)*Math.PI)/180;
			
			final double offsetBack = 1.5;//how far back the player is sitting
			
			double h = offsetBack*(Math.sin(yaw));
			double w = offsetBack*(Math.cos(yaw));
			
			getRider().setPosition(posX+w,posY,posZ+h);
		}
		else if(!worldObj.isRemote){
			lastFireTick = worldObj.getTotalWorldTime();
		}
	}

	@Override
	public void onRiderInteract(EntityPlayer player, ItemStack stack, EnumHand hand){
		if (!player.worldObj.isRemote){
			if (worldObj.getTotalWorldTime()-lastFireTick >= TIME_COOLDOWN_TICKS){

				ItemStack harpoon = null;
				List<ItemStack> gunpowders = new ArrayList<ItemStack>();
				int gunpowderSum = 0;

				for (ItemStack[] inv : player.inventory.allInventories){
					for (ItemStack is : inv){

						if (is == null)
							continue;

						if (is.getItem().equals(ValkyrienWarfareCombatMod.instance.harpoon)){
							if (harpoon == null)
								harpoon = is;
						}

						else if (is.getItem().equals(ValkyrienWarfareCombatMod.instance.powderPouch)){
							gunpowderSum+=is.stackSize;
							gunpowders.add(is);
						}

						int prevSize;

						if (harpoon != null){
							if (gunpowderSum >= POWDER_NEEDED){

								gunpowderSum=POWDER_NEEDED;//this and the for loop for removing necessary amount of gunpowder
								for (ItemStack s : gunpowders){
									prevSize = s.stackSize;
									s.stackSize=Math.max(s.stackSize-gunpowderSum,0);
									gunpowderSum-=Math.min(Math.max(prevSize,0),gunpowderSum);
									if (s.stackSize<=0){
										int index = player.inventory.getSlotFor(s);
										player.inventory.setInventorySlotContents(index, null);
									}
								}
								gunpowders = null;

								harpoon.stackSize--;
								if (harpoon.stackSize<=0){
									int index = player.inventory.getSlotFor(harpoon);
									player.inventory.setInventorySlotContents(index, null);
								}

								harpoon = null;

								fire(player,stack,hand);
								return;//cancel the loop
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void doItemDrops(){
		ItemStack i = new ItemStack(ValkyrienWarfareCombatMod.instance.harpoonGunSpawner);
		entityDropItem(i,0F);
	}

}
