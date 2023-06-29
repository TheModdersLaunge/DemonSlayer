package whosalbercik.demonslayer.core;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import whosalbercik.demonslayer.DemonSlayer;
import whosalbercik.demonslayer.commands.TeamCommand;

@Mod.EventBusSubscriber(modid = DemonSlayer.MODID)
public class ModForgeEvent {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        TeamCommand.register(event.getDispatcher());
    }
}
