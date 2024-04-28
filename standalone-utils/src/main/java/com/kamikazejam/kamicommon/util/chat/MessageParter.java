package com.kamikazejam.kamicommon.util.chat;

import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.StandaloneAction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MessageParter {
    public static List<MessagePart> getMessageParts(String original, StandaloneAction... actions) {
        List<MessagePart> messageParts = new ArrayList<>();
        messageParts.add(new MessagePart(original));

        for (StandaloneAction action : actions) {
            List<MessagePart> temp = injectAction(original, messageParts, action);
            messageParts.clear();
            messageParts.addAll(temp);
        }

        return messageParts;
    }

    private static List<MessagePart> injectAction(String original, List<MessagePart> parts, StandaloneAction action) {
        // Placeholder, which may be included fully inside of one part, or split between several parts
        String placeholder = StringUtil.t(action.getPlaceholder());
        String replacement = StringUtil.t(action.getReplacement());

        // Assume everything is translated, original and the placeholder/replacement are at this point

        // If placeholder is not in the original, then we don't need to do anything
        if (!original.contains(placeholder)) { return parts; }

        // Case 1: If the placeholder is fully inside of one part, then handle that part
        return processFullPlaceholder(new ArrayList<>(parts), action, placeholder, replacement);
    }

    // Only replaces first occurrence of a placeholder, does not support multiple of the same placeholder
    private static List<MessagePart> processFullPlaceholder(List<MessagePart> parts, StandaloneAction action, String placeholder, String replacement) {
        // A compiled list of parts, which should reflect the same content as the original message
        //   when compiled in order
        List<MessagePart> retParts = new ArrayList<>();

        // Loop through each part and split it if we find the placeholder
        for (MessagePart temp : parts) {
            if (!temp.getText().contains(placeholder)) {
                // This part does not contain the placeholder, so just add it
                retParts.add(temp);
                continue;
            }

            //          Case 1.1: Placeholder is the entire part
            if (temp.getText().equals(placeholder)) {
                // No adjustments needed, just add the action
                MessagePart part = temp.splitWithNewText(replacement);
                injectAction(part, action);
                retParts.add(part);
                continue;
            }

            //          Case 1.2: Placeholder is at the beginning of the part
            if (temp.getText().startsWith(placeholder)) {
                // Create a part to reflect the placeholder
                MessagePart placeholderPart = temp.splitWithNewText(replacement);
                // Add the action to the placeholder part
                injectAction(placeholderPart, action);
                // Add the placeholder part
                retParts.add(placeholderPart);

                // Create a part to reflect the rest of the text
                MessagePart restPart = temp.splitWithNewText(temp.getText().substring(placeholder.length()));
                // Add the rest of the text
                retParts.add(restPart);
                continue;
            }

            //          Case 1.3: Placeholder is at the end of the part
            if (temp.getText().endsWith(placeholder)) {
                // Create a part to reflect the rest of the text
                MessagePart restPart = temp.splitWithNewText(temp.getText().substring(0, temp.getText().length() - placeholder.length()));
                // Add the rest of the text
                retParts.add(restPart);

                // Create a part to reflect the placeholder
                MessagePart placeholderPart = temp.splitWithNewText(replacement);
                // Add the action to the placeholder part
                injectAction(placeholderPart, action);
                // Add the placeholder part
                retParts.add(placeholderPart);
                continue;
            }

            //          Case 1.4: Placeholder is in the middle of the part
            // Create a part to reflect the beginning of the text
            MessagePart beginningPart = temp.splitWithNewText(temp.getText().substring(0, temp.getText().indexOf(placeholder)));
            // Add the beginning of the text
            retParts.add(beginningPart);

            // Create a part to reflect the placeholder
            MessagePart placeholderPart = temp.splitWithNewText(replacement);
            // Add the action to the placeholder part
            injectAction(placeholderPart, action);
            // Add the placeholder part
            retParts.add(placeholderPart);

            // Create a part to reflect the end of the text
            MessagePart endPart = temp.splitWithNewText(temp.getText().substring(temp.getText().indexOf(placeholder) + placeholder.length()));
            // Add the end of the text
            retParts.add(endPart);
        }

        return retParts;
    }

    private static void injectAction(MessagePart part, StandaloneAction action) {
        if (action.getClick() != null) {
            part.setClick(action.getClick());
        }
        if (action.getHover() != null) {
            part.setHover(action.getHover());
        }
    }
}
