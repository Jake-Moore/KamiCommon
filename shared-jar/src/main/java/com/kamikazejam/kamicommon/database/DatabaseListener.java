package com.kamikazejam.kamicommon.database;

@SuppressWarnings("unused")
public interface DatabaseListener {
    void onConnected();
    void onConnectionFailed();
    void onExceptionCaught(Exception exception);
}
