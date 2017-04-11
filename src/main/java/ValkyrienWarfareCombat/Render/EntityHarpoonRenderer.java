package ValkyrienWarfareCombat.Render;

import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import ValkyrienWarfareCombat.Entity.EntityHarpoon;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.util.ResourceLocation;

public class EntityHarpoonRenderer extends RenderArrow<EntityHarpoon> {

	private static final ResourceLocation RES = new ResourceLocation(ValkyrienWarfareCombatMod.MODID, "textures/items/harpoonproj.png");

	protected EntityCannonBallRenderer(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHarpoon entity) {
		return null;
	}

}
