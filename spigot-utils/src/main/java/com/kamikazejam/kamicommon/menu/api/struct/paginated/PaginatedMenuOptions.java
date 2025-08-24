package com.kamikazejam.kamicommon.menu.api.struct.paginated;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.configuration.Configurable;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.layout.PaginationLayout;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.title.AbstractPaginatedMenuTitle;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.title.DefaultPaginatedMenuTitle;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A container for all the options that every {@link com.kamikazejam.kamicommon.menu.PaginatedMenu} must allow to be configured.<br>
 * Use Getters and Setters to access and modify these options.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class PaginatedMenuOptions extends MenuOptions<PaginatedMenu> {
    // Both Fields are not final, since they can be extended and then set, so long as they are an instance of the correct class
    private @NotNull PaginationLayout layout;
    private @NotNull AbstractPaginatedMenuTitle titleFormat;
    /**
     * Should the filler fill any empty page icon slots? For instance on the last page if we don't have enough icons to fill the page,
     * should we fill the last slots with filler (true) or leave them empty (false)?
     */
    @Setter
    private boolean fillerFillsEmptyPageIconSlots;
    // Icons
    /**
     * The icon that will be used to navigate to the next page.<br>
     * If null, no icon will be shown. (manual navigation required).
     */
    @Setter
    private @Nullable MenuIcon<PaginatedMenu> nextPageIcon;
    /**
     * The icon that will be used to navigate to the previous page.<br>
     * If null, no icon will be shown. (manual navigation required).
     */
    @Setter
    private @Nullable MenuIcon<PaginatedMenu> prevPageIcon;

    /**
     * The icon that will be used in place of {@link #nextPageIcon} when the next page is not available.<br>
     * If null, no icon will be shown. (default behavior).
     */
    @Setter
    private @Nullable MenuIcon<PaginatedMenu> nextPageInactiveIcon;

    /**
     * The icon that will be used in place of {@link #prevPageIcon} when the previous page is not available.<br>
     * If null, no icon will be shown. (default behavior).
     */
    @Setter
    private @Nullable MenuIcon<PaginatedMenu> prevPageInactiveIcon;

    /**
     * The message to send the player when they click the {@link #nextPageInactiveIcon} icon.<br>
     * If null, no message will be sent.
     */
    @Setter
    private @Nullable String noNextPageIconMessage;

    /**
     * The message to send the player when they click the {@link #prevPageInactiveIcon} icon.<br>
     * If null, no message will be sent.
     */
    @Setter
    private @Nullable String noPrevPageIconMessage;

    public PaginatedMenuOptions(@NotNull PaginationLayout layout) {
        Preconditions.checkNotNull(layout, "layout cannot be null");
        this.layout = layout;
        this.titleFormat = Config.getTitleFormat();
        this.fillerFillsEmptyPageIconSlots = Config.isFillerFillsEmptyPageIconSlots();
        this.nextPageIcon = Config.getNextPageIcon();
        this.prevPageIcon = Config.getPrevPageIcon();
        this.nextPageInactiveIcon = Config.getNextPageInactiveIcon();
        this.prevPageInactiveIcon = Config.getPrevPageInactiveIcon();
        this.noNextPageIconMessage = Config.getNoNextPageIconMessage();
        this.noPrevPageIconMessage = Config.getNoPrevPageIconMessage();
    }

    // Copy Constructor
    private PaginatedMenuOptions(@NotNull PaginatedMenuOptions copy) {
        this.layout = copy.layout.copy();
        this.titleFormat = copy.titleFormat.copy();
        this.fillerFillsEmptyPageIconSlots = copy.fillerFillsEmptyPageIconSlots;
        this.nextPageIcon = copy.nextPageIcon == null ? null : copy.nextPageIcon.copy();
        this.prevPageIcon = copy.prevPageIcon == null ? null : copy.prevPageIcon.copy();
        this.nextPageInactiveIcon = copy.nextPageInactiveIcon == null ? null : copy.nextPageInactiveIcon.copy();
        this.prevPageInactiveIcon = copy.prevPageInactiveIcon == null ? null : copy.prevPageInactiveIcon.copy();
        this.noNextPageIconMessage = copy.noNextPageIconMessage;
        this.noPrevPageIconMessage = copy.noPrevPageIconMessage;
    }

    public void setLayout(@NotNull PaginationLayout layout) {
        Preconditions.checkNotNull(layout, "layout cannot be null");
        this.layout = layout;
    }

    public void setTitleFormat(@NotNull AbstractPaginatedMenuTitle titleFormat) {
        Preconditions.checkNotNull(titleFormat, "titleFormat cannot be null");
        this.titleFormat = titleFormat;
    }

    @Override
    public @NotNull PaginatedMenuOptions copy() {
        // Use copy constructor to copy paginated options
        PaginatedMenuOptions copy = new PaginatedMenuOptions(this);
        // Copy base options from MenuOptions abstract class
        this.copyInto(copy);
        return copy;
    }

    @Configurable
    public static class Config {
        @Getter @Setter
        private static @NotNull AbstractPaginatedMenuTitle titleFormat = new DefaultPaginatedMenuTitle();
        @Getter @Setter
        private static boolean fillerFillsEmptyPageIconSlots = true;
        @Getter @Setter
        private static @Nullable MenuIcon<PaginatedMenu> nextPageIcon = new MenuIcon<>(true, new ItemBuilder(XMaterial.ARROW).setName("&a&lNext Page &a▶"));
        @Getter @Setter
        private static @Nullable MenuIcon<PaginatedMenu> prevPageIcon = new MenuIcon<>(true, new ItemBuilder(XMaterial.ARROW).setName("&a◀ &a&lPrevious Page"));
        @Getter @Setter
        private static @Nullable MenuIcon<PaginatedMenu> nextPageInactiveIcon = null;
        @Getter @Setter
        private static @Nullable MenuIcon<PaginatedMenu> prevPageInactiveIcon = null;
        /**
         * Only applies to the {@link #getNextPageInactiveIcon()} click (if enabled).
         */
        @Getter @Setter
        private static @Nullable String noNextPageIconMessage = "&cNo next page available";
        /**
         * Only applies to the {@link #getPrevPageInactiveIcon()} click (if enabled).
         */
        @Getter @Setter
        private static @Nullable String noPrevPageIconMessage = "&cNo previous page available";
    }
}
