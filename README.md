# Past Death Ghost

Past Death Ghost is a client-side Fabric mod for Minecraft 1.21.11. When you die, it leaves a translucent ghost at the death location with your personalized death message above it.

## Features

- Spawns a translucent client-side ghost where you died.
- Shows the death message as the ghost's nametag.
- Stores recent death ghosts locally in the client config.
- Keeps ghosts separated by dimension.
- Adds `/ghosts clear` and `/ghosts limit <amount>` client commands.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.2 or newer
- Fabric API
- Java 21

## Build

```powershell
./gradlew build
```

The release jar is created at:

```text
build/libs/past_death_ghost-1.0.0.jar
```

## License

MIT. See [LICENSE](LICENSE).
