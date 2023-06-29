package whosalbercik.demonslayer.saveddata;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.demonslayer.object.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TeamSavedData extends WorldSavedData {
    private final HashMap<String, Team> teams = new HashMap<String, Team>();

    public TeamSavedData(String name) {
        super(name);
    }


    public static TeamSavedData get(ServerWorld world) {

        DimensionSavedDataManager storage = world.getSavedData();
        return storage.getOrCreate(() -> new TeamSavedData("demonslayer"), "demonslayer");
    }


    public ArrayList<Team> getTeams() {
        ArrayList<Team> transactionsa = new ArrayList<>();
        teams.forEach((id, transaction) -> transactionsa.add(transaction));

        return transactionsa;
    }

    public Team getTeam(String id) {
        return teams.get(id);
    }

    public void addTeam(Team team) {
        teams.put(team.getId(), team);
        this.setDirty(true);
    }

    public void removeTeam(Team team) {
        teams.remove(team.getId());
        this.setDirty(true);

    }


    // to list
    @Override
    public void read(CompoundNBT nbt) {
        ListNBT teamsNBT = nbt.getList("demonslayer.teams", 10);
        for (INBT Iteam: teamsNBT) {
            CompoundNBT teamNBT = (CompoundNBT) Iteam;

            Team team = new Team(teamNBT.getString("demonslayer.id"));

            for (INBT player: teamNBT.getList("demonslayer.players", 10)) {
                CompoundNBT playerNBT = (CompoundNBT) player;
                team.addPlayer(playerNBT.getUniqueId("demonslayer.uuid"));
            }

            for (INBT bannedItem: teamNBT.getList("demonslayer.banneditems", 10)) {
                CompoundNBT itemNbt = (CompoundNBT) bannedItem;
                team.addItem(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemNbt.getString("demonslayer.item"))));
            }
            teams.put(team.getId(), team);
        }
    }


    // from list
    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT teamsNBT = new ListNBT();
        teams.forEach((id, team) -> {

            CompoundNBT teamNBT = new CompoundNBT();
            teamNBT.putString("demonslayer.id", team.getId());

            ListNBT players = new ListNBT();

            for (UUID player: team.getPlayers()) {
                CompoundNBT playerNBT = new CompoundNBT();
                playerNBT.putUniqueId("demonslayer.uuid", player);
                players.add(playerNBT);
            }

            teamNBT.put("demonslayer.players", players);

            ListNBT bannedItems = new ListNBT();

            for (Item item: team.getBannedItems()) {

                CompoundNBT itemNBT = new CompoundNBT();
                itemNBT.putString("demonslayer.item", item.getRegistryName().toString());
                bannedItems.add(itemNBT)  ;
            }

            teamNBT.put("demonslayer.banneditems", bannedItems);
            teamsNBT.add(teamNBT);
        });

        nbt.put("demonslayer.teams", teamsNBT);

        return nbt;
    }
}
