package ValkyrienWarfareCombat.Proxy;

import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import ValkyrienWarfareCombat.Entity.EntityCannonBall;
import ValkyrienWarfareCombat.Entity.EntityHarpoon;
import ValkyrienWarfareCombat.Entity.EntityCannonBasic;
import ValkyrienWarfareCombat.Entity.EntityHarpoonGun;
import ValkyrienWarfareCombat.Render.EntityCannonBasicRenderFactory;
import ValkyrienWarfareCombat.Render.EntityHarpoonGunRenderFactory;
import ValkyrienWarfareControl.ValkyrienWarfareControlMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxyCombat extends CommonProxyCombat {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		OBJLoader.INSTANCE.addDomain(ValkyrienWarfareCombatMod.MODID.toLowerCase());
		RenderingRegistry.registerEntityRenderingHandler(EntityCannonBasic.class, new EntityCannonBasicRenderFactory());
		RenderingRegistry.registerEntityRenderingHandler(EntityHarpoonGun.class, new EntityHarpoonGunRenderFactory());
		RenderingRegistry.registerEntityRenderingHandler(EntityCannonBall.class, new EntityCannonBasicRenderFactory.EntityCannonBallRenderFactory());
		RenderingRegistry.registerEntityRenderingHandler(EntityHarpoon.class, new EntityHarpoonGunRenderFactory.EntityHarpoonRenderFactory());
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
		registerItemModel(ValkyrienWarfareCombatMod.instance.basicCannonSpawner);
		registerItemModel(ValkyrienWarfareCombatMod.instance.cannonBall);
		registerItemModel(ValkyrienWarfareCombatMod.instance.powderPouch);
		registerItemModel(ValkyrienWarfareCombatMod.instance.harpoon);
		registerItemModel(ValkyrienWarfareCombatMod.instance.harpoonGunSpawner);
	}

	private void registerItemModel(Item toRegister) {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(toRegister, 0, new ModelResourceLocation(ValkyrienWarfareCombatMod.MODID + ":" + toRegister.getUnlocalizedName().substring(5), "inventory"));
		;
	}
}
