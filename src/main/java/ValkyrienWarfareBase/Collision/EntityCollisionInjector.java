package ValkyrienWarfareBase.Collision;

import java.util.ArrayList;
import java.util.List;

import ValkyrienWarfareBase.ValkyrienWarfareMod;
import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.Interaction.EntityDraggable;
import ValkyrienWarfareBase.Math.BigBastardMath;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareBase.PhysicsManagement.WorldPhysObjectManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

public class EntityCollisionInjector {

	private static final double errorSignificance = .001D;

	// Returns false if game should use default collision
	public static boolean alterEntityMovement(Entity entity, double dx, double dy, double dz) {
		if (entity instanceof PhysicsWrapperEntity) {
			return true;
		}

		Vector velVec = new Vector(dx, dy, dz);
		double origDx = dx;
		double origDy = dy;
		double origDz = dz;
		boolean isLiving = entity instanceof EntityLivingBase;
		boolean isMoving = false;
		if (isLiving) {
			EntityLivingBase living = (EntityLivingBase) entity;
			isMoving = Math.abs(living.moveForward) > .01 || Math.abs(living.moveStrafing) > .01;
		}
		Vec3d velocity = new Vec3d(dx, dy, dz);
		ArrayList<EntityPolygonCollider> fastCollisions = new ArrayList<EntityPolygonCollider>();
		EntityPolygon playerBeforeMove = new EntityPolygon(entity.getEntityBoundingBox(), entity);
		ArrayList<Polygon> colPolys = getCollidingPolygonsAndDoBlockCols(entity, velocity);

		PhysicsWrapperEntity worldBelow = null;
		EntityDraggable draggable = EntityDraggable.getDraggableFromEntity(entity);

		for (Polygon poly : colPolys) {
			if (poly instanceof ShipPolygon) {
				ShipPolygon shipPoly = (ShipPolygon) poly;
				EntityPolygonCollider fast = new EntityPolygonCollider(playerBeforeMove, shipPoly, shipPoly.normals, velVec);
				if (!fast.seperated) {
					fastCollisions.add(fast);
					worldBelow = shipPoly.shipFrom.wrapper;
				}
			}
		}

		//TODO: Make this more comprehensive
		draggable.worldBelowFeet = worldBelow;

		if (fastCollisions.isEmpty()) {
			return false;
		}
		int contX = 0;
		int contY = 0;
		int contZ = 0;
		Vector total = new Vector();
		for (EntityPolygonCollider col : fastCollisions) {
			Vector response = col.collisions[col.minDistanceIndex].getResponse();
			// TODO: Add more potential yResponses
			double stepSquared = entity.stepHeight * entity.stepHeight;
			boolean isStep = isLiving && entity.onGround;
			if (response.Y > 0 && BigBastardMath.canStandOnNormal(col.potentialSeperatingAxes[col.minDistanceIndex])) {
				response = new Vector(0, -col.collisions[col.minDistanceIndex].penetrationDistance / col.potentialSeperatingAxes[col.minDistanceIndex].Y, 0);
			}
			if (isStep) {
				EntityLivingBase living = (EntityLivingBase) entity;
				if (Math.abs(living.moveForward) > .01 || Math.abs(living.moveStrafing) > .01) {
					for (int i = 3; i < 6; i++) {
						Vector tempResponse = col.collisions[i].getResponse();
						if (tempResponse.Y > 0 && BigBastardMath.canStandOnNormal(col.collisions[i].axis) && tempResponse.lengthSq() < stepSquared) {
							response = tempResponse;
						}
					}
				}
			}
			// total.add(response);
			if (Math.abs(response.X) > .01) {
				total.X += response.X;
				contX++;
			}
			if (Math.abs(response.Y) > .01) {
				total.Y += response.Y;
				contY++;
			}
			if (Math.abs(response.Z) > .01) {
				total.Z += response.Z;
				contZ++;
			}
		}
		if (contX != 0) {
			total.X /= contX;
		}
		if (contY != 0) {
			total.Y /= contY;
		}
		if (contZ != 0) {
			total.Z /= contZ;
		}

		dx += total.X;
		dy += total.Y;
		dz += total.Z;

		boolean alreadyOnGround = entity.onGround && (dy == origDy) && origDy < 0;
		Vector original = new Vector(origDx, origDy, origDz);
		Vector newMov = new Vector(dx - origDx, dy - origDy, dz - origDz);
		entity.isCollidedHorizontally = original.dot(newMov) < 0;
		entity.isCollidedVertically = isDifSignificant(dy, origDy);
		entity.onGround = entity.isCollidedVertically && origDy < 0 || alreadyOnGround;
		entity.isCollided = entity.isCollidedHorizontally || entity.isCollidedVertically;
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase base = (EntityLivingBase) entity;
			base.motionY = dy;
			if (base.isOnLadder()) {

				float f9 = 0.15F;
				base.motionX = MathHelper.clamp_double(base.motionX, -0.15000000596046448D, 0.15000000596046448D);
				base.motionZ = MathHelper.clamp_double(base.motionZ, -0.15000000596046448D, 0.15000000596046448D);
				base.fallDistance = 0.0F;

				if (base.motionY < -0.15D) {
					base.motionY = -0.15D;
				}

				boolean flag = base.isSneaking() && base instanceof EntityPlayer;

				if (flag && base.motionY < 0.0D) {
					base.motionY = 0.0D;
				}
			}
			entity.moveEntity(dx, base.motionY, dz);
		} else {
			entity.moveEntity(dx, dy, dz);
		}
		entity.isCollidedHorizontally = (motionInterfering(dx, origDx)) || (motionInterfering(dz, origDz));
		entity.isCollidedVertically = isDifSignificant(dy, origDy);
		entity.onGround = entity.isCollidedVertically && origDy < 0 || alreadyOnGround || entity.onGround;
		entity.isCollided = entity.isCollidedHorizontally || entity.isCollidedVertically;
		if (dx != origDx) {
			entity.motionX = dx;
		}
		if (dy != origDy) {
			if (!(entity.motionY > 0 && dy > 0)) {
				entity.motionY = 0;
			}
		}
		if (dz != origDz) {
			entity.motionZ = dz;
		}

		return true;
	}

	/*
	 * This method generates an arrayList of Polygons that the player is colliding with
	 */
	public static ArrayList<Polygon> getCollidingPolygonsAndDoBlockCols(Entity entity, Vec3d velocity) {
		ArrayList<Polygon> collisions = new ArrayList<Polygon>();
		AxisAlignedBB entityBB = entity.getEntityBoundingBox().addCoord(velocity.xCoord, velocity.yCoord, velocity.zCoord).expand(1, 1, 1);

		WorldPhysObjectManager localPhysManager = ValkyrienWarfareMod.physicsManager.getManagerForWorld(entity.worldObj);

		List<PhysicsWrapperEntity> ships = localPhysManager.getNearbyPhysObjects(entityBB);
		//If a player is riding a Ship, don't process any collision between that Ship and the Player
		for (PhysicsWrapperEntity wrapper : ships) {
			if(!entity.isRidingSameEntity(wrapper)){
				Polygon playerInLocal = new Polygon(entityBB, wrapper.wrapping.coordTransform.wToLTransform);
				AxisAlignedBB bb = playerInLocal.getEnclosedAABB();

				List<AxisAlignedBB> collidingBBs = entity.worldObj.getCollisionBoxes(bb);

				// TODO: Fix the performance of this!
				if (entity.worldObj.isRemote || entity instanceof EntityPlayer) {
					BigBastardMath.mergeAABBList(collidingBBs);
				}

				for (AxisAlignedBB inLocal : collidingBBs) {
					ShipPolygon poly = new ShipPolygon(inLocal, wrapper.wrapping.coordTransform.lToWTransform, wrapper.wrapping.coordTransform.normals, wrapper.wrapping);
					collisions.add(poly);
				}
			}
		}

		for(PhysicsWrapperEntity wrapper : ships){
			if(!entity.isRidingSameEntity(wrapper)){
				double posX = entity.posX;
				double posY = entity.posY;
				double posZ = entity.posZ;

				Vector entityPos = new Vector(posX, posY, posZ);
				RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.wToLTransform, entityPos);

				setEntityPositionAndUpdateBB(entity, entityPos.X, entityPos.Y, entityPos.Z);

				int entityChunkX = MathHelper.floor_double(entity.posX / 16.0D);
				int entityChunkZ = MathHelper.floor_double(entity.posZ / 16.0D);

				if(wrapper.wrapping.ownsChunk(entityChunkX, entityChunkZ)){
					Chunk chunkIn = wrapper.wrapping.claimedChunks[entityChunkX-wrapper.wrapping.claimedChunks[0][0].xPosition][entityChunkZ-wrapper.wrapping.claimedChunks[0][0].zPosition];

					int chunkYIndex = MathHelper.floor_double(entity.posY / 16.0D);

			        if (chunkYIndex < 0)
			        {
			        	chunkYIndex = 0;
			        }

			        if (chunkYIndex >= chunkIn.entityLists.length)
			        {
			        	chunkYIndex = chunkIn.entityLists.length - 1;
			        }

					chunkIn.entityLists[chunkYIndex].add(entity);
					entity.doBlockCollisions();
					chunkIn.entityLists[chunkYIndex].remove(entity);
				}

				setEntityPositionAndUpdateBB(entity, posX, posY, posZ);
			}
		}

		return collisions;
	}

    public static void setEntityPositionAndUpdateBB(Entity entity, double x, double y, double z){
    	entity.posX = x;
    	entity.posY = y;
    	entity.posZ = z;
        float f = entity.width / 2.0F;
        float f1 = entity.height;
        entity.boundingBox = new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f);
    }

	private static boolean isDifSignificant(double dif1, double d2) {
		if (Math.abs(dif1 - d2) < errorSignificance) {
			return false;
		} else {
			return true;
		}
	}

	private static boolean motionInterfering(double orig, double modded) {
		return Math.signum(orig) != Math.signum(modded);
	}

}
