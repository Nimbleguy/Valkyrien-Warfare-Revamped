package ValkyrienWarfareCombat.Render;

import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import ValkyrienWarfareCombat.Entity.EntityHarpoon;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class EntityHarpoonRenderer extends RenderArrow<EntityHarpoon> {

	private static final ResourceLocation RES = new ResourceLocation(ValkyrienWarfareCombatMod.MODID, "textures/items/harpoonproj.png");

	protected EntityHarpoonRenderer(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityHarpoon entity, double x, double y, double z, float entityYaw, float partialTicks){
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if(entity.origin != null){
			double offx = entity.origin.posX - entity.posX;
			double offy = entity.origin.posY - entity.posY;
			double offz = entity.origin.posZ - entity.posZ;
			HarpoonHelper.renderRope(x, y, z, offx, offy + 1.25, offz);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHarpoon entity) {
		return RES;
	}

}
