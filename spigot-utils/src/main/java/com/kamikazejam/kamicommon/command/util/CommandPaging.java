package com.kamikazejam.kamicommon.command.util;

import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.configuration.Configurable;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
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
    public static KMessageSingle titleizedPageTitle(
            @NotNull KamiCommand command,
            @NotNull String title,
            int pageNum,
            int pageCount
    ) {
        // Validate KamiCommand context
        Preconditions.checkNotNull(
                command.getContext(),
                "titleizedPageTitle must be called synchronously to a " +
                "command's perform execution, where CommandContext is available"
        );

        // Create the title string, using placeholders for the prev/next arrows
        String pageTitle = Config.getTitleFormat()
                .replace(Config.getPlaceholderTitle(), title)
                .replace(Config.getPlaceholderPageNum(), String.valueOf(pageNum))
                .replace(Config.getPlaceholderPageCount(), String.valueOf(pageCount));

        // Calculate the length of what will be visible (Strip colors & replace variables)
        int pageTitleLength = ChatColor.stripColor(LegacyColors.t(pageTitle
                .replace(Config.getPlaceholderPrevPage(), Config.getBackIcon())
                .replace(Config.getPlaceholderNextPage(), Config.getForwardIcon())
        )).length();

        // Calculate how many characters we need to add on either side
        int leftPaddingSize = Math.max(0, (int) Math.ceil((TITLE_LINE_LENGTH - pageTitleLength) / 2.0));
        int rightPaddingSize = Math.max(0, TITLE_LINE_LENGTH - pageTitleLength - leftPaddingSize);

        // Create the title line with padding
        String leftPadding = Txt.Config.getTitlePaddingColor() + Txt.Config.getTitlePaddingChar().toString().repeat(leftPaddingSize);
        String rightPadding = Txt.Config.getTitlePaddingColor() + Txt.Config.getTitlePaddingChar().toString().repeat(rightPaddingSize);

        // Construct the final title line (adds padding to both sides)
        String finalTitle = leftPadding + pageTitle + rightPadding;

        // Create the
        return applyPageActions(command, new KMessageSingle(finalTitle), pageNum, pageCount);
    }

    /**
     * Creates a 'page' (list of message lines) linked to the provided command.<br>
     * Uses the command context's sender to determine the page height.
     *
     * @param command The command to which this page belongs, used for generating the page flip commands.
     * @param lines The lines of 'content' to paginate. This should be ALL the lines you want to show, not just the ones for this page.
     * @param pageNum The page number, 1-based (e.g. 1 for the first page).
     * @param title The unformatted title, which will be titleized.
     * @return The ordered list of {@link KMessage} objects, for sending to any Player or CommandSender.
     */
    @NotNull
    public static List<KMessage> getPage(
            @NotNull KamiCommand command,
            @NotNull List<KMessageSingle> lines,
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
     * @return The ordered list of {@link KMessage} objects, for sending to any Player or CommandSender.
     */
    @NotNull
    public static List<KMessage> getPage(
            @NotNull KamiCommand command,
            @NotNull List<KMessageSingle> lines,
            int pageNum,
            @NotNull String title,
            int pageheight
    ) {
        // Create Ret
        List<KMessage> ret = new KamiList<>();
        int pageIndex = pageNum - 1; // 0-based index
        int pageCount = (int) Math.ceil(((double) lines.size()) / pageheight);

        // Add Title
        KMessageSingle kTitle = titleizedPageTitle(command, title, pageNum, pageCount);
        ret.add(kTitle);

        // Check empty and invalid
        if (pageCount == 0) {
            ret.add(new KMessageSingle(Config.getNoPagesMessage()));
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
     * @param title The titleized message line to which the page actions will be applied.
     * @param pageNum The current page number, 1-based (e.g. 1 for the first page).
     * @param pageCount The total number of pages.
     *
     * @return The same {@link KMessageSingle} for chaining, with the page actions applied.
     */
    @NotNull
    private static KMessageSingle applyPageActions(
            @NotNull KamiCommand command,
            @NotNull KMessageSingle title,
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
        @Nullable String forwardCmd = getFlipPageCommand(command, pageNum - 1, args);
        if (pageNum > 1 && forwardCmd != null) {
            String replacement = Config.getActiveIconColor() + Config.getBackIcon();
            title.addClickRunCommand(Config.getPlaceholderPrevPage(), replacement, forwardCmd);
        } else {
            String replacement = Config.getInactiveIconColor() + Config.getBackIcon();
            title.setLine(title.getLine().replace(Config.getPlaceholderPrevPage(), replacement));
        }

        // Add flip forwards command
        @Nullable String backCmd = getFlipPageCommand(command, pageNum + 1, args);
        if (pageCount > pageNum && backCmd != null) {
            String replacement = Config.getActiveIconColor() + Config.getForwardIcon();
            title.addClickRunCommand(Config.getPlaceholderNextPage(), replacement, backCmd);
        } else {
            String replacement = Config.getInactiveIconColor() + Config.getForwardIcon();
            title.setLine(title.getLine().replace(Config.getPlaceholderNextPage(), replacement));
        }

        return title;
    }

    @NotNull
    private static KMessageSingle getInvalidPageMessage(int size) {
        if (size == 0) {
            return new KMessageSingle(Config.getNoPagesMessage());
        } else if (size == 1) {
            return new KMessageSingle(Config.getOnlyOnePageMessage());
        } else {
            String message = String.format(Config.getInvalidPageMessage(), size);
            return new KMessageSingle(message);
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
        @Getter private static final @NotNull String placeholderPrevPage = "{prevPage}";
        @Getter private static final @NotNull String placeholderNextPage = "{nextPage}";
        @Getter private static final @NotNull String placeholderPageNum = "{pageNum}";
        @Getter private static final @NotNull String placeholderPageCount = "{pageCount}";

        // Configurable values
        @Getter @Setter
        private static @NotNull String backIcon = "[<]";
        @Getter @Setter
        private static @NotNull String forwardIcon = "[>]";

        @Getter @Setter
        private static @NotNull ChatColor activeIconColor = ChatColor.AQUA;
        @Getter @Setter
        private static @NotNull ChatColor inactiveIconColor = ChatColor.GRAY;

        @Getter @Setter
        private static @NotNull String titleFormat =
                ChatColor.GOLD + ".[ "
                + ChatColor.DARK_GREEN + placeholderTitle
                + " " + placeholderPrevPage + " "
                + ChatColor.GOLD + placeholderPageNum + "/" + placeholderPageCount
                + " " + placeholderNextPage
                + ChatColor.GOLD + " ].";

        @Getter @Setter
        private static @NotNull String noPagesMessage = ChatColor.YELLOW + "Sorry, no pages available.";
        @Getter @Setter
        private static @NotNull String onlyOnePageMessage = ChatColor.RED + "Invalid, there is only one page.";
        @Getter @Setter
        private static @NotNull String invalidPageMessage = ChatColor.RED + "Invalid, page must be between 1 and %d.";

    }
}
