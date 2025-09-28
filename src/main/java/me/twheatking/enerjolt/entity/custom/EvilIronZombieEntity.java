package me.twheatking.enerjolt.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EvilIronZombieEntity extends Zombie {
    // Data accessors for spell states
    private static final EntityDataAccessor<Boolean> DATA_IS_CASTING_SPELL =
            SynchedEntityData.defineId(EvilIronZombieEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SPELL_TYPE =
            SynchedEntityData.defineId(EvilIronZombieEntity.class, EntityDataSerializers.INT);

    // Animation states
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState castingAnimationState = new AnimationState();
    public final AnimationState sonicBoomAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int spellCastingTickCount = 0;
    private int spellCooldownTicks = 0;

    // Spell types
    public static final int SPELL_NONE = 0;
    public static final int SPELL_FANGS = 1;
    public static final int SPELL_SONIC_BOOM = 2;

    public EvilIronZombieEntity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_CASTING_SPELL, false);
        builder.define(DATA_SPELL_TYPE, SPELL_NONE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EvilIronZombieSpellcasterGoal(this));
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new EvilIronZombieFangsGoal(this));
        this.goalSelector.addGoal(4, new EvilIronZombieSonicBoomGoal(this));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            setupAnimationStates();
        }

        if (spellCooldownTicks > 0) {
            spellCooldownTicks--;
        }

        if (this.isCastingSpell()) {
            spellCastingTickCount++;
        } else {
            spellCastingTickCount = 0;
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isCastingSpell() && !this.castingAnimationState.isStarted()) {
            this.castingAnimationState.start(this.tickCount);
        } else if (!this.isCastingSpell()) {
            this.castingAnimationState.stop();
        }
    }

    // Spell management methods
    public boolean isCastingSpell() {
        return this.entityData.get(DATA_IS_CASTING_SPELL);
    }

    public void setIsCastingSpell(boolean casting) {
        this.entityData.set(DATA_IS_CASTING_SPELL, casting);
    }

    public int getSpellType() {
        return this.entityData.get(DATA_SPELL_TYPE);
    }

    public void setSpellType(int spellType) {
        this.entityData.set(DATA_SPELL_TYPE, spellType);
    }

    public boolean isSpellReady() {
        return spellCooldownTicks <= 0 && !this.isCastingSpell();
    }

    public void startSpellCasting(int spellType, int duration) {
        this.setIsCastingSpell(true);
        this.setSpellType(spellType);
        this.spellCastingTickCount = 0;
        this.spellCooldownTicks = duration + 60; // Add cooldown after spell
    }

    public void finishSpellCasting() {
        this.setIsCastingSpell(false);
        this.setSpellType(SPELL_NONE);
        this.spellCastingTickCount = 0;
    }

    public int getSpellCastingTime() {
        return spellCastingTickCount;
    }

    // Spell casting methods
    public void castFangsSpell(LivingEntity target) {
        if (!this.level().isClientSide && target != null) {
            double targetX = target.getX();
            double targetZ = target.getZ();
            double dx = targetX - this.getX();
            double dz = targetZ - this.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0 && distance <= 16) {
                // Create fangs in a line towards the target
                createFangsLine(targetX, targetZ, distance);
            }
        }
    }

    private void createFangsLine(double targetX, double targetZ, double distance) {
        double dx = (targetX - this.getX()) / distance;
        double dz = (targetZ - this.getZ()) / distance;

        for (int i = 1; i <= (int)Math.min(distance, 8); i++) {
            double x = this.getX() + dx * i;
            double z = this.getZ() + dz * i;
            double y = this.getY();

            // Find the ground level
            BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
            while (y > this.level().getMinBuildHeight() && this.level().getBlockState(pos).isAir()) {
                y--;
                pos = new BlockPos((int)x, (int)y, (int)z);
            }
            y++; // Place fang one block above the ground

            if (this.level() instanceof ServerLevel serverLevel) {
                // Create the custom evoker fang
                // Note: You'll need to register this entity type in your mod's entity registry
                // EvilIronZombieEvokerFang fang = new EvilIronZombieEvokerFang(serverLevel, x, y, z, 0.0F, i * 2, this);
                // serverLevel.addFreshEntity(fang);

                // For now, we'll use particles and direct damage as before
                serverLevel.sendParticles(ParticleTypes.CRIT, x, y + 0.1, z, 5, 0.1, 0.1, 0.1, 0.1);
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y + 0.1, z, 3, 0.2, 0.1, 0.2, 0.0);

                // Create final copies for lambda usage
                final double finalX = x;
                final double finalY = y;
                final double finalZ = z;

                // Schedule damage after a delay to match evoker fang timing
                serverLevel.getServer().tell(new net.minecraft.server.TickTask(serverLevel.getServer().getTickCount() + i * 2 + 8, () -> {
                    // Deal damage to entities in the area
                    this.level().getEntitiesOfClass(LivingEntity.class,
                                    new net.minecraft.world.phys.AABB(finalX - 0.5, finalY, finalZ - 0.5, finalX + 0.5, finalY + 2, finalZ + 0.5))
                            .forEach(entity -> {
                                if (entity != this && !this.isAlliedTo(entity)) {
                                    entity.hurt(this.damageSources().indirectMagic(this, this), 8.0F);
                                    // Play attack sound
                                    this.level().playSound(null, entity.blockPosition(),
                                            SoundEvents.EVOKER_FANGS_ATTACK,
                                            this.getSoundSource(), 1.0F, 1.0F);
                                }
                            });
                }));
            }
        }
    }


    public void castSonicBoomSpell(LivingEntity target) {
        if (!this.level().isClientSide && target != null) {
            // Play sound
            this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);

            // Create particles
            if (this.level() instanceof ServerLevel serverLevel) {
                Vec3 direction = target.position().subtract(this.position()).normalize();

                for (int i = 0; i < 20; i++) {
                    double x = this.getX() + direction.x * i;
                    double y = this.getY() + 1.0 + direction.y * i;
                    double z = this.getZ() + direction.z * i;

                    serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, x, y, z, 1, 0, 0, 0, 0);
                }

                // Deal damage (bypasses armor like warden's attack)
                double distance = this.distanceTo(target);
                if (distance <= 20.0) {
                    target.hurt(this.damageSources().sonicBoom(this), 12.0F);
                }
            }
        }
    }

    // Sound methods
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    // Attributes
    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()  // Use Zombie.createAttributes() instead of Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)  // Reduced from 2.0 to be more balanced
                .add(Attributes.FOLLOW_RANGE, 15.0)
                .add(Attributes.MAX_HEALTH, 280.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ARMOR, 10.0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsCastingSpell", this.isCastingSpell());
        compound.putInt("SpellType", this.getSpellType());
        compound.putInt("SpellCooldown", this.spellCooldownTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setIsCastingSpell(compound.getBoolean("IsCastingSpell"));
        this.setSpellType(compound.getInt("SpellType"));
        this.spellCooldownTicks = compound.getInt("SpellCooldown");
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity == this) {
            return true;
        } else if (super.isAlliedTo(entity)) {
            return true;
        } else if (entity instanceof EvilIronZombieEntity ||
                entity instanceof net.minecraft.world.entity.monster.Zombie ||
                entity instanceof net.minecraft.world.entity.monster.Monster) {
            return this.getTeam() == null && entity.getTeam() == null;
        } else {
            return false;
        }
    }

    // Inner goal classes will be defined in separate files
}