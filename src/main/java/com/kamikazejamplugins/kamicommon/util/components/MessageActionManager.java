package com.kamikazejamplugins.kamicommon.util.components;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.util.components.actions.Action;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
public class MessageActionManager {
    /**
     * The easiest method to use. This will process the message, replace stuff, add events, and then send it.
     * @param player The player to send the messages to
     * @param line The line to process and send
     * @param actions The actions to add
     */
    public static void processAndSend(Player player, String line, Action... actions) {
        BaseComponent[] components = processPlaceholders(line, actions);
        player.spigot().sendMessage(components);
    }

    /**
     * The easiest method to use. This will process the message, replace stuff, add events, and then send it.
     * @param player The player to send the messages to
     * @param lines The lines to process and send
     * @param actions The actions to add
     */
    public static void processAndSend(Player player, List<String> lines, Action... actions) {
        for (String line : lines) { processAndSend(player, line, actions); }
    }

    /**
     * Returns TextComponent[] meant to be sent to a player in one line
     *  Example: player.sendMessages(components) or player.spigot().sendMessage(components)
     * @param line A line of text to search for actions in
     * @param actions The actions which will replace placeholders and setup events
     * @return A list of TextComponent[], each array meant to be sent to the player as one message
     */
    private static BaseComponent[] processPlaceholders(String line, Action... actions) {
        line = StringUtil.t(line);
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
    private static BaseComponent[] processPlaceholders(BaseComponent base, Action... actions) {
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

    private static List<BaseComponent> processMultiPlaceholders(Action action, List<BaseComponent> components) {
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

    private static List<BaseComponent> getComponentsWithButton(Action action, BaseComponent component) {
        List<BaseComponent> components = new ArrayList<>();

        //Create the click TextComponent
        BaseComponent[] legacyTexts = TextComponent.fromLegacyText(action.getReplacement());
        for (BaseComponent clickComponent : legacyTexts) {
            action.addClickEvent(clickComponent);
            action.addHoverEvent(clickComponent);

            String text = component.toLegacyText();
            String[] parts = text.split(Pattern.quote(action.getPlaceholder()));
            //If the message starts with the click, send the click and then the rest of the message
            if (ChatColor.stripColor(text).startsWith(action.getPlaceholder())) {
                components.add(clickComponent);
                components.add(new TextComponent(parts[parts.length-1]));
                //If the message ends with the click, send the rest, then the click
            }else if (ChatColor.stripColor(text).endsWith(action.getPlaceholder())) {
                components.add(new TextComponent(parts[0]));
                components.add(clickComponent);
                //If the click is somewhere in between, form the message around the click
            }else if (parts.length == 2) {
                components.add(new TextComponent(parts[0]));
                components.add(clickComponent);
                components.add(new TextComponent(parts[parts.length-1]));
            }else {
                KamiCommon.get().getLogger().warning(action.getPlaceholder() + " in raidPlan.request.message of lang.yml is formatted in a way this plugin could not understand. There should only be 1 instance of it, and it can either be in the front, at the end, or in the middle. Splitting with that placeholder should produce 1-2 parts");
            }
        }

        return components;
    }
}
