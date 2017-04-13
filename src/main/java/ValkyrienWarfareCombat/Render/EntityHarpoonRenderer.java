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
			GL11.glPushMatrix();

			double offx = entity.origin.posX - entity.posX;
			double offy = entity.origin.posY - entity.posY;
			double offz = entity.origin.posZ - entity.posZ;

			GlStateManager.disableTexture2D();
			//GlStateManager.disableDepth();
			GlStateManager.disableLighting();

			GlStateManager.glLineWidth(5f);

			GL11.glTranslated(x, y, z);

			Tessellator t = Tessellator.getInstance();
			VertexBuffer v = t.getBuffer();
			v.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			v.pos(0, 0, 0);
			v.color(141, 81, 24, 255);
			v.endVertex();
			v.pos(offx, offy + 1.25, offz);
			v.color(141, 81, 24, 255);
			v.endVertex();
			t.draw();

			GL11.glTranslated(-x, -y, -z);

			GlStateManager.enableLighting();
			//GlStateManager.enableDepth();
			GlStateManager.enableTexture2D();

			GL11.glPopMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHarpoon entity) {
		return RES;
	}

}
