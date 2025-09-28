package me.twheatking.enerjolt.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EvilIronZombieSpellcasterGoal extends Goal {
    protected final EvilIronZombieEntity zombie;
    protected int attackTime = -1;
    protected int seeTime;
    protected boolean strafingClockwise;
    protected boolean strafingBackwards;
    protected int strafingTime = -1;

    public EvilIronZombieSpellcasterGoal(EvilIronZombieEntity zombie) {
        this.zombie = zombie;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.zombie.getTarget();
        return target != null && target.isAlive() && this.zombie.canAttack(target);
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || (this.zombie.isCastingSpell() && this.zombie.getSpellCastingTime() < 100);
    }

    @Override
    public void start() {
        super.start();
        this.zombie.setAggressive(true);
        this.seeTime = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.zombie.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.zombie.finishSpellCasting();
    }

    @Override
    public void tick() {
        LivingEntity target = this.zombie.getTarget();
        if (target == null) return;

        double distanceSqr = this.zombie.distanceToSqr(target);
        boolean canSeeTarget = this.zombie.getSensing().hasLineOfSight(target);

        if (canSeeTarget) {
            this.seeTime++;
        } else {
            this.seeTime--;
        }

        // Look at target
        this.zombie.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // Movement logic
        if (distanceSqr > 256.0D) { // 16 blocks
            // Too far, move closer
            this.zombie.getNavigation().moveTo(target, 1.0D);
        } else if (distanceSqr < 36.0D) { // 6 blocks
            // Too close, back away
            this.zombie.getNavigation().moveTo(target.getX() - this.zombie.getX(),
                    target.getY() - this.zombie.getY(),
                    target.getZ() - this.zombie.getZ(),
                    -1.0D);
        } else {
            // Good distance, strafe occasionally
            if (--this.strafingTime <= 0) {
                if (this.zombie.getRandom().nextFloat() < 0.3F) {
                    this.strafingClockwise = !this.strafingClockwise;
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = this.zombie.getRandom().nextInt(20) + 20;
            }

            if (this.strafingTime > -1) {
                if (distanceSqr > 36.0D && distanceSqr < 144.0D) {
                    float speed = this.strafingBackwards ? -0.5F : 0.5F;
                    float strafe = this.strafingClockwise ? 0.5F : -0.5F;
                    this.zombie.getMoveControl().strafe(speed, strafe);
                    this.zombie.setYRot(this.zombie.getYRot() + (this.strafingClockwise ? 90F : -90F));
                } else {
                    this.zombie.getNavigation().moveTo(target, 1.0D);
                }
            }
        }

        // Attack logic
        if (--this.attackTime == 0) {
            if (!canSeeTarget) {
                return;
            }

            float f = (float) Math.sqrt(distanceSqr) / 4.0F;
            f = net.minecraft.util.Mth.clamp(f, 0.1F, 1.0F);
            this.attackTime = net.minecraft.util.Mth.floor(f * (20.0F + this.zombie.getRandom().nextFloat() * 20.0F));

            if (distanceSqr <= 64.0D) { // 8 blocks - melee range
                this.zombie.doHurtTarget(target);
            }
        } else if (this.attackTime < 0) {
            this.attackTime = 20;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}