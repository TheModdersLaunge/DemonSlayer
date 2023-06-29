package whosalbercik.demonslayer.core;

import net.minecraft.util.Hand;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import whosalbercik.demonslayer.DemonSlayer;
import whosalbercik.demonslayer.commands.TeamCommand;
import whosalbercik.demonslayer.object.Team;
import whosalbercik.demonslayer.saveddata.TeamSavedData;

@Mod.EventBusSubscriber(modid = DemonSlayer.MODID)
public class ModForgeEvent {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        TeamCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void attackEvent(AttackEntityEvent event) {
        if (!(event.getPlayer().world instanceof ServerWorld)) return;

        for (Team team: TeamSavedData.get((ServerWorld) event.getEntity().getEntityWorld()).getTeams()) {
            if (team.playerIn(event.getPlayer()) && (team.getBannedItems().contains(event.getPlayer().getHeldItem(Hand.MAIN_HAND).getItem()) || team.getBannedItems().contains(event.getPlayer().getHeldItem(Hand.OFF_HAND).getItem()))) {
                // item held is banned
                event.setCanceled(true);
                event.getPlayer().sendStatusMessage(new StringTextComponent("Item held is banned in team you are in!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED))), true);
            }

            }
        }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent event) {
        if (!(event.getEntity().world instanceof ServerWorld)) return;

        for (Team team: TeamSavedData.get((ServerWorld) event.getEntity().getEntityWorld()).getTeams()) {
            if (team.playerIn(event.getPlayer()) && (team.getBannedItems().contains(event.getPlayer().getHeldItem(Hand.MAIN_HAND).getItem()) || team.getBannedItems().contains(event.getPlayer().getHeldItem(Hand.OFF_HAND).getItem()))) {
                // item held is banned
                event.setCanceled(true);
                event.getPlayer().sendStatusMessage(new StringTextComponent("Cannot interact when holding banned item!").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED))), true);
            }

        }
    }
}


