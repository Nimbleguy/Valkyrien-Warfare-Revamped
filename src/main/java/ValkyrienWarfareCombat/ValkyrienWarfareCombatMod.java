package ValkyrienWarfareCombat;

import ValkyrienWarfareCombat.Entity.EntityCannonBall;
import ValkyrienWarfareCombat.Entity.EntityCannonBasic;
import ValkyrienWarfareCombat.Entity.EntityHarpoon;
import ValkyrienWarfareCombat.Entity.EntityHarpoonGun;
import ValkyrienWarfareCombat.Item.ItemBasicCannon;
import ValkyrienWarfareCombat.Item.ItemCannonBall;
import ValkyrienWarfareCombat.Item.ItemHarpoonGun;
import ValkyrienWarfareCombat.Item.ItemPowderPouch;
import ValkyrienWarfareCombat.Item.ItemHarpoon;
import ValkyrienWarfareCombat.Network.PacketHarpoon;
import ValkyrienWarfareCombat.Proxy.CommonProxyCombat;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ValkyrienWarfareCombatMod.MODID, name = ValkyrienWarfareCombatMod.MODNAME, version = ValkyrienWarfareCombatMod.MODVER)
public class ValkyrienWarfareCombatMod {

	@SidedProxy(clientSide = "ValkyrienWarfareCombat.Proxy.ClientProxyCombat", serverSide = "ValkyrienWarfareCombat.Proxy.CommonProxyCombat")
	public static CommonProxyCombat proxy;

	public static final String MODID = "valkyrienwarfarecombat";
	public static final String MODNAME = "Valkyrien Warfare Combat";
	public static final String MODVER = "0.1";

	public static ValkyrienWarfareCombatMod instance;

	public Item basicCannonSpawner;
	public Item cannonBall;
	public Item powderPouch;
	public Item harpoon;
	public Item harpoonGunSpawner;

	public Block fakeCannonBlock;
	public Block fakeHarpoonGunBlock;

	public SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID.toLowerCase());

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;
		registerBlocks(event);
		registerItems(event);
		registerRecipies(event);
		registerEntities(event);
		registerPackets(event);
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	private void registerItems(FMLStateEvent event) {
		basicCannonSpawner = new ItemBasicCannon().setUnlocalizedName("basiccannonspawner").setRegistryName(MODID, "basiccannonspawner").setCreativeTab(CreativeTabs.COMBAT).setMaxStackSize(4);
		cannonBall = new ItemCannonBall().setUnlocalizedName("cannonball").setRegistryName(MODID, "cannonball").setCreativeTab(CreativeTabs.COMBAT).setMaxStackSize(32);
		powderPouch = new ItemPowderPouch().setUnlocalizedName("powderpouch").setRegistryName(MODID, "powderpouch").setCreativeTab(CreativeTabs.COMBAT).setMaxStackSize(32);
		harpoon = new ItemHarpoon().setUnlocalizedName("harpoon").setRegistryName(MODID, "harpoon").setCreativeTab(CreativeTabs.COMBAT).setMaxStackSize(32);
		harpoonGunSpawner = new ItemHarpoonGun().setUnlocalizedName("harpoongunspawner").setRegistryName(MODID, "harpoongunspawner").setCreativeTab(CreativeTabs.COMBAT).setMaxStackSize(1);

		GameRegistry.register(basicCannonSpawner);
		GameRegistry.register(cannonBall);
		GameRegistry.register(powderPouch);
		GameRegistry.register(harpoon);
		GameRegistry.register(harpoonGunSpawner);
	}

	private void registerEntities(FMLStateEvent event) {
		EntityRegistry.registerModEntity(EntityCannonBasic.class, "EntityCannonBasic", 71, this, 120, 1, false);
		EntityRegistry.registerModEntity(EntityCannonBall.class, "EntityCannonBall", 72, this, 120, 5, true);
		EntityRegistry.registerModEntity(EntityHarpoonGun.class, "EntityHarpoonGun", 73, this, 120, 1, false);
		EntityRegistry.registerModEntity(EntityHarpoon.class, "EntityHarpoon", 74, this, 120, 5, true);
	}

	private void registerBlocks(FMLStateEvent event) {
		fakeCannonBlock = new FakeCannonBlock(Material.IRON).setHardness(5f).setUnlocalizedName("fakeCannonBlock").setRegistryName(MODID, "fakeCannonBlock").setCreativeTab(CreativeTabs.REDSTONE);
		fakeHarpoonGunBlock = new FakeHarpoonGunBlock(Material.IRON).setHardness(5f).setUnlocalizedName("fakeHarpoonGunBlock").setRegistryName(MODID, "fakeHarpoonGunBlock").setCreativeTab(CreativeTabs.REDSTONE);

		GameRegistry.registerBlock(fakeCannonBlock);
		GameRegistry.registerBlock(fakeHarpoonGunBlock);
	}

	private void registerRecipies(FMLStateEvent event) {
		GameRegistry.addRecipe(new ItemStack(cannonBall, 4), new Object[] { "II ", "II ", "   ", 'I', Items.IRON_INGOT });
		GameRegistry.addRecipe(new ItemStack(powderPouch, 4), new Object[] { " S ", "SGS", " S ", 'S', Items.STRING, 'G', Items.GUNPOWDER });
		GameRegistry.addRecipe(new ItemStack(harpoon, 1), new Object[] {" II", "SWI", "WS ", 'I', Items.IRON_INGOT, 'S', Items.STRING, 'W', Items.STICK});
	}

	private void registerPackets(FMLStateEvent event){
		int id = 0;
		network..registerMessage(PacketHarpoon.class, PacketHarpoon.class, id++, Side.CLIENT);
	}
}
