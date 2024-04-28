package com.kamikazejam.kamicommon.util.chat;

import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Click;
import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Hover;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
@SuppressWarnings("unused")
public class MessagePart {
    private String text;
    private @Nullable Click click = null;
    private @Nullable Hover hover = null;

    // Creates an empty MessagePart, with no hover text and no click events
    public MessagePart(String text) {
        this.text = text;
    }

    public MessagePart splitWithNewText(String newText) {
        // Create a new part and copy the actions to it
        MessagePart newPart = new MessagePart(newText);
        newPart.setHover(this.hover);
        newPart.setClick(this.click);
        return newPart;
    }
}
