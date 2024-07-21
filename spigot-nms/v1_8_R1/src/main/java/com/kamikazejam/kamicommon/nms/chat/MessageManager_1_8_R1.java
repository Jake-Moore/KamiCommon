package com.kamikazejam.kamicommon.nms.chat;

import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.*;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.util.StringUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A utility class for processing and sending messages <p>
 * You can create an {@link Action} and then send a message to a player with the action <p>
 * This class will parse those Action placeholders and replace it with whatever Action you specify
 */
@SuppressWarnings("unused")
public class MessageManager_1_8_R1 extends AbstractMessageManager {
    @NotNull private final AbstractItemTextPre_1_17 itemText;
    public MessageManager_1_8_R1(@NotNull AbstractItemTextPre_1_17 itemText) {
        this.itemText = itemText;
    }

    @Override
    public void processAndSend(@NotNull CommandSender sender, @NotNull KMessage kMessage) {
        for (String line : kMessage.getLines()) {
            if (sender instanceof Player player) {
                // Use BaseComponent -> will support all actions
                BaseComponent[] components = processPlaceholders(line, kMessage.isTranslate(), kMessage.getActions());
                player.spigot().sendMessage(components);
            }else {
                // CommandSender can't take BaseComponent or use any of the Action features -> just send colored text
                // We must recompile the message using the Action replacements, but none of the actions (simple)
                String msg = line;
                for (Action action : kMessage.getActions()) {
                    msg = msg.replace(action.getPlaceholder(), action.getReplacement());
                }
                sender.sendMessage((kMessage.isTranslate()) ? StringUtil.t(msg) : msg);
            }
        }
    }

    /**
     * Returns TextComponent[] meant to be sent to a player in one line
     *  Example: player.sendMessages(components) or player.spigot().sendMessage(components)
     * @param line A line of text to search for actions in
     * @param actions The actions which will replace placeholders and setup events
     * @return A list of TextComponent[], each array meant to be sent to the player as one message
     */
    private BaseComponent[] processPlaceholders(@NotNull String line, boolean translate, @NotNull List<Action> actions) {
        if (translate) { line = StringUtil.t(line); }
        List<BaseComponent> components = new ArrayList<>();

        BaseComponent[] legacies = TextComponent.fromLegacyText(line);
        for (BaseComponent legacy : legacies) {
            components.addAll(Arrays.asList(processPlaceholders(legacy, actions)));
        }

        return components.toArray(new BaseComponent[0]);
    }

    /**
     * Returns TextComponent[] meant to be sent to a player in one line
     *  Example: player.sendMessages(components) or player.spigot().sendMessage(components)
     * @param base A TextComponent to use as the base for the message
     * @param actions The actions which will replace placeholders and setup events
     * @return A list of TextComponent[], each array meant to be sent to the player as one message
     */
    private BaseComponent[] processPlaceholders(@NotNull BaseComponent base, @NotNull List<Action> actions) {
        List<BaseComponent> temp = new ArrayList<>();
        temp.add(base);

        //For each ClickContainer, reprocess a new temp list of components (replacing placeholders and stuff)
        for (Action action : actions) {
            List<BaseComponent> newTemp = processMultiPlaceholders(action, temp);
            temp.clear();
            temp.addAll(newTemp);
        }

        return temp.toArray(new BaseComponent[0]);
    }

    private List<BaseComponent> processMultiPlaceholders(Action action, List<BaseComponent> components) {
        List<BaseComponent> newComponentList = new ArrayList<>();
        for (BaseComponent component : components) {
            if (component.toLegacyText().contains(action.getPlaceholder())) {
                List<BaseComponent> merged = new ArrayList<>(getComponentsWithButton(action, component));
                newComponentList.addAll(merged);
            }else {
                newComponentList.add(component);
            }
        }
        return newComponentList;
    }

    private List<BaseComponent> getComponentsWithButton(Action action, BaseComponent component) {
        List<BaseComponent> components = new ArrayList<>();

        // Create the click TextComponent
        // This ENTIRE array represents the replacement, in this specific order
        BaseComponent[] legacyTexts = TextComponent.fromLegacyText(action.getReplacement());
        for (BaseComponent clickComponent : legacyTexts) {

            @Nullable Click click = action.getClick();
            if (click instanceof ClickCmd clickCmd) {
                clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCmd.getCommand()));
            }else if (click instanceof ClickSuggest clickSuggest) {
                clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickSuggest.getSuggestion()));
            }else if (click instanceof ClickUrl clickUrl) {
                clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickUrl.getUrl()));
            }

            @Nullable Hover hover = action.getHover();
            if (hover instanceof HoverText hoverText) {
                // For every version [1.8 - 1.16.5] this HoverEvent constructor is fine
                clickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText.getText())));
            }else if (hover instanceof HoverItem hoverItem) {
                clickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemText.getComponents(hoverItem.getItemStack())));
            }
        }

        // Check the text and split it into parts
        String text = component.toLegacyText();
        String[] parts = text.split(Pattern.quote(action.getPlaceholder()));

        //If the message starts with the click, send the click and then the rest of the message
        if (ChatColor.stripColor(text).startsWith(action.getPlaceholder())) {
            components.addAll(Arrays.asList(legacyTexts));
            components.add(new TextComponent(parts[parts.length-1]));
            //If the message ends with the click, send the rest, then the click
        }else if (ChatColor.stripColor(text).endsWith(action.getPlaceholder())) {
            components.add(new TextComponent(parts[0]));
            components.addAll(Arrays.asList(legacyTexts));
            //If the click is somewhere in between, form the message around the click
        }else if (parts.length == 2) {
            components.add(new TextComponent(parts[0]));
            components.addAll(Arrays.asList(legacyTexts));
            components.add(new TextComponent(parts[parts.length-1]));
        }else {
            throw new IllegalArgumentException("Invalid placeholder: " + action.getPlaceholder());
        }

        return components;
    }
}
