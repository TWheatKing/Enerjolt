package me.twheatking.enerjolt.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EvilIronZombieEvokerFang extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_CLIENT_ID =
            SynchedEntityData.defineId(EvilIronZombieEvokerFang.class, EntityDataSerializers.INT);

    private LivingEntity owner;
    private int warmupDelayTicks = 0;
    private boolean sentSpikeEvent = false;
    private int lifeTicks = 22;
    private boolean clientSideAttackStarted = false;

    public EvilIronZombieEvokerFang(EntityType<? extends EvilIronZombieEvokerFang> entityType, Level level) {
        super(entityType, level);
    }

    public EvilIronZombieEvokerFang(Level level, double x, double y, double z, float yRot, int warmupDelay, LivingEntity owner) {
        this(null, level); // You'll need to register this entity type
        this.warmupDelayTicks = warmupDelay;
        this.setOwner(owner);
        this.setYRot(yRot * (180F / (float) Math.PI));
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_CLIENT_ID, 0);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.entityData.set(DATA_CLIENT_ID, owner == null ? 0 : owner.getId());
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.entityData.get(DATA_CLIENT_ID) != 0 && this.level().isClientSide) {
            Entity entity = this.level().getEntity(this.entityData.get(DATA_CLIENT_ID));
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        this.lifeTicks = compound.getInt("LifeTicks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        compound.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * 0.7853981633974483D;
                        double d1 = this.getY() + 0.05D + this.random.nextDouble();
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * 0.7853981633974483D;
                        this.level().addParticle(ParticleTypes.CRIT, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                for (LivingEntity livingentity : this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {

                    this.dealDamageTo(livingentity);
                }
            }

            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    private void dealDamageTo(LivingEntity target) {
        LivingEntity owner = this.getOwner();
        if (target.isAlive() && !target.isInvulnerable() && target != owner) {
            if (owner == null) {
                target.hurt(this.damageSources().magic(), 6.0F);
            } else {
                if (owner.isAlliedTo(target)) {
                    return;
                }
                target.hurt(this.damageSources().indirectMagic(this, owner), 6.0F);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK,
                        this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }
    }

    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        } else {
            int i = this.lifeTicks - 2;
            return i <= 0 ? 1.0F : 1.0F - ((float) i - partialTicks) / 20.0F;
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    public boolean canChangeDimensions() {
        return false;
    }
}