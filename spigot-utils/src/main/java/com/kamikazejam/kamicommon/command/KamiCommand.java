package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.requirement.Requirement;
import com.kamikazejam.kamicommon.command.requirement.RequirementAbstract;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.command.type.enumeration.TypeEnum;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.interfaces.Active;
import com.kamikazejam.kamicommon.util.mson.Mson;
import com.kamikazejam.kamicommon.util.mson.MsonMessenger;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import com.kamikazejam.kamicommon.util.predicate.PredicateLevenshteinClose;
import com.kamikazejam.kamicommon.util.predicate.PredicateStartsWithIgnoreCase;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "unused"})
public class KamiCommand implements Active, PluginIdentifiableCommand {
	private static final Set<KamiCommand> allInstances = new KamiSet<>();

	@Contract(pure = true)
	public static Set<KamiCommand> getAllInstances() {
		return allInstances;
	}

	// -------------------------------------------- //
	// REGISTRATION
	// -------------------------------------------- //

	/**
	 * If you register a command after the server has started, you must call the following method: <p>
	 * @see KamiCommonCommandRegistration#updateRegistrations() <p>
	 * In order for the command to be added to the server.
	 */
	public void registerCommand(KamiPlugin plugin) {
		this.setActive(plugin);
	}

	/**
	 * If you unregister a command after the server has started, you must call the following method: <p>
	 * @see KamiCommonCommandRegistration#updateRegistrations() <p>
	 * In order for the command to be removed from the server.
	 */
	public void unregisterCommand() {
		this.setActive(false);
	}


	// -------------------------------------------- //
	// ACTIVE
	// -------------------------------------------- //

	@Override
	public boolean isActive() {
		return getAllInstances().contains(this);
	}

	@Override
	public void setActive(boolean active) {
		// Validate
		this.validateActiveAndRoot(active, null);

		// Apply
		if (active) {
			getAllInstances().add(this);
		} else {
			getAllInstances().remove(this);
		}
	}

	private KamiPlugin activePlugin = null;

	@Override
	public void setActivePlugin(KamiPlugin activePlugin) {
		this.activePlugin = activePlugin;
	}

	@Override
	public KamiPlugin getActivePlugin() {
		// Automatically fetch from parent if null.
		if (this.activePlugin == null) {
			if (parent != null && !Objects.equals(parent, this)) {
				this.activePlugin = this.getParent().getActivePlugin();
			}else {
				throw new RuntimeException("No active plugin set for command " + this.getClass().getSimpleName());
			}
		}
		return this.activePlugin;
	}

	@Override
	public void setActive(KamiPlugin plugin) {
		this.setActivePlugin(plugin);
		this.setActive(plugin != null);
	}

	public void validateActiveAndRoot(Boolean active, Boolean root) {
		if (active == null) active = this.isActive();
		if (root == null) root = this.isRoot();
		if (active && !root) throw new IllegalStateException("only root commands can be active");
	}

	// -------------------------------------------- //
	// PLUGIN IDENTIFIABLE COMMAND
	// -------------------------------------------- //

	@Override
	public @NotNull Plugin getPlugin() {
		return this.getActivePlugin();
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	// Due to the large amount of methods in this class we place the fields alone here in the beginning.
	// Field access and other similar utility methods have their special sections below. 

	// === HIERARCHY ===

	// The parent command.
	@Getter
	protected KamiCommand parent = null;

	// The child commands.
	@Getter
	protected List<KamiCommand> children = Collections.emptyList();

	// === ALIASES ===

	// The different names this commands will react to  
	@Getter
	protected List<String> aliases = new KamiList<>();

	// === PARAMETERS ===

	// The command parameters.
	@Getter
	protected List<Parameter<?>> parameters = new KamiList<>();

	// === PREPROCESS ===

	// Should the arguments be parsed considering quotes and backslashes and such?
	@Getter
	protected boolean tokenizing = true;

	// Are "smart" quotes replaced with normal characters?
	@Getter
	protected boolean unsmart = true;

	// === PUZZLER ===

	// Should an error be thrown if "too many" arguments are provided?
	@Getter
	protected boolean overflowSensitive = true;

	// Should the last parameter concatenate all surplus arguments?
	@Getter
	protected boolean concatenating = false;

	// Should we try to automatically swap the arguments around if they were typed in invalid order?
	@Getter
	protected boolean swapping = true;

	// === REQUIREMENTS ===

	// All these requirements must be met for the command to be executable;
	@Getter
	protected List<Requirement> requirements = new KamiList<>();

	// === HELP ===

	// A short description of what the command does. Such as "eat hamburgers" or "do admin stuff".
	protected String desc = null;

	// A specific permission node to use for description if desc is null.
	protected String descPermission = null;

	// Free text displayed at the top of the help command.
	@Getter
	protected List<?> help = new ArrayList<>();

	// The visibility of this command in help command.
	@Getter
	protected Visibility visibility = Visibility.VISIBLE;

	// The priority of this command when aliases are ambiguous.
	@Getter
	protected long priority = 0;

	// === EXECUTION ===

	// The raw string arguments passed upon execution. An empty list if there are none.
	@Getter
	protected List<String> args = new KamiList<>();

	// The index of the next arg to read.
	public int nextArg = 0;

	// ...
	public CommandSender sender = null;

	// ...
	public Player me = null;

	// ...
	public boolean senderIsConsole = false;

	// -------------------------------------------- //
	// HIERARCHY
	// -------------------------------------------- //

	public boolean hasParent() {
		return this.getParent() != null;
	}

	public boolean isChild() {
		return this.hasParent();
	}

	public boolean isRoot() {
		return !this.hasParent();
	}

	public boolean hasChildren() {
		return !this.getChildren().isEmpty();
	}

	public boolean isParent() {
		return this.hasChildren();
	}

	public boolean isLeaf() {
		return !this.hasChildren();
	}

	public List<KamiCommand> getVisibleChildren(CommandSender watcher) {
		// Create
		List<KamiCommand> ret = new KamiList<>();

		// Fill
		for (KamiCommand child : this.getChildren()) {
			if (child.isVisibleTo(watcher)) ret.add(child);
		}

		// Return
		return ret;
	}

	public KamiCommand getRoot() {
		// Create
		KamiCommand ret = this;

		// Fill
		while (ret.hasParent()) {
			ret = ret.getParent();
		}

		// Return
		return ret;
	}

	// The parents is like a stack trace.
	// We start with ourselves. The root is at the end.
	public List<KamiCommand> getParents(boolean includeSelf) {
		// Create
		List<KamiCommand> ret = new KamiList<>();

		// Fill
		if (includeSelf) ret.add(this);
		KamiCommand parent = this.getParent();
		while (parent != null) {
			ret.add(parent);
			parent = parent.getParent();
		}

		// Return
		return ret;
	}

	// The chain is the parents in reversed order.
	public List<KamiCommand> getChain(boolean includeSelf) {
		List<KamiCommand> ret = this.getParents(includeSelf);
		Collections.reverse(ret);
		return ret;
	}

	public void removeParent() {
		// NoChange
		if (!this.hasParent()) return;

		// Apply
		KamiCommand parent = this.getParent();
		parent.removeChild(this);
		this.parent = null;
	}

	@Contract(mutates = "this,param1")
	public void setParent(KamiCommand parent) {
		// NoChange
		if (KUtil.equals(this.getParent(), parent)) return;

		// Remove
		this.removeParent();

		// NoSet
		if (parent == null) return;

		// Validate
		this.validateActiveAndRoot(null, false);

		// Apply
		this.parent = parent;
		parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T addChild(KamiCommand child) {
		// NoChange
		if (this.getChildren().contains(child)) return (T) this;

		// Apply
		return this.addChild(child, this.getChildren().size());
	}

	@Contract(value = "_, _ -> this", mutates = "this")
	public <T extends KamiCommand> T addChildAfter(KamiCommand child, KamiCommand after) {
		int index = this.getChildren().indexOf(after);
		if (index == -1) {
			index = this.getChildren().size();
		} else {
			index++;
		}
		return this.addChild(child, index);
	}

	public int replaceChild(KamiCommand child, KamiCommand replaced) {
		int index = this.removeChild(replaced);
		if (index < 0) return index;
		this.addChild(child, index);
		return index;
	}

	public int removeChild(KamiCommand child) {
		List<KamiCommand> children = new KamiList<>(this.getChildren());
		int index = children.indexOf(child);
		if (index == -1) return -1;
		children.remove(index);
		this.children = Collections.unmodifiableList(children);
		child.removeParent();
		return index;
	}

	@SuppressWarnings("unchecked")
	public <T extends KamiCommand> T addChild(KamiCommand child, int index) {
		if (!this.hasChildren() && !(child instanceof KamiCommandHelp)) {
			this.getHelpCommand();
			index++;
		}

		List<KamiCommand> children = new KamiList<>(this.getChildren());
		children.add(index, child);
		this.children = Collections.unmodifiableList(children);
		child.setParent(this);

		return (T) this;
	}

	public KamiCommandHelp getHelpCommand() {
		if (!this.hasChildren()) this.addChild(new KamiCommandHelp(), 0);
		List<KamiCommand> children = this.getChildren();
		return (KamiCommandHelp) children.get(0);
	}

	// -------------------------------------------- //
	// CHILDREN > GET
	// -------------------------------------------- //

	// The full version of the child matcher method.
	// Returns a set of child commands with similar aliases.
	//
	// token - the full alias or an alias prefix.
	// onlyRelevantToSender - if non null only returns commands relevant to specific sender
	// prioritize - only return commands with the highest priority

	// 
	// An empty set means no child was found.
	// A single element set means we found an unambiguous match.
	// A larger set means the token was ambiguous.
	private Set<KamiCommand> getChildren(String token, boolean levenshtein, CommandSender onlyRelevantToSender, boolean prioritize) {
		// Create Ret
		Set<KamiCommand> ret = new KamiSet<>();

		// Prepare
		token = token.toLowerCase();
		Predicate<String> predicate = levenshtein ? PredicateLevenshteinClose.get(token) : PredicateStartsWithIgnoreCase.get(token);

		// Fill Ret
		// Go through each child command
		for (KamiCommand child : this.getChildren()) {
			// See if any of the aliases has a match or close enough
			// If there is a direct match, return that
			for (String alias : child.getAliases()) {
				// ... consider exact priority ...
				if (alias.equalsIgnoreCase(token)) {
					return Collections.singleton(child);
				}

				if (ret.contains(child)) continue;

				// ... matches ...
				if (!predicate.apply(alias)) continue;

				// ... and put in ret.
				ret.add(child);
			}
		}

		// Only Relevant
		if (onlyRelevantToSender != null) ret = getRelevantCommands(ret, onlyRelevantToSender);

		// Priority
		if (prioritize) ret = getPrioritizedCommands(ret);

		// Return Ret
		return ret;
	}

	private static @NotNull Set<KamiCommand> getRelevantCommands(@NotNull Iterable<@NotNull KamiCommand> commands, CommandSender sender) {
		Set<KamiCommand> ret = new KamiSet<>();
		for (KamiCommand command : commands) {
			if (!command.isRelevant(sender)) continue;
			ret.add(command);
		}
		return ret;
	}

	private static @NotNull Set<KamiCommand> getPrioritizedCommands(@NotNull Iterable<@NotNull KamiCommand> commands) {
		Set<KamiCommand> ret = new KamiSet<>();
		long highestPriority = Long.MIN_VALUE;

		for (KamiCommand command : commands) {
			long priority = command.getPriority();
			if (priority < highestPriority) continue;

			if (priority > highestPriority) {
				ret.clear();
				highestPriority = priority;
			}
			ret.add(command);
		}
		return ret;
	}

	// A simplified version returning null on ambiguity and nothing found.
	public KamiCommand getChild(String token) {
		Set<KamiCommand> children = this.getChildren(token, false, null, true);

		if (children.isEmpty()) return null;
		if (children.size() > 1) return null;
		return children.iterator().next();
	}

	protected boolean isRelevant(CommandSender sender) {
		if (sender == null) return true;

		if (!this.isVisibleTo(sender)) return false;
		return this.isRequirementsMet(sender, false);
	}

	// -------------------------------------------- //
	// ALIASES
	// -------------------------------------------- //

	@SuppressWarnings("unchecked")
	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T setAliases(Collection<String> aliases) {
		this.aliases = new KamiList<>(aliases);
		return (T) this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T setAliases(String @NotNull ... aliases) {
		return this.setAliases(Arrays.asList(aliases));
	}

	@SuppressWarnings("unchecked")
	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T addAliases(Collection<String> aliases) {
		this.aliases.addAll(aliases);
		return (T) this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T addAliases(String @NotNull ... aliases) {
		return this.addAliases(Arrays.asList(aliases));
	}

	// -------------------------------------------- //
	// PARAMETERS
	// -------------------------------------------- //

	public void setParameters(List<Parameter<?>> parameters) {
		this.parameters = parameters;
	}

	public Parameter<?> getParameter(int index) {
		if (this.isConcatenating() && this.getConcatenationIndex() < index) index = this.getConcatenationIndex();
		return this.getParameters().get(index);
	}

	public Type<?> getParameterType(int index) {
		Parameter<?> parameter = this.getParameter(index);
		return parameter.getType();
	}

	public void setParameter(int index, Parameter<?> parameter) {
		if (this.isConcatenating() && this.getConcatenationIndex() < index) index = this.getConcatenationIndex();
		this.getParameters().set(index, parameter);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void setParameterType(int index, Type<?> type) {
		this.getParameter(index).setType((Type) type);
	}

	public boolean hasParameterForIndex(int index) {
		if (index < 0) return false;
		if (this.isConcatenating() && this.getConcatenationIndex() < index) index = this.getConcatenationIndex();
		return this.getParameters().size() > index;
	}

	public int getPageParameterIndex() {
		int pageParamIndex = 0;
		for (Parameter<?> param : this.getParameters()) {
			if (param.getName().equals("page")) return pageParamIndex;
			pageParamIndex++;
		}
		return -1;
	}

	// -------------------------------------------- //
	// PARAMETERS > COUNT
	// -------------------------------------------- //

	public int getParameterCount(CommandSender sender) {
		return this.getParameterCountRequired(sender) + this.getParameterCountOptional(sender);
	}

	public int getParameterCountRequired(CommandSender sender) {
		int ret = 0;

		for (Parameter<?> parameter : this.getParameters()) {
			if (parameter.isRequiredFor(sender)) ret++;
		}

		return ret;
	}

	public int getParameterCountOptional(CommandSender sender) {
		int ret = 0;

		for (Parameter<?> parameter : this.getParameters()) {
			if (parameter.isOptionalFor(sender)) ret++;
		}

		return ret;
	}

	// -------------------------------------------- //
	// PARAMETERS > ADD
	// -------------------------------------------- //

	// The actual parameter.
	@Contract(value = "_, _ -> param1", mutates = "this")
	public <T> Parameter<T> addParameter(Parameter<T> parameter, boolean concatFromHere) {
		// Concat safety.
		if (this.isConcatenating()) {
			throw new IllegalStateException("You can't add args if a prior one concatenates.");
		}

		// Req/optional safety.
		int prior = this.getParameters().size() - 1;
		if (this.hasParameterForIndex(prior) && this.getParameter(prior).isOptional() && parameter.isRequired()) {
			throw new IllegalArgumentException("You can't add required args, if a prior one is optional.");
		}

		// If false no change is made.
		// If true change is made.
		this.setConcatenating(concatFromHere);

		this.getParameters().add(parameter);
		return parameter;
	}

	// The actual parameter without concat.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Parameter<T> parameter) {
		return this.addParameter(parameter, false);
	}

	// All
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, boolean requiredFromConsole, String name, String defaultDesc, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(defaultValue, type, requiredFromConsole, name, defaultDesc), concatFromHere);
	}

	// WITHOUT 1

	// Without defaultValue
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, boolean requiredFromConsole, String name, String defaultDesc, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(type, requiredFromConsole, name, defaultDesc), concatFromHere);
	}

	// Without reqFromConsole.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, String name, String defaultDesc, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(defaultValue, type, name, defaultDesc), concatFromHere);
	}

	// Without defaultDesc.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, boolean requiredFromConsole, String name, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(defaultValue, type, requiredFromConsole, name), concatFromHere);
	}

	// Without concat.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, boolean requiredFromConsole, String name, String defaultDesc) {
		return this.addParameter(new Parameter<>(defaultValue, type, requiredFromConsole, name, defaultDesc), false);
	}

	// WITHOUT 2

	// Without defaultValue & reqFromConsole
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, String name, String defaultDesc, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(type, name, defaultDesc), concatFromHere);
	}

	// Without defaultValue & defaultDesc
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, boolean requiredFromConsole, String name, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(type, requiredFromConsole, name), concatFromHere);
	}

	// Without defaultValue & concat.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, boolean requiredFromConsole, String name, String defaultDesc) {
		return this.addParameter(new Parameter<>(type, requiredFromConsole, name, defaultDesc));
	}

	// Without reqFromConsole & defaultDesc.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, String name, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(defaultValue, type, name), concatFromHere);
	}

	// Without reqFromConsole & concat.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, String name, String defaultDesc) {
		return this.addParameter(new Parameter<>(defaultValue, type, name, defaultDesc));
	}

	// Without defaultDesc & concat.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, boolean requiredFromConsole, String name) {
		return this.addParameter(new Parameter<>(defaultValue, type, requiredFromConsole, name));
	}

	// WITHOUT 3

	// Without defaultValue, reqFromConsole & defaultDesc.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, String name, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(type, name), concatFromHere);
	}

	// Without defaultValue, reqFromConsole & concat .
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, String name, String defaultDesc) {
		return this.addParameter(new Parameter<>(type, name, defaultDesc));
	}

	// Without defaultValue, defaultDesc & concat .
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, boolean requiredFromConsole, String name) {
		return this.addParameter(new Parameter<>(type, requiredFromConsole, name));
	}

	// Without reqFromConsole, defaultDesc & concat .
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(T defaultValue, Type<T> type, String name) {
		return this.addParameter(new Parameter<>(defaultValue, type, name));
	}

	// WITHOUT 4

	// Without defaultValue, reqFromConsole, defaultDesc & concat.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, String name) {
		return this.addParameter(new Parameter<>(type, name));
	}

	// Without defaultValue, name, reqFromConsole & defaultDesc.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type, boolean concatFromHere) {
		return this.addParameter(new Parameter<>(type), concatFromHere);
	}

	// Without 5

	// Without defaultValue, name, reqFromConsole, defaultDesc & concat.
	@Contract(mutates = "this")
	public <T> Parameter<T> addParameter(Type<T> type) {
		return this.addParameter(new Parameter<>(type));
	}

	// -------------------------------------------- //
	// PREPROCESS
	// -------------------------------------------- //
	// These options are applied very early.
	// The code is located in the KamiCommonBukkitCommand.

	@Contract(mutates = "this")
	public void setTokenizing(boolean tokenizing) {
		this.tokenizing = tokenizing;
	}

	@Contract(mutates = "this")
	public void setUnsmart(boolean unsmart) {
		this.unsmart = unsmart;
	}

	// -------------------------------------------- //
	// PUZZLER
	// -------------------------------------------- //
	// At the puzzler phase of execution we massage the raw arguments.
	// The end result is still raw arguments but their order and composition is better adapted to our parameters.

	@Contract(mutates = "this")
	public void setOverflowSensitive(boolean overflowSensitive) {
		this.overflowSensitive = overflowSensitive;
	}

	@Contract(mutates = "this")
	public void setConcatenating(boolean concatenating) {
		this.concatenating = concatenating;
	}

	public int getConcatenationIndex() {
		return this.getParameters().size() - 1;
	}

	@Contract(mutates = "this")
	public void setSwapping(boolean swapping) {
		this.swapping = swapping;
	}

	// -------------------------------------------- //
	// PUZZLER > APPLY
	// -------------------------------------------- //

	public List<String> applyPuzzler(List<String> args, CommandSender sender) {
		args = this.applyConcatenating(args);
		args = this.applySwapping(args, sender);
		return new ArrayList<>(args);
	}

	public List<String> applyConcatenating(List<String> args) {
		// Really?
		if (!this.isConcatenating()) return args;

		// Create Ret
		List<String> ret = new KamiList<>();

		// Fill Ret
		final int maxIdx = Math.min(this.getConcatenationIndex(), args.size());
		ret.addAll(args.subList(0, maxIdx)); // The args that should not be concatenated.
		if (args.size() > maxIdx) {
			ret.add(Txt.implode(args.subList(maxIdx, args.size()), " "));
		}

		// Return Ret
		return ret;
	}

	public List<String> applySwapping(List<String> args, CommandSender sender) {
		// Really?
		if (!this.isSwapping()) return args;

		// So if there is too many, or too few args. We can't do much here.
		if (!this.isArgsValid(args)) return args;

		String[] ret = new String[this.getParameters().size()];

		args:
		for (String arg : args) {
			for (int i = 0; i < this.getParameters().size(); i++) {
				Type<?> type = this.getParameterType(i);

				if (ret[i] != null) continue; // If that index is already filled.

				// We do in fact want to allow null args.
				// Those are used by us in some special circumstances.
				if (arg != null && !type.isValid(arg, sender)) continue; // If this arg isn't valid for that index.

				ret[i] = arg;
				continue args; // That arg is now set :)
			}
			// We will only end up here if an arg didn't fit any of the types.
			// In that case we failed.
			return args;
		}

		// Ensure that the required args are filled.
		for (int i = 0; i < this.getParameterCountRequired(sender); i++) {
			if (ret[i] != null) continue;
			// We end up here if an required arg wasn't filled. In that case we failed.
			return args;
		}

		return Arrays.asList(ret);
	}

	// -------------------------------------------- //
	// REQUIREMENTS
	// -------------------------------------------- //

	@SuppressWarnings("unchecked")
	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T addRequirements(Collection<Requirement> requirements) {
		this.requirements.addAll(requirements);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Contract(value = "_ -> this", mutates = "this")
	public <T extends KamiCommand> T addRequirements(Requirement @NotNull ... requirements) {
		this.addRequirements(Arrays.asList(requirements));
		return (T) this;
	}

	public boolean isRequirementsMet(CommandSender sender, boolean verbose) {
		return RequirementAbstract.isRequirementsMet(this.getRequirements(), sender, this, verbose);
	}

	public String getRequirementsError(CommandSender sender, boolean verbose) {
		return RequirementAbstract.getRequirementsError(this.getRequirements(), sender, this, verbose);
	}

	// -------------------------------------------- //
	// HELP
	// -------------------------------------------- //

	@Contract(mutates = "this")
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		if (this.desc != null) {
			return this.desc;
		}
		// Return empty string as desc
		return "";
	}

	public void setDescPermission(String descPermission) {
		this.descPermission = descPermission;
	}

	public String getDescPermission() {
		if (this.descPermission != null) return this.descPermission;
		// Otherwise we try to find one.
		for (Requirement requirement : this.getRequirements()) {
			if (!(requirement instanceof RequirementHasPerm)) continue;
			return ((RequirementHasPerm) requirement).getPermissionId();
		}
		return null;
	}

	public void setHelp(List<?> val) {
		this.help = val;
	}

	public void setHelp(Object @NotNull ... val) {
		this.help = Arrays.asList(val);
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public boolean isVisibleTo(CommandSender sender) {
		if (this.getVisibility() == Visibility.VISIBLE) return true;
		if (this.getVisibility() == Visibility.INVISIBLE) return false;
		return this.isRequirementsMet(sender, false);
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	// -------------------------------------------- //
	// PERMISSIONS
	// -------------------------------------------- //

	protected static <T extends Enum<T>> @Nullable T getPerm(String permName, boolean lenient, Class<T> permClass) {
		permName = getPermCompareString(permName, lenient);
		for (T perm : TypeEnum.getEnumValues(permClass)) {
			String compare = getPermCompareString(perm.name(), lenient);
			if (compare.equals(permName)) return perm;
		}
		return null;
	}

	@Contract("_, false -> param1")
	protected static @NotNull String getPermCompareString(@NotNull String permName, boolean lenient) {
		if (lenient) {
			permName = permName.toUpperCase();
			permName = permName.replace("_", "");
		}
		return permName;
	}

	// -------------------------------------------- //
	// EXECUTION
	// -------------------------------------------- //

	public void setArgs(List<String> args) {
		this.args = args;
	}

	// -------------------------------------------- //
	// EXECUTOR
	// -------------------------------------------- //

	public void execute(CommandSender sender, List<String> args) {
		try {
			// Sender Field - Setup
			this.senderFieldsOuter(sender);

			// Apply Puzzler
			args = this.applyPuzzler(args, sender);
			this.setArgs(args);

			// Requirements
			if (!this.isRequirementsMet(sender, true)) return;

			// Child Execution
			if (this.isParent() && !args.isEmpty()) {
				// Get matches
				String token = args.get(0);

				Set<KamiCommand> matches = this.getChildren(token, false, null, true);

				// Score!
				if (matches.size() == 1) {
					KamiCommand child = matches.iterator().next();
					args.remove(0);
					child.execute(sender, args);
				}
				// Crap!
				else {
					Mson base;
					Collection<KamiCommand> suggestions;

					if (matches.isEmpty()) {
						base = Lang.COMMAND_CHILD_NONE;
						suggestions = this.getChildren(token, true, sender, false);
						onUnmatchedArg();
					} else {
						base = Lang.COMMAND_CHILD_AMBIGUOUS;
						suggestions = this.getChildren(token, false, sender, false);
					}

					// Message: "The sub command X couldn't be found."
					// OR
					// Message: "The sub command X is ambiguous."
					Mson blueToken = mson(token).color(ChatColor.AQUA);
					MsonMessenger.get().messageOne(sender, base.replaceAll(Lang.COMMAND_REPLACEMENT, blueToken).command(this));

					// Message: "/f access ..."
					// Message: "/f ally ..."
					for (KamiCommand suggestion : suggestions) {
						MsonMessenger.get().messageOne(sender, suggestion.getTemplate(false, false, sender));
					}

					// Message: "Use /Y to see all commands."
					MsonMessenger.get().messageOne(sender, Lang.COMMAND_CHILD_HELP.replaceAll(Lang.COMMAND_REPLACEMENT, this.getTemplate(false, false, sender)).command(this));
				}

				// NOTE: This return statement will jump to the finally block.
				return;
			}

			// Self Execution > Arguments Valid
			if (!this.isArgsValid(this.getArgs(), this.sender)) return;

			// Self Execution > Perform
			this.perform();
		} catch (KamiCommonException ex) {
			// Sometimes Types (or commands themselves) throw exceptions, to stop executing and notify the user.
			if (ex.hasMessages()) {
				MsonMessenger.get().messageOne(sender, ex.getMessages());
			}
		} catch (Throwable other) {
			other.printStackTrace();
		} finally {
			// Sender Sender - Cleanup
			this.senderFieldsOuter(null);
		}
	}

	public void senderFieldsOuter(CommandSender sender) {
		this.nextArg = 0;
		this.sender = sender;
		this.senderIsConsole = true;
		this.me = null;
		if (sender instanceof Player) {
			this.me = (Player) sender;
			this.senderIsConsole = false;
		}

		boolean set = (sender != null);
		this.senderFields(set);
	}

	public void senderFields(boolean set) {

	}

	// This is where the command action is performed.
	public void perform() throws KamiCommonException {
		// Per default we just run the help command!
		this.getHelpCommand().execute(this.sender, this.getArgs());
	}

	// -------------------------------------------- //
	// CALL VALIDATION
	// -------------------------------------------- //

	public boolean isArgsValid(@NotNull List<String> args, CommandSender sender) {
		if (args.size() < this.getParameterCountRequired(sender)) {
			if (sender != null) {
				MsonMessenger.get().msgOne(sender, Lang.COMMAND_TOO_FEW_ARGUMENTS);
				MsonMessenger.get().messageOne(sender, this.getTemplate());
			}
			return false;
		}

		// We don't need to take argConcatFrom into account. Because at this point the args 
		// are already concatenated and thus cannot be too many.
		if (args.size() > this.getParameterCount(sender) && this.isOverflowSensitive()) {
			if (sender != null) {
				// Get the too many string slice
				List<String> theTooMany = args.subList(this.getParameterCount(sender), args.size());
				MsonMessenger.get().msgOne(sender, Lang.COMMAND_TOO_MANY_ARGUMENTS, Txt.implodeCommaAndDot(theTooMany, Txt.parse("<aqua>%s"), Txt.parse("<b>, "), Txt.parse("<b> and "), ""));
				MsonMessenger.get().msgOne(sender, Lang.COMMAND_TOO_MANY_ARGUMENTS2);
				MsonMessenger.get().messageOne(sender, this.getTemplate());
			}
			return false;
		}
		return true;
	}

	public boolean isArgsValid(List<String> args) {
		return this.isArgsValid(args, null);
	}

	// -------------------------------------------- //
	// TEMPLATE
	// -------------------------------------------- //

	public static final Mson TEMPLATE_CORE = Mson.mson("/").color(ChatColor.AQUA);

	public Mson getTemplate(boolean addDesc, boolean onlyFirstAlias, CommandSender sender) {
		// Get base
		Mson ret = this.getTemplateChain(onlyFirstAlias, sender);

		List<KamiCommand> commands = this.getChain(true);
		// Check if last command is parentCommand and make command suggestible/clickable
		if (commands.get(commands.size() - 1).isParent()) {
			ret = ret.command(this);
		} else {
			ret = ret.suggest(this);
		}

		// Add args
		for (Mson parameter : this.getTemplateParameters(sender)) {
			ret = ret.add(Mson.SPACE);
			ret = ret.add(parameter.color(ChatColor.DARK_AQUA));
		}

		// Add desc
		if (addDesc) {
			ret = ret.add(Mson.SPACE);
			ret = ret.add(mson(this.getDesc()).color(ChatColor.YELLOW));
		}

		// Return Ret
		return ret;
	}

	public Mson getTemplateWithArgs(CommandSender sender, String... args) {
		return this.getTemplateWithArgs(sender, KUtil.list(args));
	}

	public Mson getTemplateWithArgs(CommandSender sender, @NotNull List<String> args) {
		Mson ret = this.getTemplateChain(true, sender);

		for (String arg : args) {
			ret = ret.add(Mson.SPACE);
			ret = ret.add(mson(arg).color(ChatColor.DARK_AQUA));
		}

		return ret;
	}

	public Mson getTemplateChain(boolean onlyFirstAlias, CommandSender sender) {
		Mson ret = TEMPLATE_CORE;

		// Get commands
		List<KamiCommand> commands = this.getChain(true);

		// Add commands
		boolean first = true;
		for (KamiCommand command : commands) {
			Mson mson;

			if (first && onlyFirstAlias) {
				mson = mson(command.getAliases().get(0));
			} else {
				mson = mson(Txt.implode(command.getAliases(), ","));
			}

			if (sender != null && !command.isRequirementsMet(sender, false)) {
				mson = mson.color(ChatColor.RED);
			} else {
				mson = mson.color(ChatColor.AQUA);
			}

			if (!first) ret = ret.add(Mson.SPACE);
			ret = ret.add(mson);
			first = false;
		}

		return ret;
	}

	public boolean isFullChainMet(CommandSender sender) {
		// Add commands
		for (KamiCommand command : this.getChain(true)) {
			if (sender != null && !command.isRequirementsMet(sender, false)) {
				return false;
			}
		}
		return true;
	}

	public void onUnmatchedArg() {

	}

	protected List<Mson> getTemplateParameters(CommandSender sender) {
		List<Mson> ret = new KamiList<>();

		for (Parameter<?> parameter : this.getParameters()) {
			ret.add(parameter.getTemplate(sender));
		}

		return ret;
	}

	public Mson getTemplate(boolean addDesc, boolean onlyFirstAlias) {
		return getTemplate(addDesc, onlyFirstAlias, sender);
	}

	public Mson getTemplate(boolean addDesc) {
		return getTemplate(addDesc, false);
	}

	public Mson getTemplate() {
		return getTemplate(false);
	}

	// -------------------------------------------- //
	// GET COMMAND LINE
	// -------------------------------------------- //

	public String getCommandLine(String @NotNull ... args) {
		return getCommandLine(Arrays.asList(args));
	}

	public String getCommandLine(Iterable<String> args) {
		// Initiate ret
		StringBuilder ret = new StringBuilder();

		// First a slash
		ret.append('/');

		// Then parent commands
		for (KamiCommand parent : this.getChain(false)) {
			// Append parent
			ret.append(parent.getAliases().get(0));

			// Append space
			ret.append(' ');
		}

		// Then ourself
		if (this.getAliases().isEmpty())
			throw new IllegalStateException(this.getClass().getSimpleName() + " has no aliases.");
		ret.append(this.getAliases().get(0));

		// Then args
		for (String arg : args) {
			// Check if null
			if (arg == null) continue;

			// First a space
			ret.append(' ');

			// Wrap if necessary
			if (arg.contains(" ")) arg = "\"" + arg + "\"";

			// Then the arg
			ret.append(arg);
		}

		// Return ret
		return ret.toString();
	}

	// -------------------------------------------- //
	// TAB
	// -------------------------------------------- //

	@Contract("null, _ -> fail; !null, null -> fail")
	public List<String> getTabCompletions(List<String> args, CommandSender sender) {
		if (args == null) throw new NullPointerException("args");
		if (sender == null) throw new IllegalArgumentException("sender was null");
		if (args.isEmpty()) throw new IllegalArgumentException("args was empty");

		if (this.isParent()) {
			return this.getTabCompletionsChild(args, sender);
		} else {
			return this.getTabCompletionsArg(args, sender);
		}
	}

	protected List<String> getTabCompletionsChild(@NotNull List<String> args, CommandSender sender) {
		// If this isn't the last argument ...
		if (args.size() != 1) {
			// ... and there is a matching child ...
			KamiCommand child = this.getChild(args.get(0));
			if (child == null) return Collections.emptyList();

			// ... get tab completions for that child.
			args.remove(0);
			return child.getTabCompletions(args, sender);
		}

		// ... else check the children.
		List<String> ret = new ArrayList<>();
		//noinspection ConstantConditions
		String token = args.get(args.size() - 1).toLowerCase();
		for (KamiCommand child : this.getChildren()) {
			if (!child.isRelevant(sender)) continue;
			ret.addAll(Txt.getStartsWithIgnoreCase(child.getAliases(), token));
		}

		return ret;
	}

	protected List<String> getTabCompletionsArg(List<String> args, CommandSender sender) {
		args = this.applyConcatenating(args);

		int index = args.size() - 1;
		if (!this.hasParameterForIndex(index)) return Collections.emptyList();
		Type<?> type = this.getParameterType(index);

		return type.getTabListFiltered(sender, args.get(index));
	}

	// -------------------------------------------- //
	// MESSAGE SENDING HELPERS
	// -------------------------------------------- //

	// MSG

	public boolean msg(String msg) {
		return MsonMessenger.get().msgOne(this.sender, msg);
	}

	public boolean msg(String msg, Object... args) {
		return MsonMessenger.get().msgOne(this.sender, msg, args);
	}

	public boolean msg(Collection<String> msgs) {
		return MsonMessenger.get().msgOne(this.sender, msgs);
	}

	// MESSAGE

	public boolean message(Object message) {
		return MsonMessenger.get().messageOne(this.sender, message);
	}

	public boolean message(Object... messages) {
		return MsonMessenger.get().messageOne(this.sender, messages);
	}

	public boolean message(Collection<?> messages) {
		return MsonMessenger.get().messageOne(this.sender, messages);
	}

	// CONVENIENCE MSON

	@Contract(pure = true)
	public static @NotNull Mson mson() {
		return Mson.mson();
	}

	public static @NotNull Mson mson(Object @NotNull ... parts) {
		return Mson.mson(parts);
	}

	public static @NotNull List<Mson> msons(Object @NotNull ... parts) {
		return Mson.msons(parts);
	}

	public static @NotNull List<Mson> msons(@NotNull Collection<?> parts) {
		return Mson.msons(parts);
	}

	// -------------------------------------------- //
	// ARGUMENT READERS
	// -------------------------------------------- //

	// Util

	public boolean argIsSet(int idx) {
		if (idx < 0) return false;
		if (idx + 1 > this.getArgs().size()) return false;
		return this.getArgs().get(idx) != null;
	}

	public boolean argIsSet() {
		return this.argIsSet(nextArg);
	}

	// Implicit index

	public String arg() {
		return this.argAt(nextArg);
	}

	public <T> T readArg() throws KamiCommonException {
		return this.readArgAt(nextArg);
	}

	public <T> T readArg(T defaultNotSet) throws KamiCommonException {
		return this.readArgAt(nextArg, defaultNotSet);
	}

	// Index logic

	public String argAt(int idx) {
		nextArg = idx + 1;
		if (!this.argIsSet(idx)) return null;
		return this.getArgs().get(idx);
	}

	@SuppressWarnings("unchecked")
	public <T> T readArgAt(int idx) throws KamiCommonException {
		// Make sure that a Parameter is present.
		if (!this.hasParameterForIndex(idx))
			throw new IllegalArgumentException(idx + " is out of range. Parameters size: " + this.getParameters().size());

		// Increment
		nextArg = idx + 1;

		// Get the parameter
		Parameter<T> parameter = (Parameter<T>) this.getParameter(idx);
		// Return the default in the parameter.
		if (!this.argIsSet(idx) && parameter.isDefaultValueSet()) return parameter.getDefaultValue();

		// OLD: Throw error if there was no arg, or default value in the parameter.
		// OLD: if ( ! this.argIsSet(idx)) throw new IllegalArgumentException("Trying to access arg: " + idx + " but that is not set.");
		// NOTE: This security actually blocks some functionality. Certain AR handle null argument values and specify their own default from within.
		// NOTE: An example is the MassiveQuest ARMNode which defaults to the used node of the player but must error when the player has no used node: "You must use a quest to skip the optional argument.".

		// Get the arg.
		String arg = null;
		if (this.argIsSet(idx)) arg = this.getArgs().get(idx);

		// Read and return the arg.
		return parameter.getType().read(arg, sender);
	}

	public <T> T readArgAt(int idx, T defaultNotSet) throws KamiCommonException {
		// Return the default passed.
		if (!this.argIsSet(idx)) {
			// Increment
			nextArg = idx + 1;

			// Use default
			return defaultNotSet;
		}

		// Increment is done in this method
		return readArgAt(idx);
	}
}
