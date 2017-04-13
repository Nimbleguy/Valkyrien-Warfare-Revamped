package ValkyrienWarfareCombat.Network;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;

import ValkyrienWarfareCombat.Entity.EntityHarpoon;
import ValkyrienWarfareCombat.Entity.EntityHarpoonGun;

public class PacketHarpoon implements IMessage, IMessageHandler<PacketHarpoon, IMessage>{
	private int gid;
	private int hid;

	public PacketHarpoon() {}

	public PacketHarpoon(int gun, int harpoon){
		gid = gun;
		hid = harpoon;
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(gid);
		buf.writeInt(hid);
	}

	@Override
	public void fromBytes(ByteBuf buf){
		gid = buf.readInt();
		hid = buf.readInt();
	}

	@Override
	public IMessage onMessage(PacketHarpoon p, MessageContext c){
		Minecraft.getMinecraft().addScheduledTask(new Execute(p.gid, p.hid));
		return null;
	}

	private class Execute extends Thread{
		int gid;
		int hid;

		public Execute(int g, int h){
			gid = g;
			hid = h;
		}

		@Override
		public void run(){
			EntityHarpoon h = (EntityHarpoon)Minecraft.getMinecraft().theWorld.getEntityByID(hid);
			h.origin = (EntityHarpoonGun)Minecraft.getMinecraft().theWorld.getEntityByID(gid);
			h.origin.harpoon = h;
		}
	}
}
