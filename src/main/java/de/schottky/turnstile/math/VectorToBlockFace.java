package de.schottky.turnstile.math;

import com.google.common.collect.ImmutableSet;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Set;

public class VectorToBlockFace {

    private static final Set<BlockFace> faces = EnumSet.of(
            BlockFace.DOWN,
            BlockFace.UP,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST);

    public static Set<BlockFace> applicableFaces() {
        return ImmutableSet.copyOf(faces);
    }

    public static BlockFace convert(Vector vector) {
        BlockFace biggest = BlockFace.SELF;
        double highestCorrelation = 0;
        vector.normalize();
        for (BlockFace face: faces) {
            final double corr = face.getDirection().dot(vector);
            if (corr > highestCorrelation) {
                highestCorrelation = corr;
                biggest = face;
            }
        }
        return biggest;
    }
}
