/*
 * Copyright (C) 2016 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.util;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class CommonMessages {
    
    public static void sendPlayerOnlyCommandError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This command can only be run by a player.");
    }
    
    public static void sendNoPermissionError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Sorry, you don't have permission.");
    }

    public static void sendInvalidSubcommandError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Invalid subcommand.");
    }
    
    public static void sendNotEnoughArgumentsError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Not enough arguments.");
    }

    public static void sendFileNotFoundError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "File not found.");
    }

    public static void sendIOError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There was an IOError. Ask developer or admin for help.");
    }


}
