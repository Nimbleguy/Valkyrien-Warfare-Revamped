package ValkyrienWarfareCombat.Render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class HarpoonHelper{
	public static void renderRope(double x, double y, double z, double offx, double offy, double offz){
		GL11.glPushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		GlStateManager.glLineWidth(5f);

		GL11.glTranslated(x, y, z);

		Tessellator t = Tessellator.getInstance();
		VertexBuffer v = t.getBuffer();
		v.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		v.pos(0, 0, 0);
		v.color(141, 81, 24, 255);
		v.endVertex();
		v.pos(offx, offy, offz);
		v.color(141, 81, 24, 255);
		v.endVertex();
		t.draw();

		GL11.glTranslated(-x, -y, -z);

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();

		GL11.glPopMatrix();
	}
}
