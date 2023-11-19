package com.kamikazejam.kamicommon.util;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Objects;

@SuppressWarnings("unused")
@Data
public class VectorW2D {

    private transient World w;

    // DO NOT CHANGE NAME
    private String world;

    // DO NOT CHANGE NAME
    private double x;

    // DO NOT CHANGE NAME
    private double z;

    public VectorW2D(World world, Vector vector) {
        this.w = world;
        this.world = world.getName();
        this.x = vector.getX();
        this.z = vector.getZ();
    }

    public VectorW2D(World world, double x, double z) {
        this.w = world;
        this.world = world.getName();
        this.x = x;
        this.z = z;
    }

    public VectorW2D(World world, double x, double z, float yaw, float pitch) {
        this.w = world;
        this.world = world.getName();
        this.x = x;
        this.z = z;
    }

    public VectorW2D(Location loc) {
        this.w = loc.getWorld();
        assert w != null;
        this.world = w.getName();
        this.x = loc.getX();
        this.z = loc.getZ();
    }

    public World getW() {
        if (w != null) { return w; }
        return w = Bukkit.getWorld(world);
    }

    public Location toLocation() {
        return new Location(getW(), x, 0, z, 0, 0);
    }

    public Location toLocation(double y) {
        return new Location(getW(), x, y, z, 0, 0);
    }

    public Location toLocation(double y, float yaw, float pitch) {
        return new Location(getW(), x, 0, z, yaw, pitch);
    }

    public Vector toVector() { return new Vector(x, 0, z); }
    public Vector toVector(double y) { return new Vector(x, y, z); }

    public double distanceSquared(VectorW2D vector) {
        double dx = vector.getX() - x;
        double dz = vector.getZ() - z;
        return dx * dx + dz * dz;
    }

    public double distance(VectorW2D vector) {
        return Math.sqrt(distanceSquared(vector));
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VectorW2D)) return false;
        VectorW2D vectorW = (VectorW2D) o;
        return Double.compare(vectorW.getX(), getX()) == 0 && Double.compare(vectorW.getZ(), getZ()) == 0 && getW().getUID().equals(vectorW.getW().getUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getW().getUID(), getX(), getZ());
    }




    // Time for some typical vector methods
    public VectorW2D add(VectorW2D vector) {
        return new VectorW2D(w, x + vector.getX(), z + vector.getZ());
    }
    public VectorW2D subtract(VectorW2D vector) {
        return new VectorW2D(w, x - vector.getX(), z - vector.getZ());
    }
    public VectorW2D multiply(VectorW2D vector) {
        return new VectorW2D(w, x * vector.getX(), z * vector.getZ());
    }
    public VectorW2D divide(VectorW2D vector) {
        return new VectorW2D(w, x / vector.getX(), z / vector.getZ());
    }
    public VectorW2D add(double x, double z) {
        return new VectorW2D(w, this.x + x, this.z + z);
    }
    public VectorW2D subtract(double x, double z) {
        return new VectorW2D(w, this.x - x, this.z - z);
    }
    public VectorW2D multiply(double m) {
        return new VectorW2D(w, this.x * m, this.z * m);
    }
    public VectorW2D divide(double d) {
        return new VectorW2D(w, this.x / d, this.z / d);
    }
    public VectorW2D multiply(double x, double z) {
        return new VectorW2D(w, this.x * x, this.z * z);
    }
    public VectorW2D divide(double x, double z) {
        return new VectorW2D(w, this.x / x, this.z / z);
    }
    public double lengthSquared() {
        return x * x + z * z;
    }
    public double length() {
        return Math.sqrt(lengthSquared());
    }
    public VectorW2D normalize() {
        return divide(length());
    }
    public VectorW2D zero() {
        return new VectorW2D(w, 0, 0);
    }
    public VectorW2D copy() {
        return new VectorW2D(w, x, z);
    }
}