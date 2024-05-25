package thunder.hack.utility.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import static thunder.hack.core.IManager.mc;
// pasted and adapted by sl1eko!
// source getted by qunix!
public class MiningData {
        private final BlockPos pos;
        private final Direction direction;
        private float blockDamage;
        private boolean instantRemine;
        private boolean started;

        public MiningData(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
        }

        public boolean isInstantRemine() {
        return this.instantRemine;
        }

        public void setInstantRemine() {
        this.instantRemine = true;
        }

        public float damage(float dmg) {
        this.blockDamage += dmg;
        return this.blockDamage;
        }

        public void setDamage(float blockDamage) {
        this.blockDamage = blockDamage;
        }

        public void resetDamage() {
        this.instantRemine = false;
        this.blockDamage = 0.0F;
        }

        public BlockPos getPos() {
        return this.pos;
        }

        public Direction getDirection() {
        return this.direction;
        }



        public BlockState getState() {
        return mc.world.getBlockState(this.pos);
        }

        public boolean isStarted() {
        return this.started;
        }

        public void setStarted() {
        this.started = true;
        }

        public float getBlockDamage() {
        return this.blockDamage;
        }
        }

