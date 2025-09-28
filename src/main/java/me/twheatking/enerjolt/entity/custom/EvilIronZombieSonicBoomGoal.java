package me.twheatking.enerjolt.entity.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EvilIronZombieSonicBoomGoal extends Goal {
    private final EvilIronZombieEntity zombie;
    private int spellWarmupTime;
    private int spellCooldown;
    private int consecutiveAttackTime;
    private boolean hasUsedAttack;

    public EvilIronZombieSonicBoomGoal(EvilIronZombieEntity zombie) {
        this.zombie = zombie;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.zombie.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.zombie.isCastingSpell()) {
            return false;
        }

        if (this.spellCooldown > 0) {
            --this.spellCooldown;
            return false;
        }

        if (!this.zombie.isSpellReady()) {
            return false;
        }

        // Only use sonic boom if target is far away or behind cover
        double distance = this.zombie.distanceToSqr(target);
        boolean hasLineOfSight = this.zombie.getSensing().hasLineOfSight(target);

        // Conditions for sonic boom:
        // 1. Target is far (beyond 10 blocks) OR
        // 2. Target is behind cover (no line of sight but recently seen) OR
        // 3. Haven't been able to reach target for a while
        return (distance > 100.0D || !hasLineOfSight || this.consecutiveAttackTime > 200)
                && distance <= 400.0D; // Max 20 blocks
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.zombie.getTarget();
        return target != null && target.isAlive() && this.spellWarmupTime > 0;
    }

    @Override
    public void start() {
        this.spellWarmupTime = this.getCastWarmupTime();
        this.zombie.startSpellCasting(EvilIronZombieEntity.SPELL_SONIC_BOOM, this.spellWarmupTime);
        this.zombie.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 1.0F, 1.0F);
        this.hasUsedAttack = false;
        this.consecutiveAttackTime = 0;
    }

    @Override
    public void stop() {
        this.spellWarmupTime = 0;
        this.spellCooldown = this.getCastingCooldown();
        if (this.zombie.getSpellType() == EvilIronZombieEntity.SPELL_SONIC_BOOM) {
            this.zombie.finishSpellCasting();
        }

        if (this.zombie.sonicBoomAnimationState.isStarted()) {
            this.zombie.sonicBoomAnimationState.stop();
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.zombie.getTarget();

        if (target == null) {
            return;
        }

        // Track if we can't reach target
        if (this.zombie.getNavigation().isDone()) {
            this.consecutiveAttackTime++;
        } else {
            this.consecutiveAttackTime = Math.max(0, this.consecutiveAttackTime - 1);
        }

        --this.spellWarmupTime;

        // Always look at target during casting
        this.zombie.getLookControl().setLookAt(target,
                (float) this.zombie.getMaxHeadYRot(),
                (float) this.zombie.getMaxHeadXRot());

        // Stop moving while casting
        this.zombie.getNavigation().stop();

        // Start sonic boom animation at halfway point
        if (this.spellWarmupTime == this.getCastWarmupTime() / 2 &&
                !this.zombie.sonicBoomAnimationState.isStarted()) {
            this.zombie.sonicBoomAnimationState.start(this.zombie.tickCount);
        }

        // Execute the attack
        if (this.spellWarmupTime == 0 && !this.hasUsedAttack) {
            this.performSpellCasting();
            this.hasUsedAttack = true;
        }
    }

    protected void performSpellCasting() {
        LivingEntity target = this.zombie.getTarget();
        if (target != null) {
            // Check if target is still within range
            double distance = this.zombie.distanceTo(target);
            if (distance <= 20.0D) {
                this.zombie.castSonicBoomSpell(target);
            }
        }
    }

    protected int getCastWarmupTime() {
        return 34; // 1.7 seconds like warden
    }

    protected int getCastingCooldown() {
        return 200 + this.zombie.getRandom().nextInt(100); // 10-15 seconds (longer than fangs)
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}