package ValkyrienWarfareCombat.Network;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;

import ValkyrienWarfareCombat.Entity.EntityHarpoonGun;

public class PacketHarpoonReset implements IMessage, IMessageHandler<PacketHarpoonReset, IMessage>{
	private int gid;

	public PacketHarpoonReset() {}

	public PacketHarpoonReset(int gun){
		gid = gun;
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(gid);
	}

	@Override
	public void fromBytes(ByteBuf buf){
		gid = buf.readInt();
	}

	@Override
	public IMessage onMessage(PacketHarpoonReset p, MessageContext c){
		Minecraft.getMinecraft().addScheduledTask(new Execute(p.gid));
		return null;
	}

	private class Execute extends Thread{
		int gid;

		public Execute(int g){
			gid = g;
		}

		@Override
		public void run(){
			EntityHarpoonGun g = (EntityHarpoonGun)Minecraft.getMinecraft().theWorld.getEntityByID(gid);
			g.harpoon = null;
		}
	}
}
