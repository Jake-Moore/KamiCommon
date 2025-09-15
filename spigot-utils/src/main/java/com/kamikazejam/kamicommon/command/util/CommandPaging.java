package com.kamikazejam.kamicommon.command.util;

import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.configuration.Configurable;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.event.ClickEvent;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.event.HoverEvent;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.kamikazejam.kamicommon.util.Txt.Config.TITLE_LINE_LENGTH;

public class CommandPaging {
    public static final int PAGEHEIGHT_PLAYER = 9;
    public static final int PAGEHEIGHT_CONSOLE = 50;

    /**
     * Creates a full titleized page message for a command.
     * @param command The command to which this page belongs, used for generating the page flip commands.
     * @param title The unformatted title, which will be titleized.
     * @param pageNum The current page number, 1-based (e.g. 1 for the first page).
     * @param pageCount The total number of pages.
     */
    @SuppressWarnings("ExtractMethodRecommender")
    @NotNull
    public static VersionedComponent titleizedPageTitle(
            @NotNull KamiCommand command,
            @NotNull String title,
            int pageNum,
            int pageCount
    ) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        // Validate KamiCommand context
        Preconditions.checkNotNull(
                command.getContext(),
                "titleizedPageTitle must be called synchronously to a " +
                "command's perform execution, where CommandContext is available"
        );

        // Create the title string, using placeholders for the prev/next arrows
        String rawTitleMini = Config.getTitleFormatMini()
                .replace(Config.getPlaceholderTitle(), title)
                .replace(Config.getPlaceholderPageNum(), String.valueOf(pageNum))
                .replace(Config.getPlaceholderPageCount(), String.valueOf(pageCount));

        // Calculate the length of what will be visible (Strip colors & replace variables)
        int pageTitleLength = serializer.fromMiniMessage(
                rawTitleMini
                        .replace("<" + Config.getTagPrevPage() + ">", Config.getBackIcon())
                        .replace("<" + Config.getTagNextPage() + ">", Config.getForwardIcon())
        ).serializePlainText().length();

        // Calculate how many characters we need to add on either side
        int leftPaddingSize = Math.max(0, (int) Math.ceil((TITLE_LINE_LENGTH - pageTitleLength) / 2.0));
        int rightPaddingSize = Math.max(0, TITLE_LINE_LENGTH - pageTitleLength - leftPaddingSize);

        // Create the title line with padding
        String leftPaddingMini = Txt.Config.getTitlePaddingColorMini() + Txt.Config.getTitlePaddingChar().toString().repeat(leftPaddingSize);
        String rightPaddingMini = Txt.Config.getTitlePaddingColorMini() + Txt.Config.getTitlePaddingChar().toString().repeat(rightPaddingSize);

        // Construct the final title line (adds padding to both sides)
        String miniMessageTitleMini = leftPaddingMini + rawTitleMini + rightPaddingMini;

        // Create the
        return applyPageActions(command, miniMessageTitleMini, pageNum, pageCount);
    }

    /**
     * Creates a 'page' (list of message lines) linked to the provided command.<br>
     * Uses the command context's sender to determine the page height.
     *
     * @param command The command to which this page belongs, used for generating the page flip commands.
     * @param lines The lines of 'content' to paginate. This should be ALL the lines you want to show, not just the ones for this page.
     * @param pageNum The page number, 1-based (e.g. 1 for the first page).
     * @param title The unformatted title, which will be titleized.
     * @return The ordered list of {@link VersionedComponent} objects, for sending to any Player or CommandSender.
     */
    @NotNull
    public static List<VersionedComponent> getPage(
            @NotNull KamiCommand command,
            @NotNull List<VersionedComponent> lines,
            int pageNum,
            @NotNull String title
    ) {
        // Validate KamiCommand context
        CommandContext context = Preconditions.checkNotNull(
                command.getContext(),
                "titleizedPageTitle must be called synchronously to a " +
                "command's perform execution, where CommandContext is available"
        );

        int pageHeight = (context.getSender() instanceof Player) ? PAGEHEIGHT_PLAYER : PAGEHEIGHT_CONSOLE;
        return getPage(command, lines, pageNum, title, pageHeight);
    }

    /**
     * Creates a 'page' (list of message lines) linked to the provided command.
     * @param command The command to which this page belongs, used for generating the page flip commands.
     * @param lines The lines of 'content' to paginate. This should be ALL the lines you want to show, not just the ones for this page.
     * @param pageNum The page number, 1-based (e.g. 1 for the first page).
     * @param title The unformatted title, which will be titleized.
     * @param pageheight The height of an individual page, in lines.
     * @return The ordered list of {@link VersionedComponent} objects, for sending to any Player or CommandSender.
     */
    @NotNull
    public static List<VersionedComponent> getPage(
            @NotNull KamiCommand command,
            @NotNull List<VersionedComponent> lines,
            int pageNum,
            @NotNull String title,
            int pageheight
    ) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        // Create Ret
        List<VersionedComponent> ret = new KamiList<>();
        int pageIndex = pageNum - 1; // 0-based index
        int pageCount = (int) Math.ceil(((double) lines.size()) / pageheight);

        // Add Title
        VersionedComponent titleComponent = titleizedPageTitle(command, title, pageNum, pageCount);
        ret.add(titleComponent);

        // Check empty and invalid
        if (pageCount == 0) {
            ret.add(serializer.fromMiniMessage(Config.getNoPagesMessageMini()));
            return ret;
        } else if (pageIndex < 0 || pageNum > pageCount) {
            ret.add(getInvalidPageMessage(pageCount));
            return ret;
        }

        // Get Lines
        int from = pageIndex * pageheight;
        int to = from + pageheight;
        if (to > lines.size()) {
            to = lines.size();
        }

        // Add page lines
        ret.addAll(lines.subList(from, to));

        // Return Ret
        return ret;
    }

    /**
     * @param miniMessageTitle The titleized message line (in MiniMessage format) to which the page actions will be applied.
     * @param pageNum The current page number, 1-based (e.g. 1 for the first page).
     * @param pageCount The total number of pages.
     *
     * @return The same {@link VersionedComponent} for chaining, with the page actions applied.
     */
    @SuppressWarnings("PatternValidation") @NotNull
    private static VersionedComponent applyPageActions(
            @NotNull KamiCommand command,
            @NotNull String miniMessageTitle,
            int pageNum,
            int pageCount
    ) {
        // Validate KamiCommand context
        CommandContext context = Preconditions.checkNotNull(
                command.getContext(),
                "titleizedPageTitle must be called synchronously to a " +
                "command's perform execution, where CommandContext is available"
        );
        List<String> args = context.getArgs();

        // Add flip backwards command
        @Nullable String backwardCmd = getFlipPageCommand(command, pageNum - 1, args);
        @NotNull TagResolver.Single forwardTag;
        if (pageNum > 1 && backwardCmd != null) {
            Component hoverText = NmsAPI.getVersionedComponentSerializer().fromMiniMessage(Config.getBackIconHoverMini()).asInternalComponent();
            String replacement = Config.getActiveIconColorMini() + Config.getBackIcon();
            forwardTag = Placeholder.component(
                    Config.getTagPrevPage(),
                    MiniMessage.miniMessage().deserialize(replacement)
                            .clickEvent(ClickEvent.runCommand(backwardCmd))
                            .hoverEvent(HoverEvent.showText(hoverText))
            );
        } else {
            String replacement = Config.getInactiveIconColorMini() + Config.getBackIcon();
            forwardTag = Placeholder.component(
                    Config.getTagPrevPage(),
                    MiniMessage.miniMessage().deserialize(replacement)
            );
        }

        // Add flip forwards command
        @Nullable String forwardCmd = getFlipPageCommand(command, pageNum + 1, args);
        @NotNull TagResolver.Single backTag;
        if (pageCount > pageNum && forwardCmd != null) {
            Component hoverText = NmsAPI.getVersionedComponentSerializer().fromMiniMessage(Config.getForwardIconHoverMini()).asInternalComponent();
            String replacement = Config.getActiveIconColorMini() + Config.getForwardIcon();
            backTag = Placeholder.component(
                    Config.getTagNextPage(),
                    MiniMessage.miniMessage().deserialize(replacement)
                            .clickEvent(ClickEvent.runCommand(forwardCmd))
                            .hoverEvent(HoverEvent.showText(hoverText))
            );
        } else {
            String replacement = Config.getInactiveIconColorMini() + Config.getForwardIcon();
            backTag = Placeholder.component(
                    Config.getTagNextPage(),
                    MiniMessage.miniMessage().deserialize(replacement)
            );
        }

        Component component = MiniMessage.miniMessage().deserialize(miniMessageTitle, TagResolver.resolver(forwardTag, backTag));
        return NmsAPI.getVersionedComponentSerializer().fromInternalComponent(component);
    }

    @NotNull
    private static VersionedComponent getInvalidPageMessage(int size) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        if (size == 0) {
            return serializer.fromMiniMessage(Config.getNoPagesMessageMini());
        } else if (size == 1) {
            return serializer.fromMiniMessage(Config.getOnlyOnePageMessageMini());
        } else {
            return serializer.fromMiniMessage(String.format(Config.getInvalidPageMessageMini(), size));
        }
    }

    @Nullable
    private static String getFlipPageCommand(
            @NotNull KamiCommand command,
            int destinationPage,
            @NotNull List<String> args
    ) {
        // Create the command line
        String number = String.valueOf(destinationPage);

        int pageParamIndex = command.getPageParameterIndex();
        if (pageParamIndex == -1) { return null; } // Couldn't find which arg is the page

        List<String> arguments = new ArrayList<>(args);

        // If our page index is farther out than the args we've supplied so far,
        //  try supplementing with the defaults
        if (arguments.size() <= pageParamIndex) {
            // Add defaults for previous arguments
            for (int i = arguments.size(); i < pageParamIndex; i++) {
                try {
                    // Ensure we fetch a valid param, which has its default value set
                    Parameter<?> param = command.getParameter(i);
                    if (param == null || !param.isDefaultValueSet()) { return null; }
                    // Add the default value (which we know was set)
                    arguments.add(String.valueOf(command.getParameter(i).getDefaultValue()));
                }catch (IndexOutOfBoundsException ignored) {
                    return null;
                }
            }
            // Add this page number as the next argument
            arguments.add(number);
        } else {
            // The page is in the current arguments, just update it
            arguments.set(pageParamIndex, number);
        }

        return command.getCommandLine(arguments);
    }

    @Configurable
    public static class Config {
        // Placeholders
        @Getter private static final @NotNull String placeholderTitle = "{title}";
        @Getter private static final @NotNull String placeholderPageNum = "{pageNum}";
        @Getter private static final @NotNull String placeholderPageCount = "{pageCount}";
        @Getter private static final @NotNull String tagPrevPage = "prev_page";
        @Getter private static final @NotNull String tagNextPage = "next_page";

        // Configurable values
        @Getter @Setter
        private static @NotNull String backIcon = "[<]";
        @Getter @Setter
        private static @NotNull String forwardIcon = "[>]";
        @Getter @Setter
        private static @NotNull String backIconHoverMini = "<gray>Go to previous page.";
        @Getter @Setter
        private static @NotNull String forwardIconHoverMini = "<gray>Go to next page.";

        @Getter @Setter
        private static @NotNull String activeIconColorMini = "<aqua>";
        @Getter @Setter
        private static @NotNull String inactiveIconColorMini = "<gray>";

        @Getter @Setter
        private static @NotNull String titleFormatMini = "<gold>.[ <dark_green>" + placeholderTitle + " <" + tagPrevPage + "> <gold>" + placeholderPageNum + "/" + placeholderPageCount + " <" + tagNextPage + "><gold>" + " ].";

        @Getter @Setter
        private static @NotNull String noPagesMessageMini = "<yellow>Sorry, no pages available.";
        @Getter @Setter
        private static @NotNull String onlyOnePageMessageMini = "<red>Invalid, there is only one page.";
        @Getter @Setter
        private static @NotNull String invalidPageMessageMini = "<red>Invalid, page must be between 1 and %d.";

    }
}
