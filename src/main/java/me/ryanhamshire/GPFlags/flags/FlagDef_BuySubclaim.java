package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.objects.Claim;
import me.ryanhamshire.GriefPrevention.objects.enums.ClaimPermission;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.List;

public class FlagDef_BuySubclaim extends PlayerMovementFlagDefinition {

    public FlagDef_BuySubclaim(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "BuySubclaim";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableBuySubclaim, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableBuySubclaim);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty())
            return new SetFlagResult(false, new MessageSpecifier(Messages.CostRequired));

        try {
            double cost = Double.parseDouble(parameters);
            if (cost < 0) {
                return new SetFlagResult(false, new MessageSpecifier(Messages.CostRequired));
            }
        } catch (NumberFormatException e) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.CostRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return;

        if (claimTo == null) return;
        if (claimTo.parent == null) return;
        if (claimTo.getPermission(player.getUniqueId().toString()) == ClaimPermission.Build) return;
        if (player.getUniqueId().equals(claimTo.getOwnerID())) return;

        MessagingUtil.sendMessage(player, TextMode.Info, Messages.SubclaimPrice, flag.parameters);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (claim == null) return;
        if (claim.parent == null) return;
        if (claim.getPermission(e.getPlayer().getUniqueId().toString()) == ClaimPermission.Build) return;
        if (player.getUniqueId().equals(claim.getOwnerID())) return;

        MessagingUtil.sendMessage(player, TextMode.Info, Messages.SubclaimPrice, flag.parameters);
    }
}
