package com.kamikazejam.kamicommon.util;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.Color;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple class for sending discord webhooks, supports basic embeds and such
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class DiscordWebhook {

    private final String url;

    // Hopefully this will sanitize the content
    @Setter
    private String content;
    @Setter
    private String username;
    @Setter
    private String avatarUrl;
    @Setter
    private boolean tts;

    private final List<EmbedObject> embeds = new ArrayList<>();
    private final List<FileObject> files = new ArrayList<>();

    /**
     * Constructs a new DiscordWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void addFileObject(FileObject file) {
        this.files.add(file);
    }

    public void addTextFile(File file) {
        this.files.add(new FileObject(file, file.getName(), "text/plain"));
    }



    public JSONObject buildJson() {
        JSONObject json = new JSONObject();

        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();

            for (EmbedObject embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();

                jsonEmbed.put("title", embed.getTitle());
                jsonEmbed.put("description", embed.getDescription());
                jsonEmbed.put("url", embed.getUrl());
                if (embed.timeStamp) {
                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                    df.setTimeZone(tz);
                    String nowAsISO = df.format(new Date());

                    jsonEmbed.put("timestamp", nowAsISO);
                }

                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();

                    jsonEmbed.put("color", rgb);
                }

                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();

                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();

                    jsonFooter.put("text", footer.getText());
                    jsonFooter.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonFooter);
                }

                if (image != null) {
                    JSONObject jsonImage = new JSONObject();

                    jsonImage.put("url", image.getUrl());
                    jsonEmbed.put("image", jsonImage);
                }

                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();

                    jsonThumbnail.put("url", thumbnail.getUrl());
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }

                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();

                    jsonAuthor.put("name", author.getName());
                    jsonAuthor.put("url", author.getUrl());
                    jsonAuthor.put("icon_url", author.getIconUrl());
                    jsonEmbed.put("author", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();

                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());

                    jsonFields.add(jsonField);
                }

                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }
        return json;
    }

    public void execute() throws IOException, URISyntaxException {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
        JSONObject json = buildJson();

        // If there are no files, then just send the json
        if (files.isEmpty()) {
            executeJson(json);
        }else {
            executeWithFiles(json);
        }
    }

    private void executeJson(JSONObject json) throws IOException, URISyntaxException {
        URL url = new URI(this.url).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(json.toString().getBytes(StandardCharsets.UTF_8));
        }

        // Handle response (if needed)
        connection.getInputStream().close(); // Close the input stream
        connection.disconnect();
    }

    private void executeWithFiles(JSONObject json) throws IOException, URISyntaxException {
        // Create a boundary for multipart content
        String boundary = "*****";

        URL url = new URI(this.url).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        try (OutputStream outputStream = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

            // Write JSON payload
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"payload_json\"\r\n\r\n");
            writer.append(json.toString()).append("\r\n");

            // Write files
            for (FileObject file : files) {
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(file.getFileName()).append("\"\r\n");
                writer.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
                writer.flush();

                try (InputStream fileInputStream = Files.newInputStream(file.getFile().toPath())) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }

                writer.append("\r\n");
            }

            writer.append("--").append(boundary).append("--\r\n");
            writer.flush();
        }

        // Handle response (if needed)
        connection.getInputStream().close(); // Close the input stream
        connection.disconnect();
    }




    public static class EmbedObject {
        @Getter
        private String title;
        @Getter
        private String description;
        @Getter
        private String url;
        @Getter
        private Color color;

        @Getter
        private Footer footer;
        @Getter
        private Thumbnail thumbnail;
        @Getter
        private Image image;
        @Getter
        private Author author;
        private boolean timeStamp;
        @Getter
        private final List<Field> fields = new ArrayList<>();

        public EmbedObject setTimeStamp() {
            this.timeStamp = true;
            return this;
        }

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        private static class Footer {
            private final String text;
            private final String iconUrl;

            private Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            private String getText() {
                return text;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Thumbnail {
            private final String url;

            private Thumbnail(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Image {
            private final String url;

            private Image(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Author {
            private final String name;
            private final String url;
            private final String iconUrl;

            private Author(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            private String getName() {
                return name;
            }

            private String getUrl() {
                return url;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Field {
            private final String name;
            private final String value;
            private final boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return name;
            }

            private String getValue() {
                return value;
            }

            private boolean isInline() {
                return inline;
            }
        }
    }

    @Getter
    public static class FileObject {
        private final File file;
        private final String fileName;
        private final String contentType;

        public FileObject(File file, String fileName, String contentType) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
        }
    }
}