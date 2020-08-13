package de.schottky.turnstile.math;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorToBlockFaceTest {

    @Test
    public void a_zero_vector_will_be_self() {
        assertEquals(BlockFace.SELF, VectorToBlockFace.convert(new Vector()));
    }

    @Test
    public void a_vector_from_its_block_face_will_return_itself() {
        for (BlockFace face: VectorToBlockFace.applicableFaces()) {
            assertEquals(face, VectorToBlockFace.convert(face.getDirection()));
        }
    }

}