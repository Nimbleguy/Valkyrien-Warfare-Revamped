package ValkyrienWarfareCombat.Entity;

import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityHarpoonGun extends EntityMountingWeaponBase{

	private boolean loaded = true;
	
	public EntityHarpoonGun(World worldIn){
		super(worldIn);
	}

	public void fireHarpoonGun(EntityPlayer actor, ItemStack ammo, EnumHand hand){
		
		
		
		loaded = false;
	}
	
	@Override
	public void onRiderInteract(EntityPlayer player, ItemStack stack, EnumHand hand){
		
	}

	@Override
	public void doItemDrops(){
		ItemStack i = new ItemStack(ValkyrienWarfareCombatMod.instance.harpoonGunSpawner);
		i.setStackDisplayName(getName() == null? "HARPOON_GUN" : getName());//if getName() is null, set name to HARPOON_GUN. else set it to getName()
		entityDropItem(i,0F);
	}

}
