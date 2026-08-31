# KamiCommon

A Spigot and Paper library. One artifact serves Minecraft 1.8.8 through 26.2, and every module targets
Java 8. See the [wiki](https://github.com/Jake-Moore/KamiCommon/wiki) for what it does; this file is
only for things that are not obvious from the code.

## Writing

Apply <https://github.com/cursor/plugins/blob/main/pstack/skills/unslop/SKILL.md> to commit messages,
PR and issue bodies, code comments and javadoc. **Zero em dashes anywhere.** Formal and impersonal, no
marketing adjectives, no narrating the investigation that produced a change.

Commit messages end with exactly this and nothing else:

```
Co-Authored-By: Claude Code <noreply@anthropic.com>
```

No model name, no context window, no session link. History was rewritten once to remove those; do not
reintroduce them.

## Building

**`sh gradlew`, never `./gradlew`.** `gradlew` is tracked mode 644, so the direct form fails.

## Releasing

`VERSION` lives at `build.gradle.kts:12`. An `-alpha.N` suffix cuts a prerelease; a bare version cuts a
release.

The publish is guarded against overwriting an existing coordinate, so **pushing again without bumping
`VERSION` silently skips** rather than failing. If a release seems not to have happened, check the
version first.

**Verify a release by downloading it, and confirm the previous version still returns 200 at its
original size.** A green CI run is not evidence that the artifact changed, and an overwritten
predecessor is a failure that looks like a success.

A CI failure in `aggregateJavadoc` fetching `docs.jake-moore.dev` is transient: that site redeploys
after a `spigot-nms` release and the link check fails mid-flight. Re-run the job. It fails `setup` and
therefore skips `prerelease`, so nothing publishes and it reads like a build break when it is not.

## Verifying behaviour

`/kc texttest` is the real check: it asserts on what a serializer actually emits, per version, and runs
from a **console** on any server. `/kc nmstest` covers the NMS capabilities and **needs a player**.

The `gradle/verify-*.gradle.kts` tasks check packaging, not behaviour. They all passed while `click()`
reached no client below 1.18.2 for nine releases.

## Branches

- `release/v5` is the working line and does all releasing, prerelease and final.
- `main` is the continuous timeline, synced from `release/v5` by a workflow. Do not commit to it.
- `release/v4` and `release/v3` are archives. Changes there are direct commits that never merge
  anywhere.

Direct pushes to `release/v5` bypass the review ruleset. Prefer a PR for anything user-visible.
