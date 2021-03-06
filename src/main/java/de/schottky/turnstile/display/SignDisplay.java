package de.schottky.turnstile.display;

import com.github.schottky.zener.localization.Localizable;
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
import java.util.Objects;

/**
 * A {@link TurnstileInformationDisplay} that is based on a sign
 */
public class SignDisplay implements TurnstileInformationDisplay, Localizable {

    public static final String METADATA_IDENTIFIER = MetadataKeys.create("sign_display");

    private Location signLocation;
    // what to display in the sign, can have a special meaning
    private final String[] formats;

    public SignDisplay(Block sign) {
        this.signLocation = sign.getLocation();
        if (!Tag.SIGNS.isTagged(sign.getType())) {
            throw new RuntimeException("Block is not a sign");
        }
        final Sign thisSign = (Sign) sign.getState();
        this.formats = Arrays.copyOf(thisSign.getLines(), 4);
    }

    @RequiredConstructor
    private SignDisplay() {
        this.formats = new String[4];
    }

    // returns the sign-state, or null if the sign
    // no longer exists
    private Sign getSign() {
        final Block block = signLocation.getBlock();
        if (!Tag.SIGNS.isTagged(block.getType()) || !block.hasMetadata(METADATA_IDENTIFIER)) {
            return null;
        } else {
            return (Sign) block.getState();
        }
    }

    // weak reference to the turnstile that this belongs to
    private transient WeakReference<Turnstile> turnstile = new WeakReference<>(null);

    @Override
    public boolean link(Turnstile toTurnstile) {
        final Block block = signLocation.getBlock();
        if (Tag.SIGNS.isTagged(block.getType())) {
            block.setMetadata(METADATA_IDENTIFIER,
                    new FixedMetadataValue(TurnstilePlugin.instance(), this));
            turnstile = new WeakReference<>(toTurnstile);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Turnstile linkedTurnstile() {
        return turnstile.get();
    }

    @Override
    public void destroy() {
        signLocation.getBlock().removeMetadata(METADATA_IDENTIFIER, TurnstilePlugin.instance());
    }

    @Override
    public void onTurnstileStateUpdate() {
        final Sign sign = getSign();
        if (sign == null) {
            if (this.linkedTurnstile() != null)
                this.linkedTurnstile().unlink(this);
            return;
        }
        final Turnstile theTurnstile = turnstile.get();
        if (theTurnstile == null) return;

        for (int i = 0; i < 4; i++) {
            String line = formats[i]
                    .replace("[name]", theTurnstile.name())
                    .replace("[price]", theTurnstile.price().toString());
            final String name = theTurnstile.owningPlayer().getName();
            line = line.replace("[owner]", name == null ? "No owner" : name);
            sign.setLine(i, line);
        }
        sign.update();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignDisplay that = (SignDisplay) o;
        return Objects.equals(signLocation, that.signLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signLocation);
    }

    @Override
    public String toString() {
        return "Sign-display";
    }

    @Override
    public String identifier() {
        return "ident.sign_display";
    }
}
