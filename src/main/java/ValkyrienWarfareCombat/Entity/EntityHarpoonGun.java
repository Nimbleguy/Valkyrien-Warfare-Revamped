package ValkyrienWarfareCombat.Entity;

import java.util.ArrayList;
import java.util.List;

import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityHarpoonGun extends EntityMountingWeaponBase{

	long lastFire = -Long.MIN_VALUE;

	private boolean loaded = false;
	private boolean powdered = false;

	private final static int POWDER_NEEDED = 4;

	public EntityHarpoonGun(World worldIn){
		super(worldIn);
	}

	public void fire(EntityPlayer actor, ItemStack ammo, EnumHand hand){

		

		loaded = false;
	}

	@Override
	public void onRiderInteract(EntityPlayer player, ItemStack stack, EnumHand hand){

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

				else if (is.getItem().equals(Items.GUNPOWDER)){
					gunpowderSum+=is.stackSize;
					gunpowders.add(is);
				}

				if (harpoon != null){
					if (gunpowderSum >= POWDER_NEEDED){

						gunpowderSum=POWDER_NEEDED;//this and the for loop for removing necessary amount of gunpowder
						for (ItemStack s : gunpowders){
							s.stackSize=Math.max(s.stackSize-gunpowderSum,0);
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

	@Override
	public void doItemDrops(){
		ItemStack i = new ItemStack(ValkyrienWarfareCombatMod.instance.harpoonGunSpawner);
		i.setStackDisplayName(getName() == null? "HARPOON_GUN" : getName());//if getName() is null, set name to HARPOON_GUN. else set it to getName()
		entityDropItem(i,0F);
	}

}
