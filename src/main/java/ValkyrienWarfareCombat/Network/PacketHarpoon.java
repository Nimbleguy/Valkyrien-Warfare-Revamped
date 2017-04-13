package ValkyrienWarfareCombat.Network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;

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
		EntityHarpoon h = (EntityHarpoon)Minecraft.getMinecraft().theWorld.getEntityByID(hid);
		h.shootingEntity = Minecraft.getMinecraft().theWorld.getEntityByID(gid);
	}
}
