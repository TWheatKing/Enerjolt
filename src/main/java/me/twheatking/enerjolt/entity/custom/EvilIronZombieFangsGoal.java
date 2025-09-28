package me.twheatking.enerjolt.entity.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EvilIronZombieFangsGoal extends Goal {
    private final EvilIronZombieEntity zombie;
    private int spellWarmupTime;
    private int spellCooldown;

    public EvilIronZombieFangsGoal(EvilIronZombieEntity zombie) {
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

        double distance = this.zombie.distanceToSqr(target);
        return distance <= 256.0D && distance >= 9.0D && this.zombie.getSensing().hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.zombie.getTarget();
        return target != null && target.isAlive() && this.spellWarmupTime > 0;
    }

    @Override
    public void start() {
        this.spellWarmupTime = this.getCastWarmupTime();
        this.zombie.startSpellCasting(EvilIronZombieEntity.SPELL_FANGS, this.spellWarmupTime);
        this.zombie.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
    }

    @Override
    public void stop() {
        this.spellWarmupTime = 0;
        this.spellCooldown = this.getCastingCooldown();
        if (this.zombie.getSpellType() == EvilIronZombieEntity.SPELL_FANGS) {
            this.zombie.finishSpellCasting();
        }
    }

    @Override
    public void tick() {
        --this.spellWarmupTime;

        LivingEntity target = this.zombie.getTarget();
        if (target != null) {
            this.zombie.getLookControl().setLookAt(target,
                    (float) this.zombie.getMaxHeadYRot(),
                    (float) this.zombie.getMaxHeadXRot());
        }

        if (this.spellWarmupTime == 0 && target != null) {
            this.performSpellCasting();
        }
    }

    protected void performSpellCasting() {
        LivingEntity target = this.zombie.getTarget();
        if (target != null) {
            this.zombie.castFangsSpell(target);
            this.zombie.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
        }
    }

    protected int getCastWarmupTime() {
        return 40; // 2 seconds
    }

    protected int getCastingCooldown() {
        return 100 + this.zombie.getRandom().nextInt(60); // 5-8 seconds
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}