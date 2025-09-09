package com.kamikazejam.kamicommon.menu.api.title;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class MenuTitleCalculator {
    private @Nullable ComponentMenuTitleProvider provider;
    private final @NotNull List<MenuTitleReplacement> replacements = new ArrayList<>();

    public MenuTitleCalculator() {
        provider = null;
    }

    @NotNull
    public VersionedComponent buildTitle(@NotNull Player player) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        if (provider == null) { return serializer.fromLegacySection(" "); }
        String miniMessage = Objects.requireNonNull(provider.getTitle(player)).serializeMiniMessage();
        for (MenuTitleReplacement replacement : replacements) {
            miniMessage = miniMessage.replace(replacement.getTarget(), replacement.getReplacement());
        }
        return serializer.fromMiniMessage(miniMessage);
    }
}
