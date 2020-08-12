package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;

@Cmd(name = "turnstile", maxArgs = 1)
public class TurnstileCommand extends CommandBase {

    public TurnstileCommand() {
        super();
        this.registerSubCommands(
                new TurnstileSetupCommand(this),
                new TurnstileActivateCommand(this));
    }

}
