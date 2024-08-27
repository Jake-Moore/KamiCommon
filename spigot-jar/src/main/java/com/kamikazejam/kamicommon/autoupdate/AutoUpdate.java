package com.kamikazejam.kamicommon.autoupdate;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.configuration.ConfigHelper;
import com.kamikazejam.kamicommon.gson.JsonArray;
import com.kamikazejam.kamicommon.gson.JsonElement;
import com.kamikazejam.kamicommon.gson.JsonObject;
import com.kamikazejam.kamicommon.gson.JsonParser;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.util.data.SimpleStringCoder;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class, primarily for KamikazeJAM_YT's plugins, allowing them to be automatically updated
 * (By streaming update files into the Bukkit "update-folder", normally the `/plugins/update` folder)
 * When a file is added here, it will replace the file with the same name in /plugins automatically next boot.
 * This utility also tries to rename the current jar to .old while placing the new jar into /plugins if the names do not match.
 *   This allows linux machines to update jars that have different versions seamlessly
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class AutoUpdate implements Listener {
    private static final Set<String> updatedPlugins = new HashSet<>();
    private static final String BASE_URL = "https://api.github.com/repos/KamiUpdates/AutoUpdate/releases/tags/";
    //Access token of the KamiUpdates machine user (second account)
    //It can only see the empty repository for AutoUpdate, only seeing releases I put there
    //This is "encrypted" only to stop GitHub from automatically revoking it, I realize it's not anymore "secure"
    private static final String tokenEnc = "6N5f9=4g7M7WURp[]J5QPNLQIRHXVpJRU=oV|{YRvFLVl9Yg4N[[|M~Y{JIirRnf8UqZ4UZYsZY^JF7\\9pY]|VISMRJVI|Ih{oIYPlpU~IYQ|<JhlF7\\mZLe4p6^";

    private static boolean debug = false;
    private static AutoUpdateListeners listeners = null;

    private static String getToken() {
        return SimpleStringCoder.CaesarCipherDecrypt(tokenEnc);
    }

    public static void update(JavaPlugin plugin) {
        update(plugin, false);
    }
    public static void update(JavaPlugin plugin, boolean debugMessages) {
        plugin.getLogger().info("[AutoUpdate] Running auto update task in 5 seconds (debug: " + debugMessages + ")");
        new BukkitRunnable() {
            @Override
            public void run() {
                grabLatest(plugin, debugMessages);
            }
        }.runTaskLater(plugin, 100L);
    }

    public static void updateNow(JavaPlugin plugin) {
        updateNow(plugin, false);
    }

    public static void updateNow(JavaPlugin plugin, boolean debugMessages) {
        plugin.getLogger().info("[AutoUpdate] Running auto update task now! (debug: " + debugMessages + ")");
        grabLatest(plugin, debugMessages);
    }

    public static boolean hasPluginUpdates() {
        return !updatedPlugins.isEmpty();
    }

    private static void grabLatest(JavaPlugin plugin, boolean debugMessages) {
        //This just helps ensure it's been configured properly
        if (listeners == null) {
            listeners = new AutoUpdateListeners(plugin);
        }
        debug = debugMessages;

        String projectName = plugin.getName();

        //Files can be large, let's not hold up the main thread please
        new Thread(() -> {
            try {
                Pair<JsonObject, JsonObject> urlData = grabUrl(projectName);
                if (urlData == null) {
                    plugin.getLogger().severe("[AutoUpdate] Error finding latest java jar from release (was null)");
                    return;
                }
                if (debug) { plugin.getLogger().info("[AutoUpdate] Grabbed " + projectName + "'s Download Url: " + urlData.getB().get("url").getAsString()); }

                boolean updated = updateFile(urlData, plugin);
                if (!updated) { return; }
                updatedPlugins.add(plugin.getName());
                plugin.getLogger().info(StringUtil.t("[AutoUpdate] &aUpdate successfully downloaded, it will be effective next restart!"));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //Returns a pair of the Main JsonObject, and then the specific asset data JsonObject
    private static @Nullable Pair<JsonObject, JsonObject> grabUrl(String projectName) throws IOException {
        //OkHttp doesn't authenticate properly with this specific api call, idk
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(BASE_URL + projectName);
            //System.out.println(BASE_URL + projectName);
            request.addHeader("Accept", "application/vnd.github+json");
            request.addHeader("Authorization", "Bearer " + getToken());

            return httpClient.execute(request, result -> {
                if (result.getCode() != 200) {
                    throw new IOException("Unexpected code " + result.getCode());
                }

                String j = EntityUtils.toString(result.getEntity(), "UTF-8");
                JsonObject jsonObject = JsonParser.parseString(j).getAsJsonObject();
                JsonArray array = jsonObject.getAsJsonArray("assets");

                JsonObject base = null;
                for (JsonElement e : array) {
                    JsonObject file = e.getAsJsonObject();
                    if (file.get("content_type").getAsString().equals("application/java-archive")) {
                        // Ignore all original jars, they aren't shaded
                        if (file.get("name").getAsString().startsWith("original-")) { continue; }

                        // If we find an -obf jar, send it immediately
                        if (file.get("name").getAsString().endsWith("-obf.jar")) {
                            return Pair.of(jsonObject, file);
                        }
                        base = file;
                    }
                }

                // If we didn't find an -obf jar, we try to send the normal jar
                if (base == null) { return null; }
                return Pair.of(jsonObject, base);
            });
        }
    }

    private static void testCopyJar(Pair<JsonObject, JsonObject> dataPair, File file) throws IOException {
        JsonObject jsonObject = dataPair.getA();
        JsonObject dataJson = dataPair.getB();
        String downloadUrl = dataJson.get("url").getAsString();
        // System.out.println(downloadUrl);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(downloadUrl);
            request.addHeader("Accept", "application/octet-stream");
            request.addHeader("Authorization", "Bearer " + getToken());
            httpClient.execute(request, response -> {
                if (response.getCode() != 200) {
                    throw new IOException("Unexpected code " + response.getCode());
                }

                InputStream in = response.getEntity().getContent();
                copyToFile(in, new File("C:\\Users\\Jake\\Desktop\\test.jar"));
                return null;
            });
        }
    }

    private static boolean updateFile(Pair<JsonObject, JsonObject> dataPair, JavaPlugin plugin) throws IOException {
        JsonObject jsonObject = dataPair.getA();
        JsonObject dataJson = dataPair.getB();
        String downloadUrl = dataJson.get("url").getAsString();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(downloadUrl);
            request.addHeader("Accept", "application/octet-stream");
            request.addHeader("Authorization", "Bearer " + getToken());

            InputStream in = httpClient.execute(request, response -> {
                if (response.getCode() != 200) {
                    throw new IOException("Unexpected code " + response.getCode());
                }

                return response.getEntity().getContent();
            });

            //Grab the current jar File from the plugin, to determine its name
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(plugin);
            if (debug) { plugin.getLogger().info("[AutoUpdate] Found Plugin jar at: " + file.getAbsolutePath()); }
            String currJarName = file.getName();
            if (debug) { plugin.getLogger().info("[AutoUpdate] Determined jar name to be: " + currJarName); }

            //Grab some folders and ensure the update folder exists
            File updateFolder = new File(plugin.getDataFolder().getParent() + File.separator + "update");
            if (!updateFolder.exists()) {
                if (!updateFolder.mkdirs()) {
                    plugin.getLogger().warning("[AutoUpdate] Could not create update folder directory!");
                    return false;
                }
            }
            //Data variables about this asset release
            int latestAssetId = jsonObject.get("id").getAsInt();
            String updatedTimestamp = (dataJson.has("updated_at") ? dataJson.get("updated_at").getAsString() : dataJson.get("created_at").getAsString());

            File kamicommon = PluginSource.get().getDataFolder();
            FileConfiguration data = ConfigHelper.createConfig(plugin, kamicommon, "updater.yml");

            if (data.contains(plugin.getName())) {
                //Check that the current download is of the same asset from the release
                int latestDownload = data.getInt(plugin.getName());
                if (latestAssetId == latestDownload) {
                    //Check that the current download has not been updated
                    String currUpdatedTimestamp = data.getString(plugin.getName()+"Updated", "null");
                    if (updatedTimestamp.equals(currUpdatedTimestamp)) {
                        plugin.getLogger().info(StringUtil.t("[AutoUpdate] &aThis plugin is up to date!"));
                        return false;
                    }
                }
            }

            //If we are sure the version is not the same, then save the data about this new version
            data.set(plugin.getName(), latestAssetId);
            data.set(plugin.getName()+"Updated", updatedTimestamp);
            ConfigHelper.saveConfig(plugin, kamicommon, data, "updater.yml");

            //If the newName isn't the same as the old name, it won't update via the update folder, cancel
            String newJarName = dataJson.get("name").getAsString();
            newJarName = newJarName.replace("-obf.jar", ".jar");

            if (!newJarName.equals(currJarName)) {
                plugin.getLogger().info("[AutoUpdate] New and current version mismatch detected. New: " + newJarName + " Current: " + currJarName);

                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    plugin.getLogger().severe("[AutoUpdate] Windows system detected while trying to update a new version number. Windows won't allow file renaming during runtime. Please find the latest plugin jar (.pending file extension) and enable it manually.");
                    File updateJar = new File(file.getParent() + File.separator + newJarName + ".pending");
                    copyToFile(in, updateJar);
                    return false;
                }

                //Rename the jar file to .old so it won't load next time
                File offFile = new File(file.getParent() + File.separator + file.getName() + ".old");
                Files.move(Paths.get(file.getAbsolutePath()), Paths.get(offFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

                //Upload the new file which will work after next restart
                File updateJar = new File(file.getParent() + File.separator + newJarName);
                copyToFile(in, updateJar);
                return true;
            }

            //Move the jar into updates so it will be updated
            File updateJar = new File(updateFolder.getAbsolutePath() + File.separator + currJarName);
            copyToFile(in, updateJar);
            return true;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyToFile(InputStream initialStream, File targetFile) throws IOException {
        Files.copy(initialStream, Paths.get(targetFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void notify(Player player) {
        if (!hasPluginUpdates()) { return; }

        String names = Arrays.toString(updatedPlugins.toArray());
        player.sendMessage(StringUtil.t("&c&lThere are new updates for '" + names + "'! These updates should automatically load on the next restart."));
    }



//    public static void main(String[] args) {
//        try {
//            Pair<JsonObject, JsonObject> a = grabUrl("FriendlyRaids");
//            if (a == null) {
//                System.out.println("null");
//                return;
//            }
//            System.out.println(a.getB().get("url").getAsString());
//
//            testCopyJar(a, new File("C:\\Users\\Jake\\Desktop\\test.jar"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
