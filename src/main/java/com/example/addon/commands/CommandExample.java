package com.example.addon.commands;

import meteordevelopment.meteorclient.systems.commands.Command;

public class CommandExample extends Command {
    public CommandExample() {
        super("Example", "An example command for your addon");
    }

    @Override
    public void execute(String[] args) {
        // Command implementation
    }
}
