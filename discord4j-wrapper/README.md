# discord4j-wrapper

By default, the mod JAR does not bundle any of its dependencies.
This works fine for the Minecraft game files, and the Fabric API, but not so well for Discord4J, which is required for the mod to run, but not provided in the server environment.

As a result, unless the mod user installs `discord4j-core-3.2.6.jar` on their server instance, the mod won't have access to what is arguably the most important dependency.
It will also require manually installing every single dependency of Discord4J, since JARs from the standard sources don't include transitive dependencies.
-------------------------------
To work around this issue, discord-verifier needs to build a very specific kind of fat JAR:
one which bundles Discord4J and all of its transitive dependencies **only**. The Fabric API, Minecraft libraries, etc. should never be bundled with the mod.

Neither of the standard fat JAR solutions (build.gradle `jar` task, shadowJar, Fabric Loom) provide a mechanism for bundling one specific dependency and its transitive dependencies, so a homebrew solution is needed to accomplish this.

`discord4j-wrapper` is a subproject of `discord-verifier` which serves only one purpose: to depend on `discord4j-core`.
It can be built as a basic "everything" fat JAR, and because it only depends on `discord4j-core`, that package and its dependencies are all it will contain in the end.

`discord-verifier` can then include `discord4j-wrapper` in the final mod JAR using Loom's `include` mechanism, and the wrapper will bring with it every transitive dependency of Discord4J.

### JAR Summary:

```
Discord Verifier
    ├── Mod Code
    ├── discord4j-wrapper
        └── discord4j-core
            ├── some
            ├── dependencies
            └── .....
    └── External Dependencies:
        ├── Fabric API
        └── Minecraft
```

