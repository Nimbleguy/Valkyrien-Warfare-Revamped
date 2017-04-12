package ValkyrienWarfareCombat.Render;

import org.lwjgl.opengl.GL11;

import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import ValkyrienWarfareCombat.Entity.EntityHarpoon;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
			GlStateManager.pushMatrix();

			GlStateManager.disableTexture2D();
			GlStateManager.disableDepth();

			Tessellator t = Tessellator.getInstance();
			VertexBuffer v = t.getBuffer();
			v.begin(GL11.GL_LINE, DefaultVertexFormats.POSITION_COLOR);
			v.pos(x, y, z);
			v.color(141, 81, 24, 255);
			v.endVertex();
			v.pos(entity.origin.posX, entity.origin.posY, entity.origin.posZ);
			v.color(141, 81, 24, 255);
			v.endVertex();
			t.draw();

			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHarpoon entity) {
		return RES;
	}

}
