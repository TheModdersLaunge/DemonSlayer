package whosalbercik.demonslayer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.system.CallbackI;
import whosalbercik.demonslayer.object.Team;
import whosalbercik.demonslayer.saveddata.TeamSavedData;

import java.util.Collection;
import java.util.UUID;

public class TeamCommand {
    public static void register(CommandDispatcher<CommandSource> stack) {
        stack.register(Commands.literal("teams")
                        .executes(TeamCommand::teams)

                .then(Commands.literal("create")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .executes(TeamCommand::create)))

                .then(Commands.literal("delete")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .executes(TeamCommand::remove)))

                .then(Commands.literal("add")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .then(Commands.argument("target", EntityArgument.players())
                                        .executes((ctx) -> addPlayer(ctx, EntityArgument.getPlayers(ctx, "target"))))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .then(Commands.argument("target", EntityArgument.players())
                                        .executes((ctx) -> removePlayer(ctx, EntityArgument.getPlayers(ctx, "target"))))))
                .then(Commands.literal("ban")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .then(Commands.argument("item", ItemArgument.item())
                                        .executes((ctx) -> banItem(ctx, ItemArgument.getItem(ctx, "item"))))))
                .then(Commands.literal("unban")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .then(Commands.argument("item", ItemArgument.item())
                                        .executes((ctx) -> unBanItem(ctx, ItemArgument.getItem(ctx, "item"))))))

                .then(Commands.literal("members")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .executes(TeamCommand::members)))

                .then(Commands.literal("banneditems")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .executes(TeamCommand::bannedItems))));
    }

    private static int teams(CommandContext<CommandSource> ctx) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        if (data.getTeams().isEmpty()) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("No teams found!"));
            return 0;
        }

        for (Team team: data.getTeams()) {
            ctx.getSource().sendFeedback(new StringTextComponent(String.format("TEAM: %s", team.getId())), false);
        }

        return 0;
    }

    private static int bannedItems(CommandContext<CommandSource> ctx) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        Team team = data.getTeam(ctx.getArgument("team", String.class));

        if (team == null) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("Team does not exist!"));
            return 0;
        }

        if (team.getBannedItems().isEmpty()) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("No banned items in this team!"));
            return 0;
        }

        for (Item item: team.getBannedItems()) {
            ctx.getSource().sendFeedback(new StringTextComponent(String.format("ITEM: %s", item.getRegistryName().toString())), false);
        }

        return 0;
    }

    private static int members(CommandContext<CommandSource> ctx) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        Team team = data.getTeam(ctx.getArgument("team", String.class));

        if (team == null) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("Team does not exist!"));
            return 0;
        }
        if (team.getPlayers().isEmpty()) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("No players are in this team!"));
            return 0;
        }

        for (UUID playerUUID: team.getPlayers()) {
            ctx.getSource().sendFeedback(new StringTextComponent(String.format("MEMBER: %s", ctx.getSource().getServer().getPlayerList().getPlayerByUUID(playerUUID).getDisplayName().getString())), false);
        }

        return 0;
    }

    private static int unBanItem(CommandContext<CommandSource> ctx, ItemInput item) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        Team team = data.getTeam(ctx.getArgument("team", String.class));

        if (team == null) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("Team does not exist!"));
            return 0;
        }

        team.addItem(item.getItem());

        data.addTeam(team);
        ctx.getSource().sendFeedback(new StringTextComponent("Unbanned item in team!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), true);

        return 0;
    }

    private static int banItem(CommandContext<CommandSource> ctx, ItemInput item) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        Team team = data.getTeam(ctx.getArgument("team", String.class));

        if (team == null) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("Team does not exist!"));
            return 0;
        }

        team.addItem(item.getItem());

        data.addTeam(team);
        ctx.getSource().sendFeedback(new StringTextComponent("Banned item in team!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), true);
        return 0;
    }

    private static int removePlayer(CommandContext<CommandSource> ctx, Collection<ServerPlayerEntity> target) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        Team team = data.getTeam(ctx.getArgument("team", String.class));

        if (team == null) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("Team does not exist!"));
            return 0;
        }

        if (!team.playersIn(target)) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("None of the specified players are in the team!"));
            return 0;
        }

        for (ServerPlayerEntity p: target) {
            team.removePlayer(p);
        }

        data.addTeam(team);

        ctx.getSource().sendFeedback(new StringTextComponent("Successfully removed player from team!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), false);
        return 0;
    }

    private static int addPlayer(CommandContext<CommandSource> ctx, Collection<ServerPlayerEntity> target) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        Team team = data.getTeam(ctx.getArgument("team", String.class));

        if (team == null) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("Team does not exist!"));
            return 0;
        }

        if (team.playersIn(target)) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("All of the specified players are already in the team!"));
            return 0;
        }

        for (ServerPlayerEntity p: target) {
            team.addPlayer(p);
        }
        data.addTeam(team);

        ctx.getSource().sendFeedback(new StringTextComponent("Successfully added player to team!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), false);
        return 0;
    }

    private static int remove(CommandContext<CommandSource> ctx) {
        TeamSavedData data = TeamSavedData.get(ctx.getSource().getWorld());

        Team team = data.getTeam(ctx.getArgument("id", String.class));

        if (team == null) {
            ctx.getSource().sendErrorMessage(new StringTextComponent("Team does not exist!"));
            return 0;
        }

        data.removeTeam(team);

        ctx.getSource().sendFeedback(new StringTextComponent("Successfully removed team!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), true);
        return 0;
    }

    private static int create(CommandContext<CommandSource> ctx) {
        Team team = new Team(ctx.getArgument("id", String.class));

        TeamSavedData.get(ctx.getSource().getWorld()).addTeam(team);

        ctx.getSource().sendFeedback(new StringTextComponent("Successfully created team!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), true);
        return 0;
    }
}
