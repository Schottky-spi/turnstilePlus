package de.schottky.turnstile.display;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.Arrays;
import java.util.Objects;

public class SignDisplay implements TurnstileInformationDisplay {

    private Location signLocation;
    private final String[] formats;

    public SignDisplay(Block sign) {
        if (!Tag.SIGNS.isTagged(sign.getType())) {
            throw new RuntimeException("Block is not a sign");
        }
        this.signLocation = sign.getLocation();
        Sign thisSign = Objects.requireNonNull(getSign());
        this.formats = Arrays.copyOf(thisSign.getLines(), 4);
    }

    @RequiredConstructor
    private SignDisplay() {
        this.formats = new String[4];
    }

    private Sign getSign() {
        final Block block = signLocation.getBlock();
        if (!Tag.SIGNS.isTagged(block.getType())) {
            return null;
        } else {
            return (Sign) block.getState();
        }
    }

    @Override
    public boolean hasBeenRemoved() {
        return getSign() == null;
    }

    @Override
    public void displayInformationAbout(Turnstile turnstile) {
        final Sign sign = getSign();
        if (sign == null) return;
        for (int i = 0; i < 4; i++) {
            String line = formats[i]
                    .replace("[name]", turnstile.name())
                    .replace("[price]", turnstile.price().toString());
            final String name = turnstile.owningPlayer().getName();
            line = line.replace("[owner]", name == null ? "No owner" : name);
            sign.setLine(i, line);
        }
        sign.update();

    }
}
