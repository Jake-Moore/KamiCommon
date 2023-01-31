package com.kamikazejamplugins.kamicommon.util;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Objects;

@SuppressWarnings("unused")
@Data
public class VectorW {

    private transient World world;

    @SerializedName("world")
    private String worldName;

    @SerializedName("x")
    private double x;

    @SerializedName("y")
    private double y;

    @SerializedName("z")
    private double z;

    @SerializedName("pitch")
    private float pitch;

    @SerializedName("yaw")
    private float yaw;

    public VectorW(World world, Vector vector) {
        this.world = world;
        this.worldName = world.getName();
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
        this.yaw = 0;
        this.pitch = 0;
    }

    public VectorW(World world, double x, double y, double z) {
        this.world = world;
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public VectorW(Location loc) {
        this.world = loc.getWorld();
        assert world != null;
        this.worldName = world.getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.pitch = loc.getPitch();
        this.yaw = loc.getYaw();
    }

    public World getWorld() {
        if (world != null) { return world; }
        return world = Bukkit.getWorld(worldName);
    }

    public Location toLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    public Vector toVector() { return new Vector(x, y, z); }

    public double distance(VectorW vector) {
        return Math.sqrt(Math.pow(vector.getX() - x, 2) + Math.pow(vector.getY() - y, 2) + Math.pow(vector.getZ() - z, 2));
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }
    public int getBlockY() {
        return (int) Math.floor(y);
    }
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VectorW)) return false;
        VectorW vectorW = (VectorW) o;
        return Double.compare(vectorW.getX(), getX()) == 0 && Double.compare(vectorW.getY(), getY()) == 0 && Double.compare(vectorW.getZ(), getZ()) == 0 && getWorld().getUID().equals(vectorW.getWorld().getUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorld().getUID(), getX(), getY(), getZ());
    }
}