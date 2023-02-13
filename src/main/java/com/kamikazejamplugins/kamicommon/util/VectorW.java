package com.kamikazejamplugins.kamicommon.util;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Objects;

@SuppressWarnings("unused")
@Data
public class VectorW {

    private transient World w;

    // DO NOT CHANGE NAME
    private String world;

    // DO NOT CHANGE NAME
    private double x;

    // DO NOT CHANGE NAME
    private double y;

    // DO NOT CHANGE NAME
    private double z;

    // DO NOT CHANGE NAME
    private float pitch;

    // DO NOT CHANGE NAME
    private float yaw;

    public VectorW(World world, Vector vector) {
        this.w = world;
        this.world = world.getName();
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
        this.yaw = 0;
        this.pitch = 0;
    }

    public VectorW(World world, double x, double y, double z) {
        this.w = world;
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public VectorW(World world, double x, double y, double z, float yaw, float pitch) {
        this.w = world;
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public VectorW(Location loc) {
        this.w = loc.getWorld();
        assert w != null;
        this.world = w.getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.pitch = loc.getPitch();
        this.yaw = loc.getYaw();
    }

    public World getW() {
        if (w != null) { return w; }
        return w = Bukkit.getWorld(world);
    }

    public Location toLocation() {
        return new Location(getW(), x, y, z, yaw, pitch);
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
        return Double.compare(vectorW.getX(), getX()) == 0 && Double.compare(vectorW.getY(), getY()) == 0 && Double.compare(vectorW.getZ(), getZ()) == 0 && getW().getUID().equals(vectorW.getW().getUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getW().getUID(), getX(), getY(), getZ());
    }




    // Time for some typical vector methods
    public VectorW add(VectorW vector) {
        return new VectorW(w, x + vector.getX(), y + vector.getY(), z + vector.getZ());
    }
    public VectorW subtract(VectorW vector) {
        return new VectorW(w, x - vector.getX(), y - vector.getY(), z - vector.getZ());
    }
    public VectorW multiply(VectorW vector) {
        return new VectorW(w, x * vector.getX(), y * vector.getY(), z * vector.getZ());
    }
    public VectorW divide(VectorW vector) {
        return new VectorW(w, x / vector.getX(), y / vector.getY(), z / vector.getZ());
    }
    public VectorW add(double x, double y, double z) {
        return new VectorW(w, this.x + x, this.y + y, this.z + z);
    }
    public VectorW subtract(double x, double y, double z) {
        return new VectorW(w, this.x - x, this.y - y, this.z - z);
    }
    public VectorW multiply(double m) {
        return new VectorW(w, this.x * m, this.y * m, this.z * m);
    }
    public VectorW divide(double d) {
        return new VectorW(w, this.x / d, this.y / d, this.z / d);
    }
    public VectorW multiply(double x, double y, double z) {
        return new VectorW(w, this.x * x, this.y * y, this.z * z);
    }
    public VectorW divide(double x, double y, double z) {
        return new VectorW(w, this.x / x, this.y / y, this.z / z);
    }
    public VectorW crossProduct(VectorW vector) {
        return new VectorW(w, y * vector.getZ() - z * vector.getY(), z * vector.getX() - x * vector.getZ(), x * vector.getY() - y * vector.getX());
    }
    public double dotProduct(VectorW vector) {
        return x * vector.getX() + y * vector.getY() + z * vector.getZ();
    }
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }
    public double length() {
        return Math.sqrt(lengthSquared());
    }
    public VectorW normalize() {
        return divide(length());
    }
    public VectorW zero() {
        return new VectorW(w, 0, 0, 0);
    }
    public VectorW copy() {
        return new VectorW(w, x, y, z, pitch, yaw);
    }

}