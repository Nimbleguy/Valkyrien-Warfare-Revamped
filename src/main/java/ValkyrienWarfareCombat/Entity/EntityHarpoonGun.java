package ValkyrienWarfareCombat.Entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
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

	private boolean newRider = false;
	private long lastFireTick = -TIME_COOLDOWN_TICKS;

	public EntityHarpoonGun(World worldIn){
		super(worldIn);
	}

	public void fire(EntityPlayer actor, ItemStack stack, EnumHand hand){

		Vec3d velocityNormal = getVectorForRotation(rotationPitch, rotationYaw);
		Vector velocityVector = new Vector(velocityNormal);

		PhysicsWrapperEntity wrapper = getParentShip();
		if(wrapper != null){
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWRotation, velocityVector);
		}

		velocityVector.multiply(2D);
		EntityHarpoon projectile = new EntityHarpoon(worldObj, velocityVector, this);

		projectile.shootingEntity = actor;

		Vector projectileSpawnPos = new Vector(0,.5,0);

		if(wrapper != null){
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWRotation, projectileSpawnPos);
		}

		projectile.posX += projectileSpawnPos.X;
		projectile.posY += projectileSpawnPos.Y;
		projectile.posZ += projectileSpawnPos.Z;

		worldObj.playBroadcastSound(155, new BlockPos(posX,posY,posZ), 1);
		worldObj.spawnEntityInWorld(projectile);

		lastFireTick = worldObj.getMinecraftServer().getTickCounter();
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand){

		boolean notRiding = false;

		if (player.getRidingEntity() != null){
			if (player.getRidingEntity().equals(this)){
				notRiding = true;
			}
		}

		boolean ret = super.processInitialInteract(player, stack, hand);

		if (player.getRidingEntity() != null){
			if (player.getRidingEntity().equals(this)){
				if (notRiding){
					newRider = true;
				}
			}
		}

		return ret;
	}

	@Override
	public void onRiderInteract(EntityPlayer player, ItemStack stack, EnumHand hand){
		if (!newRider){//no instant shooting
			if (!player.worldObj.isRemote){
				if (worldObj.getMinecraftServer().getTickCounter()-lastFireTick >= TIME_COOLDOWN_TICKS){

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
		}else{
			newRider=false;
		}
	}

	@Override
	public void doItemDrops(){
		ItemStack i = new ItemStack(ValkyrienWarfareCombatMod.instance.harpoonGunSpawner);
		i.setStackDisplayName(getName() == null? "HARPOON_GUN" : getName());//if getName() is null, set name to HARPOON_GUN. else set it to getName()
		entityDropItem(i,0F);
	}

}
