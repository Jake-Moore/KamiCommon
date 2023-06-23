package com.kamikazejamplugins.kamicommon.util;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Objects;

@SuppressWarnings("unused")
@Data
public class Vector2D {

    // DO NOT CHANGE NAME
    private double x;

    // DO NOT CHANGE NAME
    private double z;

    public Vector2D(Vector vector) {
        this.x = vector.getX();
        this.z = vector.getZ();
    }

    public Vector2D(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public Vector2D(double x, double z, float yaw, float pitch) {
        this.x = x;
        this.z = z;
    }

    public Vector2D(Location loc) {
        this.x = loc.getX();
        this.z = loc.getZ();
    }

    public Location toLocation(World w) {
        return new Location(w, x, 0, z, 0, 0);
    }

    public Location toLocation(World w, double y) {
        return new Location(w, x, y, z, 0, 0);
    }

    public Location toLocation(World w, double y, float yaw, float pitch) {
        return new Location(w, x, 0, z, yaw, pitch);
    }

    public Vector toVector() { return new Vector(x, 0, z); }
    public Vector toVector(double y) { return new Vector(x, y, z); }

    public double distanceSquared(Vector2D vector) {
        double dx = vector.getX() - x;
        double dz = vector.getZ() - z;
        return dx * dx + dz * dz;
    }

    public double distance(Vector2D vector) {
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
        if (!(o instanceof Vector2D)) return false;
        Vector2D vectorW = (Vector2D) o;
        return Double.compare(vectorW.getX(), getX()) == 0 && Double.compare(vectorW.getZ(), getZ()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getZ());
    }




    // Time for some typical vector methods
    public Vector2D add(Vector2D vector) {
        return new Vector2D(x + vector.getX(), z + vector.getZ());
    }
    public Vector2D subtract(Vector2D vector) {
        return new Vector2D(x - vector.getX(), z - vector.getZ());
    }
    public Vector2D multiply(Vector2D vector) {
        return new Vector2D(x * vector.getX(), z * vector.getZ());
    }
    public Vector2D divide(Vector2D vector) {
        return new Vector2D(x / vector.getX(), z / vector.getZ());
    }
    public Vector2D add(double x, double z) {
        return new Vector2D(this.x + x, this.z + z);
    }
    public Vector2D subtract(double x, double z) {
        return new Vector2D(this.x - x, this.z - z);
    }
    public Vector2D multiply(double m) {
        return new Vector2D(this.x * m, this.z * m);
    }
    public Vector2D divide(double d) {
        return new Vector2D(this.x / d, this.z / d);
    }
    public Vector2D multiply(double x, double z) {
        return new Vector2D(this.x * x, this.z * z);
    }
    public Vector2D divide(double x, double z) {
        return new Vector2D(this.x / x, this.z / z);
    }
    public double lengthSquared() {
        return x * x + z * z;
    }
    public double length() {
        return Math.sqrt(lengthSquared());
    }
    public Vector2D normalize() {
        return divide(length());
    }
    public Vector2D zero() {
        return new Vector2D(0, 0);
    }
    public Vector2D copy() {
        return new Vector2D(x, z);
    }
}