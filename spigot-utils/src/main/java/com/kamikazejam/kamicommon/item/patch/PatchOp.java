package com.kamikazejam.kamicommon.item.patch;

/**
 * Represents the type of operation to be performed in a patch.<br>
 * <br>
 * VALUES:<br>
 * - ADD: Adds a new element or modifies an existing one.<br>
 * - REMOVE: Removes an existing element.
 */
public enum PatchOp {
    ADD,
    REMOVE,
}
