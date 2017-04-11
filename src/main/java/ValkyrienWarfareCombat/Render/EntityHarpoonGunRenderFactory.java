package ValkyrienWarfareCombat.Render;

import ValkyrienWarfareCombat.Entity.EntityCannonBall;
import ValkyrienWarfareCombat.Entity.EntityCannonBasic;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityHarpoonGunRenderFactory implements IRenderFactory<EntityHarpoonGun> {

	@Override
	public Render<? super EntityHarpoonGun> createRenderFor(RenderManager manager) {
		return new EntityHarpoonGunRender(manager);
	}

	public static class EntityHarpoonRenderFactory implements IRenderFactory<EntityHarpoon> {
		@Override
		public Render<? super EntityHarpoon> createRenderFor(RenderManager manager) {
			return new EntityHarpoonRenderer(manager);
		}

	}
}
