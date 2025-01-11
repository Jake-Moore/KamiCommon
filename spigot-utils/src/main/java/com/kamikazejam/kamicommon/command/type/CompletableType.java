package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A wrapper class that handles completable futures and error handling for commands.
 * This makes it easier to work with async operations in command types.
 * @param <T> The type of value being wrapped
 */
@SuppressWarnings("unused")
public class CompletableType<T> {
    private final CompletableFuture<T> future;
    private final CommandSender sender;

    public CompletableType(CompletableFuture<T> future, CommandSender sender) {
        this.future = future;
        this.sender = sender;
    }

    /**
     * Handle the successful completion of the future with the provided consumer.
     * Any {@link KamiCommonException}s that occur during the async operation will be handled automatically.
     * @param consumer The consumer to handle the successful result (called ASYNC - NOT ON MAIN THREAD)
     */
    public void whenParsedAsync(Consumer<T> consumer) {
        whenComplete(true, consumer);
    }

    /**
     * Handle the successful completion of the future with the provided consumer.
     * Any {@link KamiCommonException}s that occur during the async operation will be handled automatically.
     * @param consumer The consumer to handle the successful result (called ASYNC - NOT ON MAIN THREAD)
     */
    public void whenParsedSync(Consumer<T> consumer) {
        whenComplete(false, consumer);
    }

    private void whenComplete(boolean async, Consumer<T> consumer) {
        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                if (async) {
                    consumer.accept(result);
                }else {
                    Bukkit.getScheduler().runTask(SpigotUtilsSource.get(), () -> consumer.accept(result));
                }
                return;
            }

            // Unwrap the exception if it's wrapped in a CompletionException
            Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;

            // Go Sync for message sending purposes
            Bukkit.getScheduler().runTask(SpigotUtilsSource.get(), () -> {
                if (cause instanceof KamiCommonException ex) {
                    // Handle KamiCommonException by sending the message to the sender
                    KMessageSingle message = ex.getKMessage();
                    if (message != null) {
                        NmsAPI.getMessageManager().processAndSend(sender, message);
                    }
                } else {
                    // For other exceptions, wrap in KamiCommonException and send generic error
                    KMessageSingle message = new KMessageSingle("&cAn unexpected error occurred while processing your request.");
                    NmsAPI.getMessageManager().processAndSend(sender, message);
                    throwable.printStackTrace();
                }
            });
        });
    }

    /**
     * Create a new AsyncCommandWrapper from a CompletableFuture and CommandSender.
     * @param future The future to wrap
     * @param sender The command sender
     * @param <T> The type of value being wrapped
     * @return A new AsyncCommandWrapper
     */
    public static <T> CompletableType<T> wrap(CompletableFuture<T> future, CommandSender sender) {
        return new CompletableType<>(future, sender);
    }

} 