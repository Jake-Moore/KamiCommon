package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.requirement.Requirement;
import com.kamikazejam.kamicommon.command.requirement.RequirementAbstract;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageBlock;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.interfaces.Active;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import com.kamikazejam.kamicommon.util.predicate.PredicateLevenshteinClose;
import com.kamikazejam.kamicommon.util.predicate.PredicateStartsWithIgnoreCase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "unused"})
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
     * If you register a command after the server has started, you must call the following method: <br>
     * {@link KamiCommonCommandRegistration#updateRegistrations()} <br>
     * In order for the command to be added to the server.
     */
    public void registerCommand(KamiPlugin plugin) {
        this.setActive(plugin);
    }

    /**
     * If you unregister a command after the server has started, you must call the following method: <br>
     * @see KamiCommonCommandRegistration#updateRegistrations() <br>
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
            // add() call is safe, as the 'allInstances' field is a set.
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
    public @NotNull KamiPlugin getActivePlugin() {
        // Automatically fetch from parent if null.
        if (this.activePlugin == null) {
            if (parent != null && !Objects.equals(parent, this)) {
                this.activePlugin = Objects.requireNonNull(this.getParent().getActivePlugin());
            } else {
                throw new RuntimeException("No active plugin set for command " + this.getClass().getSimpleName());
            }
        }
        return Objects.requireNonNull(this.activePlugin);
    }

    @Override
    public void setActive(@Nullable KamiPlugin plugin) {
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
    private KamiCommand parent = null;

    // The child commands.
    @Getter
    private List<KamiCommand> children = Collections.emptyList();

    // === ALIASES ===

    // The different names this commands will react to
    @Getter
    private List<String> aliases = new KamiList<>();

    // === PARAMETERS ===

    // The command parameters.
    @Setter @Getter
    private List<Parameter<?>> parameters = new KamiList<>();

    // === PREPROCESS ===

    // Should the arguments be parsed considering quotes and backslashes and such?
    @Getter
    private boolean tokenizing = true;

    // Are "smart" quotes replaced with normal characters?
    @Getter
    private boolean unsmart = true;

    // === PUZZLER ===

    // Should an error be thrown if "too many" arguments are provided?
    @Getter
    private boolean overflowSensitive = true;

    // Should the last parameter concatenate all surplus arguments?
    @Getter
    private boolean concatenating = false;

    // Should we try to automatically swap the arguments around if they were typed in invalid order?
    @Getter
    private boolean swapping = true;

    // === REQUIREMENTS ===

    // All these requirements must be met for the command to be executable;
    @Getter
    private List<Requirement> requirements = new KamiList<>();

    // === HELP ===

    // A short description of what the command does. Such as "eat hamburgers" or "do admin stuff".
    private String desc = null;

    // A specific permission node to use for description if desc is null.
    @Setter
    private String descPermission = null;

    // Free text displayed at the top of the help command.
    // prefixed with a # character and a space.
    @Getter
    private @NotNull List<KMessageSingle> helpComments = new KamiList<>();

    // The visibility of this command in help command.
    @Setter
    @Getter
    private Visibility visibility = Visibility.VISIBLE;

    // The priority of this command when aliases are ambiguous.
    @Setter @Getter
    private long priority = 0;

    // === EXECUTION ===
    @Getter
    private @Nullable CommandContext context = null;

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

    /**
     * Returns all parents of this command, like a stack trace.<br>
     * The first element will be this command. The root command is last in the list.
     */
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

    /**
     * Returns the chain of commands leading to this command, like a stack trace.<br>
     * The first element will be the root command. This command is last in the list.
     */
    public List<KamiCommand> getChain(boolean includeSelf) {
        // return parents in reverse order.
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

    @Contract(mutates = "this")
    public void addChild(KamiCommand child) {
        // NoChange
        if (this.getChildren().contains(child)) return;

        // Apply
        this.addChild(child, this.getChildren().size());
    }

    @Contract(mutates = "this")
    public void addChildAfter(KamiCommand child, KamiCommand after) {
        int index = this.getChildren().indexOf(after);
        if (index == -1) {
            index = this.getChildren().size();
        } else {
            index++;
        }
        this.addChild(child, index);
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

    public void addChild(KamiCommand child, int index) {
        if (!this.hasChildren() && !(child instanceof KamiCommandHelp)) {
            this.getHelpCommand();
            index++;
        }

        List<KamiCommand> children = new KamiList<>(this.getChildren());
        children.add(index, child);
        this.children = Collections.unmodifiableList(children);
        child.setParent(this);
    }

    public KamiCommandHelp getHelpCommand() {
        if (!this.hasChildren()) this.addChild(new KamiCommandHelp(), 0);
        List<KamiCommand> children = this.getChildren();
        return (KamiCommandHelp) children.getFirst();
    }

    // -------------------------------------------- //
    // CHILDREN > GET
    // -------------------------------------------- //

    // The full version of the child matcher method.
    // Returns a set of child commands with similar aliases.
    //
    // token - the full alias or an alias prefix.
    // onlyRelevantToSender - if non-null only returns commands relevant to specific sender
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

    @Contract(mutates = "this")
    public void setAliases(Collection<String> aliases) {
        this.aliases = new KamiList<>(aliases);
    }

    @Contract(mutates = "this")
    public void setAliases(String @NotNull ... aliases) {
        this.setAliases(Arrays.asList(aliases));
    }

    @Contract(mutates = "this")
    public void addAliases(Collection<String> aliases) {
        this.aliases.addAll(aliases);
    }

    @Contract(mutates = "this")
    public void addAliases(String @NotNull ... aliases) {
        this.addAliases(Arrays.asList(aliases));
    }

    // -------------------------------------------- //
    // PARAMETERS
    // -------------------------------------------- //

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

    @Contract(mutates = "this")
    @NotNull
    public <T> Parameter<T> addParameter(@NotNull Parameter<T> parameter) {
        Preconditions.checkNotNull(parameter, "parameter cannot be null");

        // Concat safety. (throw if addParameter was called after a concatenating parameter was added)
        if (this.isConcatenating()) {
            throw new IllegalStateException("You can't add args if a prior one concatenates.");
        }

        // Req/optional safety. (All 'required' args must come before any 'optional' args)
        int prior = this.getParameters().size() - 1;
        if (this.hasParameterForIndex(prior) && this.getParameter(prior).isOptional() && parameter.isRequired()) {
            throw new IllegalArgumentException("You can't add required args, if a prior one is optional.");
        }

        // If false no change is made.
        // If true change is made.
        this.setConcatenating(parameter.isConcatFromHere());

        this.getParameters().add(parameter);
        return parameter;
    }

    @Contract(mutates = "this")
    @NotNull
    public <T> Parameter<T> addParameter(@NotNull Parameter.Builder<T> parameter) {
        return this.addParameter(parameter.build());
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
    // REQUIREMENTS
    // -------------------------------------------- //

    @Contract(mutates = "this")
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    @Contract(mutates = "this")
    public void addRequirements(Collection<Requirement> requirements) {
        this.requirements.addAll(requirements);
    }

    @Contract(mutates = "this")
    public void addRequirements(Requirement @NotNull ... requirements) {
        this.addRequirements(Arrays.asList(requirements));
    }

    public boolean isRequirementsMet(@NotNull CommandSender sender, boolean verbose) {
        return RequirementAbstract.isRequirementsMet(this.getRequirements(), sender, this, verbose);
    }

    public String getRequirementsError(@NotNull CommandSender sender, boolean verbose) {
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

    public String getDescPermission() {
        if (this.descPermission != null) return this.descPermission;
        // Otherwise we try to find one.
        for (Requirement requirement : this.getRequirements()) {
            if (!(requirement instanceof RequirementHasPerm)) continue;
            return ((RequirementHasPerm) requirement).getPermissionId();
        }
        return null;
    }

    public boolean isVisibleTo(CommandSender sender) {
        if (this.getVisibility() == Visibility.VISIBLE) return true;
        if (this.getVisibility() == Visibility.INVISIBLE) return false;
        return this.isRequirementsMet(sender, false);
    }

    // -------------------------------------------- //
    // EXECUTOR
    // -------------------------------------------- //

    // Internal command execution logic, called from the bukkit command executor.
    // For implementing your own command logic, override the perform() method. (see below)

    /**
     * @param label The specific label (or 'alias') used for this KamiCommand.
     */
    public final void execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        try {
            // Apply Concatenation Rules (merges arg strings together if applicable)
            args = new ArrayList<>(this.applyConcatenating(args));

            // Initialize Execution State (run context)
            this.context = new CommandContext(sender, label, args);

            // Requirements
            if (!this.isRequirementsMet(sender, true)) return;

            // Child Execution
            if (this.isParent() && !args.isEmpty()) {
                // Get matches
                String token = args.getFirst();

                // Fetch direct matches (not using levenshtein) only allowing lowercase startsWith matches.
                // i.e. if you run "/command t", any subcommand starting with "t" will match.
                Set<KamiCommand> matches = this.getChildren(token, false, null, true);

                // Score!
                if (matches.size() == 1) {
                    KamiCommand child = matches.iterator().next();
                    List<String> childArgs = new ArrayList<>(args);
                    childArgs.removeFirst();
                    // Invoke child command with the token (label) used, and the remaining args.
                    child.execute(sender, token, childArgs);
                }
                // Crap!
                else {
                    String errorContent;
                    Collection<KamiCommand> suggestions;

                    if (matches.isEmpty()) {
                        // Use levenshtein distance to find the closest subcommand match.
                        errorContent = Lang.getCommandChildNone();
                        suggestions = this.getChildren(token, true, sender, false);
                        onUnmatchedArg(context);
                    } else {
                        // Had multiple possible matches, inform the user
                        errorContent = Lang.getCommandChildAmbiguous();
                        suggestions = this.getChildren(token, false, sender, false);
                    }

                    // Message: "The sub command X couldn't be found."
                    // OR
                    // Message: "The sub command X is ambiguous."
                    errorContent = errorContent.replace(Lang.placeholderReplacement, Lang.commandColor + token);

                    // Create the KMessage informing the user about the matching error.
                    // This is kind of like the title line, with additional possible commands listed after it.
                    NmsAPI.getMessageManager().processAndSend(sender, new KMessageSingle(errorContent));

                    // Send all possible subcommand matches as suggestions. I.E.:
                    //   "/command test ..."
                    //   "/command temp ..."
                    for (KamiCommand suggestion : suggestions) {
                        KMessage template = this.getSuggestionClickable(
                                suggestion,
                                token, // token that triggered this suggestion
                                sender
                        );
                        NmsAPI.getMessageManager().processAndSend(sender, template);
                    }

                    // Message: "Use /<command> to see all commands."
                    KMessage help = new KMessageSingle(Lang.getCommandChildHelp()).addClickSuggestCommand(
                            Lang.placeholderReplacement,
                            this.getCurrentTemplateChain(),
                            this.getCurrentCommandLine()
                    );
                    NmsAPI.getMessageManager().processAndSend(sender, help);
                }

                // NOTE: This return statement will jump to the 'finally' block.
                return;
            }

            // Self Execution > Arguments Valid
            if (!this.isArgsValid(context)) return;

            // Self Execution > Perform
            this.perform(context);
        } catch (KamiCommonException ex) {
            // Sometimes Types (or commands themselves) throw exceptions, to stop executing and notify the user.
            KMessageSingle message = ex.getKMessage();
            if (message != null) {
                NmsAPI.getMessageManager().processAndSend(sender, message);
            }
        } catch (Throwable other) {
            other.printStackTrace();
        } finally {
            // Reset Execution Fields - Cleanup
            this.resetExecutionState();
        }
    }

    private void resetExecutionState() {
        this.context = null;
    }

    // This is where the command action is performed.
    // By default, it will execute the help command unless overridden to do something else.
    public void perform(@NotNull CommandContext context) throws KamiCommonException {
        // Per default, we just run the help command!
        Preconditions.checkNotNull(context, "context cannot be null");
        this.getHelpCommand().execute(context.getSender(), context.getLabel(), context.getArgs());
    }

    // -------------------------------------------- //
    // CALL VALIDATION
    // -------------------------------------------- //

    /**
     * Called on a SubCommand to check if the provided arguments are valid for the command to execute.<br>
     */
    private boolean isArgsValid(@NotNull CommandContext context) {
        @NotNull CommandSender sender = context.getSender();
        @NotNull List<String> args = context.getArgs();

        // Check if there are too few arguments.
        if (args.size() < this.getParameterCountRequired(sender)) {
            sender.sendMessage(StringUtil.t(Lang.getCommandTooFewArguments()));
            sender.sendMessage(StringUtil.t(this.getCurrentTemplateUsage(sender, false)));
            return false;
        }

        // We don't need to take argConcatFrom into account. Because at this point the args
        // are already concatenated and thus cannot be too many.
        if (args.size() > this.getParameterCount(sender) && this.isOverflowSensitive()) {
            // Get the extra args that were the 'too many'
            List<String> extraArgs = args.subList(this.getParameterCount(sender), args.size());
            String extraArgsImploded = Txt.implodeCommaAndDot(
                    extraArgs,
                    Lang.commandColor + "%s",
                    Lang.errorColor + ", ",
                    Lang.errorColor + " and ",
                    ""
            );

            String message = String.format(Lang.getCommandTooManyArguments(), extraArgsImploded);
            sender.sendMessage(StringUtil.t(message));
            sender.sendMessage(StringUtil.t(Lang.getCommandUseLike()));
            sender.sendMessage(StringUtil.t(this.getCurrentTemplateUsage(sender, false)));
            return false;
        }
        return true;
    }

    // -------------------------------------------- //
    // TEMPLATE
    // -------------------------------------------- //

    /**
     * @param suggested The command that should be suggested to the user. (subcommand of this command)
     * @param token The token used that triggered this possible suggestion. (likely a partial alias)
     */
    @NotNull
    public KMessageSingle getSuggestionClickable(
            @NotNull KamiCommand suggested,
            @NotNull String token,
            @NotNull CommandSender sender
    ) {
        // The current chain of args leading to the suggestion (does NOT include the suggestion itself).
        StringBuilder templateBuilder = new StringBuilder(this.getCurrentTemplateChain());

        // Find the aliases of suggested that match the token. (using startsWith Predicate)
        Predicate<String> predicate = PredicateStartsWithIgnoreCase.get(token);
        List<String> matchingAliases = new KamiList<>();
        for (String alias : suggested.getAliases()) {
            // If the alias matches the token, add it to the list.
            if (predicate.apply(alias)) {
                matchingAliases.add(alias);
            }
        }

        // Compose the subCommand aliases concatenated with commas.
        //   fall back to the first alias if no matches were found.
        String subCommandAliases = matchingAliases.isEmpty() ? suggested.getAliases().getFirst() : Txt.implode(matchingAliases, ",");
        templateBuilder.append(" ").append(Lang.commandColor).append(subCommandAliases.trim());

        // Add 'suggested' parameters to the template string
        for (String parameter : suggested.getTemplateParameters(sender)) {
            templateBuilder.append(" ").append(Lang.parameterColor).append(parameter);
        }

        // Content of the KMessageSingle
        String messageContent = templateBuilder.toString().trim(); // remove trailing spaces

        return KMessageSingle.ofClickSuggestCommand(messageContent, getSuggestionCommandLine(suggested, token));
    }

    /**
     * Given a suggested subcommand (a child of this command), returns a fully qualified command line
     * that includes the current command chain and the suggested subcommand alias.<br>
     * The subcommand alias is chosen based on the provided token, which is expected to be a prefix of the alias.
     */
    @NotNull
    public String getSuggestionCommandLine(@NotNull KamiCommand suggested, @NotNull String token) {
        // A valid command string comprised of every KamiCommand up until this point.
        StringBuilder commandBuilder = new StringBuilder(this.getCurrentCommandLine());

        // Find the aliases of suggested that match the token. (using startsWith Predicate)
        Predicate<String> predicate = PredicateStartsWithIgnoreCase.get(token);
        List<String> matchingAliases = new KamiList<>();
        for (String alias : suggested.getAliases()) {
            // If the alias matches the token, add it to the list.
            if (predicate.apply(alias)) {
                matchingAliases.add(alias);
            }
        }

        // Pick one alias to use as the suggested subcommand.
        //   fall back to the first alias if no matches were found.
        String subCommandAlias = matchingAliases.isEmpty() ? suggested.getAliases().getFirst() : matchingAliases.getFirst();
        commandBuilder.append(" ").append(subCommandAlias.trim());

        // If the suggestion has more subcommands or parameters, append a space for tab completions to begin.
        if (!suggested.getParameters().isEmpty() || suggested.hasChildren()) {
            return commandBuilder.toString().trim() + " "; // add trailing space for tab completion
        } else {
            return commandBuilder.toString().trim(); // no trailing space, this looks like the end of the command.
        }
    }

    /**
     * @param child The command that should be suggested to the user. (subcommand of this command)
     */
    @NotNull
    public KMessageSingle getHelpClickable(
            @NotNull KamiCommand child,
            @NotNull CommandSender sender
    ) {
        // The current chain of args leading to the suggestion (does NOT include the suggestion itself).
        StringBuilder templateBuilder = new StringBuilder(this.getCurrentTemplateChain());

        // Compose the subCommand aliases concatenated with commas.
        String subCommandAliases = Txt.implode(child.getAliases(), ",");
        templateBuilder.append(" ").append(Lang.commandColor).append(subCommandAliases.trim());

        // Add parameters to the template string
        for (String parameter : child.getTemplateParameters(sender)) {
            templateBuilder.append(" ").append(Lang.parameterColor).append(parameter);
        }

        // Add desc (always want to show the description in help display)
        templateBuilder.append(" ").append(Lang.descriptionColor).append(child.getDesc());

        // Content of the KMessageSingle
        String messageContent = templateBuilder.toString().trim(); // remove trailing spaces

        return KMessageSingle.ofClickSuggestCommand(messageContent, getHelpCommandLine(child));
    }

    /**
     * Given a subcommand (a child of this command), returns a fully qualified command line
     * that includes the current command chain and the suggested subcommand alias.
     */
    @NotNull
    public String getHelpCommandLine(@NotNull KamiCommand child) {
        // A valid command string comprised of every KamiCommand up until this point.
        StringBuilder commandBuilder = new StringBuilder(this.getCurrentCommandLine());

        // Pick one alias to use as the suggested subcommand.
        //   fall back to the first alias if no matches were found.
        String subCommandAlias = child.getAliases().getFirst();
        commandBuilder.append(" ").append(subCommandAlias.trim());

        // If the suggestion has more subcommands or parameters, append a space for tab completions to begin.
        if (!child.getParameters().isEmpty() || child.hasChildren()) {
            return commandBuilder.toString().trim() + " "; // add trailing space for tab completion
        } else {
            return commandBuilder.toString().trim(); // no trailing space, this looks like the end of the command.
        }
    }

    /**
     * Returns the current template chain for this command. Requires an active command instance with non-null context.<br>
     * The chain is the color coded concatenation of all command labels thus far (no parameters or descriptions).
     */
    @NotNull
    public String getCurrentTemplateChain() {
        StringBuilder ret = new StringBuilder(Lang.commandColor + "/");
        List<KamiCommand> commands = this.getChain(true);

        boolean first = true;
        for (KamiCommand command : commands) {
            @NotNull CommandContext context = Preconditions.checkNotNull(command.context);
            String base = Lang.commandColor + context.getLabel();

            if (!first) {
                ret.append(" ");
            }
            ret.append(base);
            first = false;
        }

        return ret.toString().trim(); // remove trailing space
    }

    /**
     * Returns the current template usage for this command. Requires an active command instance with non-null context.<br>
     * The usage is the color coded concatenation of all command labels thus far, followed by parameters and description.
     */
    @NotNull
    public String getCurrentTemplateUsage(@NotNull CommandSender sender, boolean addDesc) {
        StringBuilder ret = new StringBuilder(this.getCurrentTemplateChain());

        // Add args
        for (String parameter : this.getTemplateParameters(sender)) {
            ret.append(" ").append(Lang.parameterColor).append(parameter);
        }

        // Add desc
        if (addDesc) {
            ret.append(" ").append(Lang.descriptionColor).append(this.getDesc());
        }

        return ret.toString().trim(); // remove trailing space
    }

    /**
     * Returns the current command ran by the player. Requires an active command instance with non-null context.<br>
     * Includes a leading slash.
     */
    @NotNull
    public String getCurrentCommandLine() {
        StringBuilder ret = new StringBuilder("/");
        List<KamiCommand> commands = this.getChain(true);

        boolean first = true;
        for (KamiCommand command : commands) {
            @NotNull CommandContext context = Preconditions.checkNotNull(command.context);

            if (!first) {
                ret.append(" ");
            }
            ret.append(context.getLabel());
            first = false;
        }

        return ret.toString().trim(); // remove trailing space
    }

    @NotNull
    public String getFullTemplate(boolean addDesc, boolean onlyFirstAlias, boolean onlyOneSubAlias, @Nullable CommandSender sender) {
        // Get base
        StringBuilder ret = new StringBuilder(this.getTemplateChain(onlyFirstAlias, onlyOneSubAlias, sender));

        // Add args
        for (String parameter : this.getTemplateParameters(sender)) {
            ret.append(" ").append(Lang.parameterColor).append(parameter);
        }

        // Add desc
        if (addDesc) {
            ret.append(" ").append(Lang.descriptionColor).append(this.getDesc());
        }

        // Return Ret
        return ret.toString();
    }

    @NotNull
    private  String getTemplateChain(boolean onlyFirstAlias, boolean onlyOneSubAlias, @Nullable CommandSender sender) {
        StringBuilder ret = new StringBuilder(Lang.commandColor + "/");

        // Get commands
        List<KamiCommand> commands = this.getChain(true);

        // Add commands
        boolean first = true;
        for (KamiCommand command : commands) {
            String base;

            if ((first && onlyFirstAlias) || onlyOneSubAlias) {
                base = command.getAliases().getFirst();
            } else {
                base = Txt.implode(command.getAliases(), ",");
            }

            if (sender != null && !command.isRequirementsMet(sender, false)) {
                base = Lang.inaccessibleCommandColor + base;
            } else {
                base = Lang.commandColor + base;
            }

            if (!first) {
                ret.append(" ");
            }
            ret.append(base);
            first = false;
        }

        return ret.toString();
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

    // Intended to be overridden
    public void onUnmatchedArg(@NotNull CommandContext context) {}

    @NotNull
    protected List<String> getTemplateParameters(@Nullable CommandSender sender) {
        List<String> ret = new KamiList<>();

        for (Parameter<?> parameter : this.getParameters()) {
            ret.add(parameter.getTemplate(sender));
        }
        return ret;
    }

    // -------------------------------------------- //
    // GET COMMAND LINE
    // -------------------------------------------- //

    public String getCommandLine(Iterable<String> args) {
        // Initiate ret
        StringBuilder ret = new StringBuilder();

        // First a slash
        ret.append('/');

        // Then parent commands
        for (KamiCommand parent : this.getChain(false)) {
            // Append parent
            ret.append(parent.getAliases().getFirst());

            // Append space
            ret.append(' ');
        }

        // Then ourselves
        if (this.getAliases().isEmpty())
            throw new IllegalStateException(this.getClass().getSimpleName() + " has no aliases.");
        ret.append(this.getAliases().getFirst());

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
            KamiCommand child = this.getChild(args.getFirst());
            if (child == null) return Collections.emptyList();

            // ... get tab completions for that child.
            args.removeFirst();
            return child.getTabCompletions(args, sender);
        }

        // ... else check the children.
        List<String> ret = new ArrayList<>();
        //noinspection ConstantConditions
        String token = args.getLast().toLowerCase();
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

    // -------------------------------------------- //
    // ARGUMENT READERS
    // -------------------------------------------- //

    // Util

    public boolean argIsSet(int idx) {
        if (this.context == null) return false;
        if (idx < 0) return false;
        if (idx + 1 > this.context.getArgs().size()) return false;
        return this.context.getArgs().get(idx) != null;
    }

    public boolean argIsSet() {
        return this.context != null && this.argIsSet(context.getNextArg());
    }

    // Implicit index

    public <T> T readArg() throws KamiCommonException {
        Preconditions.checkNotNull(this.context, "context cannot be null");
        return this.readArgAt(this.context.getNextArg());
    }

    public <T> T readArg(T defaultNotSet) throws KamiCommonException {
        Preconditions.checkNotNull(this.context, "context cannot be null");
        return this.readArgAt(this.context.getNextArg(), defaultNotSet);
    }

    @SuppressWarnings("unchecked")
    private <T> T readArgAt(int idx) throws KamiCommonException {
        Preconditions.checkNotNull(this.context, "context cannot be null");

        // Make sure that a Parameter is present.
        if (!this.hasParameterForIndex(idx)) {
            throw new IllegalArgumentException(idx + " is out of range. Parameters size: " + this.getParameters().size());
        }

        // Increment
        this.context.setNextArg(idx + 1);

        // Get the parameter
        Parameter<T> parameter = (Parameter<T>) this.getParameter(idx);

        // Return the default in the parameter.
        if (!this.argIsSet(idx) && parameter.getDefaultValue() != null) {
            return parameter.getDefaultValue().getValue();
        }

        // OLD: Throw error if there was no arg, or default value in the parameter.
        // OLD: if ( ! this.argIsSet(idx)) throw new IllegalArgumentException("Trying to access arg: " + idx + " but that is not set.");
        // NOTE: This security actually blocks some functionality. Certain AR handle null argument values and specify their own default from within.
        // NOTE: An example is the MassiveQuest ARMNode which defaults to the used node of the player but must error when the player has no used node: "You must use a quest to skip the optional argument".

        // Get the arg.
        String arg = null;
        if (this.argIsSet(idx)) arg = this.context.getArgs().get(idx);

        // Read and return the arg.
        return parameter.getType().read(arg, this.context.getSender());
    }

    private <T> T readArgAt(int idx, T defaultNotSet) throws KamiCommonException {
        Preconditions.checkNotNull(this.context, "context cannot be null");

        // Return the default passed.
        if (!this.argIsSet(idx)) {
            // Increment
            this.context.setNextArg(idx + 1);

            // Use default
            return defaultNotSet;
        }

        // Increment is done in this method
        return readArgAt(idx);
    }


    public void setHelpComments(@NotNull List<KMessageSingle> helpComments) {
        this.helpComments = helpComments;
    }

    public void setHelpComments(@NotNull KMessageBlock block) {
        this.helpComments.clear();
        for (String line : block.getLines()) {
            this.helpComments.add(new KMessageSingle(line));
        }
    }

    /**
     * Message and Color configuration for KamiCommand messages.
     */
    public static class Lang {
        // Command Colors and Formats
        @Getter @Setter
        private static @NotNull String helpCommentFormat = ChatColor.GOLD + "# %s";
        @Getter @Setter
        private static @NotNull ChatColor commandColor = ChatColor.AQUA;
        @Getter @Setter
        private static @NotNull ChatColor inaccessibleCommandColor = ChatColor.RED;
        @Getter @Setter
        private static @NotNull ChatColor parameterColor = ChatColor.DARK_AQUA;
        @Getter @Setter
        private static @NotNull ChatColor descriptionColor = ChatColor.YELLOW;

        // Placeholders
        public static final @NotNull String placeholderReplacement = "{REPLACEMENT}";
        public static final @NotNull String placeholderErrorColor = "{ERROR_COLOR}";
        public static final @NotNull String placeholderErrorParamColor = "{ERROR_PARAM_COLOR}";

        // Placeholder Values
        @Getter @Setter
        private static @NotNull ChatColor errorColor = ChatColor.RED;
        @Getter @Setter
        private static @NotNull ChatColor errorParamColor = ChatColor.LIGHT_PURPLE;

        // Configurable Strings
        //   Errors
        @Setter private static @NotNull String requirementPermissionDenied =    placeholderErrorColor + "You don't have permission to do that.";
        @Setter private static @NotNull String senderMustBePlayer =             placeholderErrorColor + "This command can only be used by ingame players.";
        @Setter private static @NotNull String senderMustNotBePlayer =          placeholderErrorColor + "This command can not be used by ingame players.";
        @Setter private static @NotNull String commandTooFewArguments =         placeholderErrorColor + "Not enough command input. " + ChatColor.YELLOW + "You should use it like this:";
        @Setter private static @NotNull String commandTooManyArguments =        placeholderErrorColor + "Too much command input %s" + placeholderErrorColor + ".";
        @Setter private static @NotNull String commandTooManyTabSuggestions =   placeholderErrorParamColor + "%d " + placeholderErrorColor + "tab completions available. Be more specific and try again.";

        //   Other
        @Getter @Setter private static @NotNull String commandUseLike =         ChatColor.YELLOW + "You should use the command like this:";
        @Getter @Setter private static @NotNull String commandChildAmbiguous =  ChatColor.YELLOW + "The sub command " + placeholderReplacement + ChatColor.YELLOW + " is ambiguous.";
        @Getter @Setter private static @NotNull String commandChildNone =       ChatColor.YELLOW + "The sub command " + placeholderReplacement + ChatColor.YELLOW + " couldn't be found.";
        @Getter @Setter private static @NotNull String commandChildHelp =       ChatColor.YELLOW + "Use " + placeholderReplacement + ChatColor.YELLOW + " to see all commands.";

        // Derived Strings
        public static @NotNull String getRequirementPermissionDenied() {
            return requirementPermissionDenied.replace(placeholderErrorColor, errorColor.toString());
        }
        public static @NotNull String getSenderMustBePlayer() {
            return senderMustBePlayer.replace(placeholderErrorColor, errorColor.toString());
        }
        public static @NotNull String getSenderMustNotBePlayer() {
            return senderMustNotBePlayer.replace(placeholderErrorColor, errorColor.toString());
        }
        public static @NotNull String getCommandTooFewArguments() {
            return commandTooFewArguments.replace(placeholderErrorColor, errorColor.toString());
        }
        public static @NotNull String getCommandTooManyArguments() {
            return commandTooManyArguments.replace(placeholderErrorColor, errorColor.toString());
        }
        public static @NotNull String getCommandTooManyTabSuggestions() {
            return commandTooManyTabSuggestions
                    .replace(placeholderErrorColor, errorColor.toString())
                    .replace(placeholderErrorParamColor, errorParamColor.toString());
        }
    }
}
