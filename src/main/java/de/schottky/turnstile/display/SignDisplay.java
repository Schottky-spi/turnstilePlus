package de.schottky.turnstile.display;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePlugin;
import de.schottky.turnstile.metadata.MetadataKeys;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class SignDisplay implements TurnstileInformationDisplay {

    public static final String METADATA_IDENTIFIER = MetadataKeys.create("sign_display");

    private Location signLocation;
    private final String[] formats;

    public SignDisplay(Block sign) {
        this.signLocation = sign.getLocation();
        Sign thisSign = getSign();
        if (thisSign == null) {
            throw new RuntimeException("Block is not a sign");
        }
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

    private transient WeakReference<Turnstile> turnstile = new WeakReference<>(null);

    @Override
    public void link(Turnstile toTurnstile) {
        turnstile = new WeakReference<>(toTurnstile);
        final Sign sign = this.getSign();
        if (sign == null) {
            this.unlink();
        } else {
            sign.setMetadata(METADATA_IDENTIFIER,
                    new FixedMetadataValue(TurnstilePlugin.instance(), this));
        }
    }

    @Override
    public Turnstile unlink() {
        final Turnstile theTurnstile = turnstile.get();
        if (theTurnstile == null) return null;
        theTurnstile.removeInformationDisplay(this);
        return theTurnstile;
    }

    @Override
    public void displayInformationAbout(Turnstile turnstile) {
        final Sign sign = getSign();
        if (sign == null) {
            this.unlink();
            return;
        }
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
