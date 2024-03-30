package com.kamikazejam.kamicommon.util.mson;

import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class MsonMessenger {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final MsonMessenger d = new MsonMessenger();
	@SuppressWarnings("FieldMayBeFinal")
	private static MsonMessenger i = d;

	@Contract(pure = true)
	public static MsonMessenger get() {
		return i;
	}

	// -------------------------------------------- //
	// MSG > ALL
	// -------------------------------------------- //

	public boolean msgAll(String msg) {
		return this.messageAll(Txt.parse(msg));
	}

	public boolean msgAll(String msg, Object... args) {
		return this.messageAll(Txt.parse(msg, args));
	}

	public boolean msgAll(Collection<String> msgs) {
		return this.messageAll(Txt.parse(msgs));
	}

	// -------------------------------------------- //
	// MSG > PREDICATE
	// -------------------------------------------- //

	public boolean msgPredicate(Predicate<CommandSender> predicate, String msg) {
		return this.messagePredicate(predicate, Txt.parse(msg));
	}

	public boolean msgPredicate(Predicate<CommandSender> predicate, String msg, Object... args) {
		return this.messagePredicate(predicate, Txt.parse(msg, args));
	}

	public boolean msgPredicate(Predicate<CommandSender> predicate, Collection<String> msgs) {
		return this.messagePredicate(predicate, Txt.parse(msgs));
	}

	// -------------------------------------------- //
	// MSG > ONE
	// -------------------------------------------- //

	public boolean msgOne(Object sendeeObject, String msg) {
		return this.messageOne(sendeeObject, Txt.parse(msg));
	}

	public boolean msgOne(Object sendeeObject, String msg, Object... args) {
		return this.messageOne(sendeeObject, Txt.parse(msg, args));
	}

	public boolean msgOne(Object sendeeObject, Collection<String> msgs) {
		return this.messageOne(sendeeObject, Txt.parse(msgs));
	}

	// -------------------------------------------- //
	// MESSAGE > ALL
	// -------------------------------------------- //

	public boolean messageAll(Object message) {
		return this.messageAll(asCollection(message));
	}

	public boolean messageAll(Object... messages) {
		return this.messageAll(asCollection(messages));
	}

	public boolean messageAll(Collection<?> messages) {
		// Check Messages
		if (messages == null) return false;
		if (messages.isEmpty()) return false;

		// Here
		for (CommandSender sender : KUtil.getLocalSenders()) {
			this.messageOne(sender, messages);
		}

		// Return
		return true;
	}

	// -------------------------------------------- //
	// MESSAGE > PREDICATE
	// -------------------------------------------- //

	public boolean messagePredicate(Predicate<CommandSender> predicate, Object message) {
		return this.messagePredicate(predicate, asCollection(message));
	}

	public boolean messagePredicate(Predicate<CommandSender> predicate, Object... messages) {
		return this.messagePredicate(predicate, asCollection(messages));
	}

	public boolean messagePredicate(Predicate<CommandSender> predicate, Collection<?> messages) {
		// Check Predicate
		if (predicate == null) return false;

		// Check Messages
		if (messages == null) return false;
		if (messages.isEmpty()) return false;

		// Here
		for (CommandSender sender : KUtil.getLocalSenders()) {
			if (!predicate.apply(sender)) continue;
			this.messageOne(sender, messages);
		}

		// Return
		return true;
	}

	// -------------------------------------------- //
	// MESSAGE > ONE
	// -------------------------------------------- //

	public boolean messageOne(Object sendeeObject, Object message) {
		return this.messageOne(sendeeObject, asCollection(message));
	}

	public boolean messageOne(Object sendeeObject, Object... messages) {
		return this.messageOne(sendeeObject, asCollection(messages));
	}

	public boolean messageOne(Object sendeeObject, Collection<?> messages) {
		// Check Sendee
		CommandSender sendee = KUtil.getSender(sendeeObject);
		if (sendee == null) { return false; }

		// Check Messages
		if (messages == null) return false;
		if (messages.isEmpty()) return false;

		// For each Message
		for (Object message : messages) {
			if (message instanceof String) {
				// Send plain string
				sendee.sendMessage(StringUtil.t((String) message));
			} else if (message instanceof Mson) {
				sendee.sendMessage(StringUtil.t(((Mson) message).toPlain(true)));
			} else {
				String desc = (message == null ? "null" : message.getClass().getSimpleName());
				throw new IllegalArgumentException(desc + " is neither String nor Mson.");
			}
		}
		return true;
	}

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	public Collection<?> asCollection(Object message) {
		if (message instanceof Collection) return (Collection<?>) message;
		if (message instanceof Object[]) return Arrays.asList((Object[]) message);
		return Collections.singleton(message);
	}

}
