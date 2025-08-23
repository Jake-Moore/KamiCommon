package com.kamikazejam.kamicommon.menu.api.title;

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
    private @Nullable MenuTitleProvider provider;
    private final @NotNull List<MenuTitleReplacement> replacements = new ArrayList<>();

    public MenuTitleCalculator() {
        provider = null;
    }

    @NotNull
    public String buildTitle(@NotNull Player player) {
        if (provider == null) {return " ";}
        String title = Objects.requireNonNull(provider.getTitle(player));
        for (MenuTitleReplacement replacement : replacements) {
            title = title.replace(replacement.getTarget(), replacement.getReplacement());
        }
        return title;
    }
}
