package com.kamikazejam.kamicommon.util.pager;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.mson.Mson;
import com.kamikazejam.kamicommon.util.mson.MsonMessenger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
@SuppressWarnings("unused")
public class Pager<T> {
	// -------------------------------------------- //
	// DATA SUPPLY
	// -------------------------------------------- //

	// The command to use for back and forward buttons.
	protected KamiCommand command = null;

	public boolean hasCommand() {
		return this.command != null;
	}

	public Pager<T> setCommand(KamiCommand command) {
		this.command = command;
		return this;
	}

	// The CommandSender for fallback height.
	protected CommandSender sender = null;

	public boolean hasSender() {
		return this.sender != null;
	}

	public Pager<T> setSender(CommandSender sender) {
		this.sender = sender;
		return this;
	}

	public CommandSender getSenderCalc() {
		CommandSender ret = this.getSender();
		if (ret != null) return ret;

		KamiCommand command = this.getCommand();
		if (command != null) return command.sender;

		return null;
	}

	// The args to use for back and forward buttons.
	protected List<String> args = null;

	public boolean hasArgs() {
		return this.args != null;
	}

	public Pager<T> setArgs(List<String> args) {
		this.args = args;
		return this;
	}

	public Pager<T> setArgs(String @NotNull ... args) {
		this.setArgs(Arrays.asList(args));
		return this;
	}

	public List<String> getArgsCalc() {
		List<String> ret = this.getArgs();
		if (ret != null) return ret;

		KamiCommand command = this.getCommand();
		if (command != null) return new ArrayList<>(command.getArgs());

		return null;
	}

	// The page height. The asmount of items per page.
	protected Integer height = null;

	public boolean hasHeight() {
		return this.height != null;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public Pager<T> setHeight(Integer height) {
		this.height = height;
		return this;
	}

	public Integer getHeightCalc() {
		Integer ret = this.getHeight();
		if (ret != null) return ret;

		CommandSender sender = this.getSenderCalc();
		if (sender == null) return Txt.PAGEHEIGHT_PLAYER;
		if (sender instanceof Player) return Txt.PAGEHEIGHT_PLAYER;

		return Txt.PAGEHEIGHT_CONSOLE;
	}

	// The title to use at the top of the page.
	protected String title = null;

	public boolean hasTitle() {
		return this.title != null;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public Pager<T> setTitle(String title) {
		this.title = title;
		return this;
	}

	// The page number we want to show.
	protected Integer number = null;

	public boolean hasNumber() {
		return this.number != null;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public Pager<T> setNumber(Integer number) {
		this.number = number;
		return this;
	}

	// The items we are paging.
	protected Collection<? extends T> items = null;

	public boolean hasItems() {
		return this.items != null;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public Pager<T> setItems(Collection<? extends T> items) {
		this.items = items;
		return this;
	}

	// The method of converting from item to Mson.
	protected Msonifier<T> msonifier = null;

	public boolean hasMsonifier() {
		return this.msonifier != null;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public Pager<T> setMsonifier(Msonifier<T> msonifier) {
		this.msonifier = msonifier;
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public Pager<T> setMsonifier(final Stringifier<T> stringifier) {
		this.msonifier = (item, index) -> Mson.fromParsedMessage(stringifier.toString(item, index));
		return this;
	}

	// -------------------------------------------- //
	// CALC
	// -------------------------------------------- //

	public void calc() {
		this.setSender(this.getSenderCalc());
		this.setArgs(this.getArgsCalc());
		this.setHeight(this.getHeightCalc());
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public Pager() {

	}

	public Pager(KamiCommand command, String title, Integer number, Stringifier<T> stringifier) {
		this(command, title, number, null, stringifier);
	}

	public Pager(KamiCommand command, String title, Integer number, Collection<? extends T> items, Stringifier<T> stringifier) {
		this(command, title, number, items);
		this.setMsonifier(stringifier);
	}

	public Pager(KamiCommand command, String title, Integer number) {
		this(command, title, number, (Collection<? extends T>) null);
	}

	public Pager(KamiCommand command, String title, Integer number, Collection<? extends T> items) {
		this(command, title, number, items, (Msonifier<T>) null);
	}

	public Pager(KamiCommand command, String title, Integer number, Msonifier<T> msonifier) {
		this(command, title, number, null, msonifier);
	}

	public Pager(KamiCommand command, String title, Integer number, Collection<? extends T> items, Msonifier<T> msonifier) {
		this.command = command;
		this.title = title;
		this.number = number;
		this.items = items;
		this.msonifier = msonifier;
		this.calc();
	}

	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //

	public int size() {
		return (int) Math.ceil((double) this.getItems().size() / this.getHeight());
	}

	public boolean isValid(int number) {
		if (this.isEmpty()) return false;
		if (number < 1) return false;
        return number <= this.size();
    }

	public boolean isEmpty() {
		return this.getItems().isEmpty();
	}

	@SuppressWarnings("unchecked")
	public List<T> getPage(int number) {
		// Return null if the page number is invalid
		if (!this.isValid(number)) return null;

		// Forge list from collection
		List<T> items;
		if (this.getItems() instanceof List) {
			items = (List<T>) this.getItems();
		} else {
			items = new ArrayList<>(this.getItems());
		}

		int index = number - 1;

		// Calculate from and to
		int from = index * this.getHeight();
		int to = from + this.getHeight();
		if (to > items.size()) {
			to = items.size();
		}

		// Pick them
		return items.subList(from, to);
	}

	// -------------------------------------------- //
	// GET
	// -------------------------------------------- //

	public List<Mson> get() {
		// Create ret
		List<Mson> ret = new ArrayList<>();

		// Add title
		ret.add(Txt.titleizeMson(this.getTitle(), this.size(), this.getNumber(), this.getCommand(), this.getArgs()));

		// Check empty
		if (this.isEmpty()) {
			ret.add(Txt.getMessageEmpty());
			return ret;
		}

		// Get items
		List<T> pageItems = this.getPage(this.getNumber());

		// Check invalid
		if (pageItems == null) {
			ret.add(Txt.getMessageInvalid(this.size()));
			return ret;
		}

		// Add items
		int index = (this.getNumber() - 1) * this.getHeight();
		for (T pageItem : pageItems) {
			ret.add(this.getMsonifier().toMson(pageItem, index));
			index++;
		}

		// Return ret
		return ret;
	}

	// -------------------------------------------- //
	// MESSAGE
	// -------------------------------------------- //

	public void message() {
		// Get
		List<Mson> messages = this.get();

		// Message
		MsonMessenger.get().messageOne(this.getSender(), messages);
	}

	public void messageAsync() {
		Bukkit.getScheduler().runTaskAsynchronously(PluginSource.get(), this::message);
	}

}
