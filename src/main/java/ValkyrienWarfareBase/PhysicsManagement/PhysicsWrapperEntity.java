package ValkyrienWarfareBase.PhysicsManagement;

import javax.annotation.Nullable;

import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.Collision.Polygon;
import ValkyrienWarfareBase.Interaction.ShipNameUUIDData;
import ValkyrienWarfareCombat.Entity.EntityMountingWeaponBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This entity's only purpose is to use the functionality of sending itself to nearby players, all other operations are handled by the PhysicsObject class
 *
 * @author thebest108
 *
 */
public class PhysicsWrapperEntity extends Entity implements IEntityAdditionalSpawnData {

	public PhysicsObject wrapping;
	public double pitch;
	public double yaw;
	public double roll;

	public double prevPitch;
	public double prevYaw;
	public double prevRoll;
	
	public static final DataParameter<Boolean> IS_NAME_CUSTOM = EntityDataManager.<Boolean>createKey(Entity.class, DataSerializers.BOOLEAN);

	private static final AxisAlignedBB zeroBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	public PhysicsWrapperEntity(World worldIn) {
		super(worldIn);
		wrapping = new PhysicsObject(this);
		dataManager.register(IS_NAME_CUSTOM, Boolean.valueOf(false));
	}

	public PhysicsWrapperEntity(World worldIn, double x, double y, double z, @Nullable EntityPlayer maker, int detectorID) {
		this(worldIn);
		posX = x;
		posY = y;
		posZ = z;
		wrapping.creator = maker.entityUniqueID.toString();
		wrapping.detectorID = detectorID;
		wrapping.processChunkClaims(maker);
	}

	@Override
	public void onUpdate() {
		if (isDead) {
			return;
		}
		if(worldObj.isRemote){
			wrapping.isNameCustom = dataManager.get(IS_NAME_CUSTOM);
		}
		// super.onUpdate();
		wrapping.onTick();
	}

	@Override
	public void updatePassenger(Entity passenger) {
		Vector inLocal = wrapping.getLocalPositionForEntity(passenger);

		if (inLocal != null) {
			Vector newEntityPosition = new Vector(inLocal);
			float f = passenger.width / 2.0F;
	        float f1 = passenger.height;
	        AxisAlignedBB inLocalAABB = new AxisAlignedBB(newEntityPosition.X - (double)f, newEntityPosition.Y, newEntityPosition.Z - (double)f, newEntityPosition.X + (double)f, newEntityPosition.Y + (double)f1, newEntityPosition.Z + (double)f);
	        wrapping.coordTransform.fromLocalToGlobal(newEntityPosition);
			passenger.setPosition(newEntityPosition.X, newEntityPosition.Y, newEntityPosition.Z);
			Polygon entityBBPoly = new Polygon(inLocalAABB, wrapping.coordTransform.lToWTransform);

			AxisAlignedBB newEntityBB = entityBBPoly.getEnclosedAABB();
			passenger.setEntityBoundingBox(newEntityBB);
			if(passenger instanceof EntityMountingWeaponBase){
				passenger.onUpdate();

				for(Entity e:passenger.riddenByEntities){
					if(wrapping.isEntityFixed(e)){
						Vector inLocalAgain = wrapping.getLocalPositionForEntity(e);
						if (inLocalAgain != null) {
							Vector newEntityPositionAgain = new Vector(inLocalAgain);
							wrapping.coordTransform.fromLocalToGlobal(newEntityPositionAgain);

							e.setPosition(newEntityPositionAgain.X, newEntityPositionAgain.Y, newEntityPositionAgain.Z);
						}
					}
				}
			}
		}
	}

	@Override
	protected void addPassenger(Entity passenger) {
		// System.out.println("entity just mounted");
		super.addPassenger(passenger);
	}

	@Override
	protected void removePassenger(Entity toRemove) {
		// System.out.println("entity just dismounted");
		super.removePassenger(toRemove);
		if (!worldObj.isRemote) {
			wrapping.unFixEntity(toRemove);
			if(wrapping.pilotingController.getPilotEntity() == toRemove){
				wrapping.pilotingController.setPilotEntity(null, false);
			}
		} else {
			// It doesnt matter if I dont remove these terms from client, and things are problematic if I do. Best to leave this commented
			// wrapping.removeEntityUUID(toRemove.getPersistentID().hashCode());
		}
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		return wrapping.collisionBB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return wrapping.collisionBB;
	}

	@Override
	public void setPosition(double x, double y, double z) {
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
	}

	@Override
	protected boolean canFitPassenger(Entity passenger){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
	}

	@SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender(){
        return wrapping.isNameCustom;
    }

	public void setCustomNameTagInitial(String name){
		super.setCustomNameTag(name);
	}

	@Override
	public void setCustomNameTag(String name){
		if(!worldObj.isRemote){
			if(getCustomNameTag() != null && !getCustomNameTag().equals("")){
				//Update the name registry
				boolean didRenameSuccessful = ShipNameUUIDData.get(worldObj).renameShipInRegsitry(this, name, getCustomNameTag());
				if(didRenameSuccessful){
					super.setCustomNameTag(name);
					wrapping.isNameCustom = true;
					dataManager.set(IS_NAME_CUSTOM, true);
				}
			}else{
				super.setCustomNameTag(name);
			}
		}else{
			super.setCustomNameTag(name);
		}
    }

	@Override
	public void setPositionAndUpdate(double x, double y, double z) {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompound) {
		wrapping.readFromNBTTag(tagCompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
		wrapping.writeToNBTTag(tagCompound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		wrapping.preloadNewPlayers();
		wrapping.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		wrapping.readSpawnData(additionalData);
	}
}
