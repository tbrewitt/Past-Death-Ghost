package com.pastdeathghost.ghost;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

/**
 * Data class representing a recorded player death.
 * Stores the position, rotation, dimension, and player GameProfile for skin rendering.
 */
public class GhostData {
    private final UUID id;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final String dimension;
    private final GameProfile profile;
    private final String deathMessage;

    public GhostData(UUID id, double x, double y, double z, float yaw, float pitch, String dimension, GameProfile profile, String deathMessage) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
        this.profile = profile;
        this.deathMessage = deathMessage;
    }

    public UUID getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public String getDimension() {
        return dimension;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public GameProfile getProfile() {
        return profile;
    }

    /**
     * Converts the GhostData into a JsonObject for file persistence.
     */
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id.toString());
        obj.addProperty("x", x);
        obj.addProperty("y", y);
        obj.addProperty("z", z);
        obj.addProperty("yaw", yaw);
        obj.addProperty("pitch", pitch);
        obj.addProperty("dimension", dimension);
        obj.addProperty("deathMessage", deathMessage);

        JsonObject profObj = new JsonObject();
        profObj.addProperty("uuid", profile.id().toString());
        profObj.addProperty("name", profile.name());

        JsonArray propsArr = new JsonArray();
        profile.properties().forEach((name, prop) -> {
            JsonObject propObj = new JsonObject();
            propObj.addProperty("name", prop.name());
            propObj.addProperty("value", prop.value());
            if (prop.hasSignature()) {
                propObj.addProperty("signature", prop.signature());
            }
            propsArr.add(propObj);
        });
        profObj.add("properties", propsArr);
        obj.add("profile", profObj);

        return obj;
    }

    /**
     * Reconstructs GhostData from a JsonObject.
     */
    public static GhostData fromJson(JsonObject obj) {
        UUID id = UUID.fromString(obj.get("id").getAsString());
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        float yaw = obj.get("yaw").getAsFloat();
        float pitch = obj.get("pitch").getAsFloat();
        String dimension = obj.get("dimension").getAsString();
        String deathMessage = obj.has("deathMessage") ? obj.get("deathMessage").getAsString() : "Died here";

        JsonObject profObj = obj.getAsJsonObject("profile");
        UUID profUuid = UUID.fromString(profObj.get("uuid").getAsString());
        String name = profObj.get("name").getAsString();
        GameProfile profile = new GameProfile(profUuid, name);

        if (profObj.has("properties")) {
            JsonArray propsArr = profObj.getAsJsonArray("properties");
            for (int i = 0; i < propsArr.size(); i++) {
                JsonObject propObj = propsArr.get(i).getAsJsonObject();
                String propName = propObj.get("name").getAsString();
                String value = propObj.get("value").getAsString();
                String signature = propObj.has("signature") ? propObj.get("signature").getAsString() : null;
                if (signature != null) {
                    profile.properties().put(propName, new Property(propName, value, signature));
                } else {
                    profile.properties().put(propName, new Property(propName, value));
                }
            }
        }

        return new GhostData(id, x, y, z, yaw, pitch, dimension, profile, deathMessage);
    }
}
