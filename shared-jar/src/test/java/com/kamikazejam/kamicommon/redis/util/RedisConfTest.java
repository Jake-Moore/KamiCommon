package com.kamikazejam.kamicommon.redis.util;

import io.lettuce.core.RedisCredentials;
import io.lettuce.core.RedisURI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedisConfTest {

    private static RedisCredentials credentials(RedisConf conf) {
        return conf.getURI().getCredentialsProvider().resolveCredentials().block();
    }

    @Nested
    @DisplayName("fromUrl accepts")
    class Accepts {
        @Test
        void hostOnly() {
            RedisConf c = RedisConf.fromUrl("redis://redis.internal");
            assertEquals("redis.internal", c.getAddress());
            assertEquals(6379, c.getPort());
            assertNull(c.getUsername());
            assertNull(c.getPassword());
            assertEquals(0, c.getDatabase());
            assertFalse(c.isSsl());
        }

        @Test
        void hostAndPort() {
            RedisConf c = RedisConf.fromUrl("redis://redis.internal:6380");
            assertEquals(6380, c.getPort());
        }

        @Test
        @DisplayName("password alone, the pre-ACL form")
        void passwordOnly() {
            RedisConf c = RedisConf.fromUrl("redis://:s3cret@redis.internal");
            assertNull(c.getUsername());
            assertEquals("s3cret", c.getPassword());
        }

        @Test
        @DisplayName("username and password, the ACL form")
        void usernameAndPassword() {
            RedisConf c = RedisConf.fromUrl("redis://transcripts:s3cret@redis.internal:6380/3");
            assertEquals("transcripts", c.getUsername());
            assertEquals("s3cret", c.getPassword());
            assertEquals("redis.internal", c.getAddress());
            assertEquals(6380, c.getPort());
            assertEquals(3, c.getDatabase());
        }

        @Test
        @DisplayName("rediss:// selects TLS rather than being ignored")
        void tls() {
            RedisConf c = RedisConf.fromUrl("rediss://transcripts:s3cret@redis.internal");
            assertTrue(c.isSsl());
            assertTrue(c.getURI().isSsl(), "the URI handed to lettuce must carry TLS too");
        }

        @Test
        void databaseFromPath() {
            assertEquals(2, RedisConf.fromUrl("redis://redis.internal/2").getDatabase());
            assertEquals(0, RedisConf.fromUrl("redis://redis.internal/").getDatabase());
        }

        @Test
        @DisplayName("a username with no password keeps the username")
        void usernameWithoutPassword() {
            // Lettuce's own parser drops BOTH halves for this input, which would quietly downgrade
            // the connection to the default user. Ours keeps the username.
            RedisConf c = RedisConf.fromUrl("redis://transcripts:@redis.internal");
            assertEquals("transcripts", c.getUsername());
            assertNull(c.getPassword());
            RedisCredentials creds = credentials(c);
            assertTrue(creds.hasUsername(), "the username must survive into the lettuce URI");
            assertEquals("transcripts", creds.getUsername());
        }

        @Test
        void surroundingWhitespaceIsTolerated() {
            assertEquals("redis.internal", RedisConf.fromUrl("  redis://redis.internal  ").getAddress());
        }
    }

    @Nested
    @DisplayName("fromUrl rejects")
    class Rejects {
        private void rejected(String url) {
            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> RedisConf.fromUrl(url));
            assertFalse(
                    ex.getMessage().contains("s3cret"),
                    "the failure message must never carry the password: " + ex.getMessage()
            );
        }

        @Test
        @DisplayName("a colon-less credential, which would silently authenticate as default")
        void ambiguousCredential() {
            rejected("redis://transcripts@redis.internal");
        }

        @Test
        void unknownScheme() {
            rejected("http://redis.internal");
            rejected("redis.internal:6379");
        }

        @Test
        @DisplayName("schemes this type cannot represent")
        void sentinelAndSocket() {
            rejected("redis-sentinel://redis.internal:26379/0#mymaster");
            rejected("redis-socket:///var/run/redis.sock");
        }

        @Test
        void nonNumericDatabase() {
            rejected("redis://redis.internal/production");
        }

        @Test
        void negativeDatabase() {
            rejected("redis://redis.internal/-1");
        }

        @Test
        @DisplayName("query parameters, rather than accepting and ignoring them")
        void queryParameters() {
            rejected("redis://redis.internal?timeout=5s");
        }

        @Test
        void noHost() {
            rejected("redis://");
        }

        @Test
        @DisplayName("and the message never leaks the password")
        void messageIsRedacted() {
            rejected("redis://transcripts@redis.internal:s3cret");
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> RedisConf.fromUrl("redis://user:s3cret@redis.internal/nope")
            );
            assertFalse(ex.getMessage().contains("s3cret"), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("getURI carries")
    class Uri {
        @Test
        void passwordOnlyAuthenticatesAsDefault() {
            RedisCredentials creds = credentials(RedisConf.of("h", 6379, "s3cret"));
            assertFalse(creds.hasUsername());
            assertTrue(creds.hasPassword());
            assertEquals("s3cret", new String(creds.getPassword()));
        }

        @Test
        void usernameAndPassword() {
            RedisCredentials creds = credentials(new RedisConf("h", 6379, "transcripts", "s3cret"));
            assertTrue(creds.hasUsername());
            assertEquals("transcripts", creds.getUsername());
            assertEquals("s3cret", new String(creds.getPassword()));
        }

        @Test
        void databaseAndTls() {
            RedisURI uri = new RedisConf("h", 6380, "u", "p", 4, true).getURI();
            assertEquals(4, uri.getDatabase());
            assertTrue(uri.isSsl());
            assertEquals(6380, uri.getPort());
        }

        @Test
        @DisplayName("no credentials at all when none were configured")
        void noAuth() {
            RedisCredentials creds = credentials(RedisConf.of("h"));
            assertFalse(creds.hasUsername());
            assertFalse(creds.hasPassword());
        }
    }

    @Nested
    @DisplayName("value semantics, which the connector's cache depends on")
    class Value {
        @Test
        void equalConfigsShareACacheEntry() {
            Map<RedisConf, String> cache = new HashMap<>();
            cache.put(new RedisConf("h", 6379, "u", "p"), "client");
            // A separate instance describing the same connection must find it. Before RedisConf had
            // equals/hashCode this missed, and the connector built a second lettuce client.
            assertEquals("client", cache.get(new RedisConf("h", 6379, "u", "p")));
        }

        @Test
        @DisplayName("a different username is a different identity and must not share")
        void usernameSeparatesIdentities() {
            RedisConf transcripts = new RedisConf("h", 6379, "transcripts", "p");
            RedisConf metrics = new RedisConf("h", 6379, "metrics", "p");
            assertNotEquals(transcripts, metrics);
            assertNotEquals(transcripts, RedisConf.of("h", 6379, "p"));

            Map<RedisConf, String> cache = new HashMap<>();
            cache.put(transcripts, "a");
            cache.put(metrics, "b");
            assertEquals(2, cache.size());
        }

        @Test
        void databaseAndTlsSeparateIdentities() {
            assertNotEquals(new RedisConf("h", 6379, null, "p", 0, false), new RedisConf("h", 6379, null, "p", 1, false));
            assertNotEquals(new RedisConf("h", 6379, null, "p", 0, false), new RedisConf("h", 6379, null, "p", 0, true));
        }

        @Test
        @DisplayName("the legacy three-argument constructor still means what it did")
        void backwardsCompatible() {
            RedisConf legacy = new RedisConf("h", 6379, "s3cret");
            assertNull(legacy.getUsername());
            assertEquals("s3cret", legacy.getPassword());
            assertEquals(0, legacy.getDatabase());
            assertFalse(legacy.isSsl());
            assertEquals(legacy, RedisConf.of("h", 6379, "s3cret"));
        }

        @Test
        @DisplayName("an empty username or password is the same as none")
        void emptyIsAbsent() {
            assertNull(new RedisConf("h", 6379, "", "").getUsername());
            assertNull(new RedisConf("h", 6379, "", "").getPassword());
            assertEquals(RedisConf.of("h"), new RedisConf("h", 6379, "", ""));
        }

        @Test
        @DisplayName("toString never prints the password")
        void toStringRedacts() {
            String s = new RedisConf("h", 6379, "transcripts", "s3cret", 2, true).toString();
            assertFalse(s.contains("s3cret"), s);
            assertTrue(s.contains("transcripts"), s);
            assertTrue(s.contains("rediss"), s);
        }

        @Test
        void sameInstanceEqualsItself() {
            RedisConf c = RedisConf.of("h");
            assertSame(c, c);
            assertEquals(c, c);
            assertFalse(c.equals(null));
            assertNotEquals(c, "not a conf");
        }
    }
}
