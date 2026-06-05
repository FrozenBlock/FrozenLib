package net.frozenblock.lib.entity.api;

import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class AbstractBlockLikeMob extends Mob {
	private static final EntityDataAccessor<Direction> DATA_CLIMBING_DIRECTION = SynchedEntityData.defineId(AbstractBlockLikeMob.class, EntityDataSerializers.DIRECTION);
	private static final double BLOCK_SNAP_THRESHOLD = 0.125D;
	private static final double COLLISION_NUDGE_OFFSET = 0.05D;
	private static final int ROLL_SOUND_MIN_TIME = 4;
	private static final double ROLL_SOUND_MIN_ANGLE = 10D;
	private static final float ROLL_ROTATION_DELTA_EPSILON = 1.0E-5F;
	private final Quaternionf lastRotation = new Quaternionf();
	private final Quaternionf rotation = new Quaternionf();
	private float rollDeltaX;
	private float rollDeltaZ;
	private int rollSoundTime;
	private boolean canDoStepEffects;
	private Vec3 boundingBoxOffset = Vec3.ZERO;
	private BlockState blockStateCache = this.defaultBlockState();

	public AbstractBlockLikeMob(EntityType<? extends Mob> type, Level level) {
		super(type, level);
		this.blocksBuilding = true;
	}

	@Override
	protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
		super.defineSynchedData(entityData);
		entityData.define(DATA_CLIMBING_DIRECTION, Direction.DOWN);
	}

	public abstract BlockState defaultBlockState();

	public BlockState getBlockState() {
		if (this.blockStateCache == null) this.blockStateCache = this.defaultBlockState();
		return this.blockStateCache;
	}

	public void setBlockState(BlockState blockState) {
		this.blockStateCache = blockState;
	}

	@Override
	protected AABB makeBoundingBox(final Vec3 position) {
		makeBoundingBoxFromBlockState: {
			final BlockState blockState = this.getBlockState();
			if (blockState.isAir()) break makeBoundingBoxFromBlockState;

			final VoxelShape shape = blockState.getShape(this.level(), BlockPos.containing(position), CollisionContext.empty());
			if (shape.isEmpty()) break makeBoundingBoxFromBlockState;

			final float scale = this.getScale();
			final AABB bounds = shape.bounds();
			final AABB scaledBounds = new AABB(
				bounds.minX * scale,
				bounds.minY * scale,
				bounds.minZ * scale,
				bounds.maxX * scale,
				bounds.maxY * scale,
				bounds.maxZ * scale
			);

			this.boundingBoxOffset = scaledBounds.getCenter().reverse();
			return scaledBounds.move(position.subtract(scaledBounds.getBottomCenter()));
		}

		return super.makeBoundingBox(position);
	}

	public Vec3 getBoundingBoxOffset() {
		return this.boundingBoxOffset;
	}

	public Direction getClimbingDirection() {
		return this.entityData.get(DATA_CLIMBING_DIRECTION);
	}

	public void setClimbingDirection(Direction direction) {
		if (!this.level().isClientSide()) this.entityData.set(DATA_CLIMBING_DIRECTION, direction);
	}

	public void resetClimbingDirection() {
		this.setClimbingDirection(Direction.DOWN);
	}

	public boolean isClimbing() {
		return this.getClimbingDirection().getAxis() != Direction.Axis.Y;
	}

	@Override
	public void aiStep() {
		applyMovementRotation(this.rollDeltaX, this.rollDeltaZ, this.rotation);
		if (this.rollSoundTime-- <= 0 && !this.lastRotation.equals(this.rotation, ROLL_ROTATION_DELTA_EPSILON)) {
			final Quaternionf groundAngle = AdvancedMath.snapToNearestRightAngle(this.rotation);
			final Quaternionf lastToGround = new Quaternionf(this.lastRotation);
			lastToGround.conjugate();
			lastToGround.mul(groundAngle);

			final double lastAngleToZero = Math.toDegrees(lastToGround.angle());
			final double lastDistanceToZero = Math.min(lastAngleToZero, 360D - lastAngleToZero);
			final Quaternionf currentToGround = new Quaternionf(this.rotation);
			currentToGround.conjugate();
			currentToGround.mul(groundAngle);

			final double currentAngleToZero = Math.toDegrees(currentToGround.angle()) % 360D;
			final double currentDistanceToZero = Math.min(currentAngleToZero, 360D - currentAngleToZero);
			if (lastDistanceToZero > ROLL_SOUND_MIN_ANGLE && currentDistanceToZero <= ROLL_SOUND_MIN_ANGLE) {
				this.rollSoundTime = ROLL_SOUND_MIN_TIME;
				this.canDoStepEffects = true;
			}
		}

		this.lastRotation.set(this.rotation);
		this.rollDeltaX = 0F;
		this.rollDeltaZ = 0F;

		super.aiStep();

		final boolean isClimbing = this.isClimbing();
		if (this.onGround() && this.getDeltaMovement().horizontalDistanceSqr() < Mth.square(0.001D) || isClimbing && Math.abs(this.getDeltaMovement().y) < 0.001D) {
			final Vec3 blockGridDelta = Vec3.atBottomCenterOf(this.blockPosition()).subtract(this.position()).horizontal();
			final double blockGridOffset = blockGridDelta.length();
			final double rotationAlpha = Mth.clamp(blockGridOffset * 64D, COLLISION_NUDGE_OFFSET, 1D);
			this.rotation.slerp(AdvancedMath.snapToNearestRightAngle(this.rotation), (float)rotationAlpha);
			if (blockGridOffset > ROLL_ROTATION_DELTA_EPSILON && blockGridOffset <= BLOCK_SNAP_THRESHOLD && !this.level().isClientSide()) {
				this.move(MoverType.SELF, blockGridDelta);
			}
		}

		final Vector3f facing = new Vector3f();
		this.rotation.getEulerAnglesYXZ(facing);
		if (facing.isFinite()) {
			this.setRot(facing.y * (180F / (float)Math.PI), facing.x * (180F / (float)Math.PI));
		} else {
			this.setRot(0F, 0F);
			this.rotation.identity();
		}

		final AABB bounds = this.getBoundingBox();
		final double sizeY = bounds.getYsize();
		if (sizeY > ROLL_ROTATION_DELTA_EPSILON) {
			final Vector3f localYaxis = Mth.Y_AXIS.rotate(this.lastRotation, new Vector3f());
			float tilt = Math.abs(localYaxis.y);
			final double sizeX = bounds.getXsize();
			final double sizeZ = bounds.getZsize();
			final float rotationSpeed = !isClimbing && !this.onGround() ? 0.5F : 1F;
			if (sizeZ > ROLL_ROTATION_DELTA_EPSILON) {
				final double sideLength = sizeZ < sizeY
					? Mth.lerp(tilt, Math.sqrt(sizeZ / sizeY) * sizeY, sizeY)
					: Mth.lerp(tilt, sizeZ, Math.sqrt(sizeY / sizeZ) * sizeZ);
				this.rollDeltaX = this.rollDeltaX + (float)((this.getZ() - this.zo) * rotationSpeed / sideLength);
			}

			if (sizeX > ROLL_ROTATION_DELTA_EPSILON) {
				final double sideLength = sizeX < sizeY
					? Mth.lerp(tilt, Math.sqrt(sizeX / sizeY) * sizeY, sizeY)
					: Mth.lerp(tilt, sizeX, Math.sqrt(sizeY / sizeX) * sizeX);
				this.rollDeltaZ = this.rollDeltaZ + (float)((this.getX() - this.xo) * rotationSpeed / sideLength);
			}

			if (isClimbing) {
				final Direction groundDirection = this.getClimbingDirection();
				final boolean onXAxis = groundDirection.getAxis() == Direction.Axis.X;
				final double size = onXAxis ? sizeZ : sizeX;
				final double sideLength = size < sizeY
					? Mth.lerp(tilt, sizeY, Math.sqrt(size / sizeY) * sizeY)
					: Mth.lerp(tilt, Math.sqrt(sizeY / size) * size, size);
				final int sign = groundDirection.getAxisDirection().getStep();
				final float roll = (float)((this.getY() - this.yo) * rotationSpeed / sideLength * sign);
				if (onXAxis) {
					this.rollDeltaZ += roll;
				} else {
					this.rollDeltaX += roll;
				}
			}
		}
	}

	private static void applyMovementRotation(float dx, float dz, Quaternionf dest) {
		if (Math.abs(dx) > ROLL_ROTATION_DELTA_EPSILON) dest.rotateLocalX(dx * Mth.HALF_PI);
		if (Math.abs(dz) > ROLL_ROTATION_DELTA_EPSILON) dest.rotateLocalZ(dz * Mth.HALF_PI);
	}

	public void getRotation(Quaternionf dest, float partialTicks) {
		this.lastRotation.slerp(this.rotation, partialTicks, dest);
		applyMovementRotation(this.rollDeltaX * partialTicks, this.rollDeltaZ * partialTicks, dest);
	}

	@Override
	protected float nextStep() {
		this.setCanDoStepEffects(false);
		return 0F;
	}

	public boolean canDoStepEffects() {
		return this.canDoStepEffects;
	}

	public void setCanDoStepEffects(boolean canDoStepEffects) {
		this.canDoStepEffects = canDoStepEffects;
	}

	public void spawnBreakParticles(int particleCount) {
		if (!(this.level() instanceof ServerLevel level)) return;
		level.sendParticles(
			new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockState()),
			this.getX(),
			this.getY(0.6666666666666666D),
			this.getZ(),
			particleCount,
			this.getBbWidth() / 4F,
			this.getBbHeight() / 4F,
			this.getBbWidth() / 4F,
			0.05D
		);
	}
}
