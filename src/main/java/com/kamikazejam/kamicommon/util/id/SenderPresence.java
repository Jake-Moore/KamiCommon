package com.kamikazejam.kamicommon.util.id;

import org.jetbrains.annotations.Contract;

public enum SenderPresence {
    // IMP NOTE: These must be sorted, with the most strict first
    // and the most loose at the end.
    LOCAL, // Online and logged in on this very server.
    ONLINE, // Online somewhere on the cloud. May be this server may be another server.
    OFFLINE, // The opposite of online.
    ANY, // Any. Local, Online or Offline.

    ;

    // -------------------------------------------- //
    // GET FROM ONLINE VALUE
    // -------------------------------------------- //

    @Contract("null -> null")
    public static SenderPresence fromOnline(Boolean online) {
        if (online == null) return null;
        return fromOnline(online.booleanValue());
    }

    @Contract(pure = true)
    public static SenderPresence fromOnline(boolean online) {
        return online ? ONLINE : OFFLINE;
    }

}
 