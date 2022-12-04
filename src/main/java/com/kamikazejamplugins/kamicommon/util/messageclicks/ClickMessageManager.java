package com.kamikazejamplugins.kamicommon.util.messageclicks;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.messageclicks.clicks.Click;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ClickMessageManager {
    public static List<List<TextComponent>> processPlaceholders(String line, Click... clicks) {
        return processPlaceholders(new TextComponent(line), clicks);
    }

    public static List<List<TextComponent>> processPlaceholders(TextComponent base, Click... clicks) {
        List<List<TextComponent>> temp = new ArrayList<>();
        temp.add(new ArrayList<>(Collections.singleton(base)));

        //For each ClickContainer, reprocess a new temp list of components (replacing placeholders and stuff)
        for (Click click : clicks) {
            List<List<TextComponent>> newTemp = processMultiPlaceholders(click, temp);
            temp.clear();
            temp.addAll(newTemp);
        }
        return temp;
    }

    private static List<List<TextComponent>> processMultiPlaceholders(Click click, List<List<TextComponent>> components) {
        List<List<TextComponent>> temp = new ArrayList<>();
        for (List<TextComponent> componentList : components) {
            List<TextComponent> newComponentList = new ArrayList<>();
            for (TextComponent component : componentList) {
                if (component.getText().contains(click.replacement)) {
                    List<TextComponent> merged = new ArrayList<>(getComponentsWithButton(click, component));
                    newComponentList.addAll(merged);
                }else {
                    newComponentList.add(component);
                }
            }
            temp.add(newComponentList);
        }
        return temp;
    }

    private static List<TextComponent> getComponentsWithButton(Click click, TextComponent component) {
        List<TextComponent> components = new ArrayList<>();

        //Create the click TextComponent
        TextComponent clickComponent = new TextComponent(click.text);
        click.setClickEvent(clickComponent);
        clickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(click.hover).create()));

        String text = component.getText();
        String[] parts = text.split(Pattern.quote(click.replacement));
        //If the message starts with the click, send the click and then the rest of the message
        if (ChatColor.stripColor(text).startsWith(click.replacement)) {
            components.add(clickComponent);
            components.add(new TextComponent(parts[parts.length-1]));
            //If the message ends with the click, send the rest, then the click
        }else if (ChatColor.stripColor(text).endsWith(click.replacement)) {
            components.add(new TextComponent(parts[0]));
            components.add(clickComponent);
            //If the click is somewhere in between, form the message around the click
        }else if (parts.length == 2) {
            components.add(new TextComponent(parts[0]));
            components.add(clickComponent);
            components.add(new TextComponent(parts[parts.length-1]));
        }else {
            KamiCommon.warn(click.replacement + " in raidPlan.request.message of lang.yml is formatted in a way this plugin could not understand. There should only be 1 instance of it, and it can either be in the front, at the end, or in the middle. Splitting with that placeholder should produce 1-2 parts");
        }

        return components;
    }
}
