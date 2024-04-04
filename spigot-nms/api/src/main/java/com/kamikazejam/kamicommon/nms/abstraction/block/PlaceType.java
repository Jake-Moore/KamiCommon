package com.kamikazejam.kamicommon.nms.abstraction.block;

public enum PlaceType {
    /**
     * Physics and Light are handled (aka Bukkit Handling)
     * Slowest
     */
    BUKKIT,
    /**
     * Light is handled, but physics is not (Bukkit w/ applyPhysics = false)
     * Slightly faster than Bukkit
     */
    NO_PHYSICS,
    /**
     * Neither Physics nor Light are handled (NMS Handling, light=false & physics=false)
     * Fastest possible
     */
    NMS
}
