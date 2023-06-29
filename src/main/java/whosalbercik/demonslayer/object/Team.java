
package whosalbercik.demonslayer.object;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.UUID;

public class Team {
    private ArrayList<UUID> players;
    private ArrayList<Item> bannedItems;
    private String id;

    public Team(String id) {
        this.id = id;
        players = new ArrayList<UUID>();
        bannedItems = new ArrayList<Item>();
    }

    public boolean playerIn(PlayerEntity p) {
        return players.contains(p.getUniqueID());
    }

    public boolean itemBanned(Item item) {
        return bannedItems.contains(item);
    }

    public void addPlayer(PlayerEntity p) {
        players.add(p.getUniqueID());
    }

    public void addPlayer(UUID p) {
        players.add(p);
    }

    public void addItem(Item item) {
        bannedItems.add(item);
    }

    public ArrayList<UUID> getPlayers() {
        return players;
    }

    public ArrayList<Item> getBannedItems() {
        return bannedItems;
    }

    public String getId() {
        return id;
    }
}
