# DiscordVerifier: Gatekeep your Minecraft server!

DiscordVerifier is a convenient way for Minecraft server admins to restrict access to a limited group of individuals without having to deal with the hassle of a whitelist.

Some example use cases include hosting a Minecraft server for members of a patrons-only Discord server, or for a school club.

## Usage

For a user to gain access to the server, they simply need to use the `/verify {username}` Discord command with their Minecraft username. Each Discord user can only have one Minecraft account.

DiscordVerifier is a purely server-side mod, so no changes to the Vanilla client will be necessary to join a server running DiscordVerifier.

## Setup

Download the appropriate mod version and place it in your server's `mods` folder.
The mod depends on the Fabric API, so be sure that is also installed.

Next, register a new Discord app through the [Discord developer portal](https://discord.com/developers/applications?new_application=true).
You may want to make the bot private, so that server members cannot add it to arbitrary other servers which you do not run.

Copy the bot token from the *Bot* subsection and paste it into the config file on the server.
Until you do this, DiscordVerifier __will not work__. The bot will appear as offline on Discord until the server is running and the token is valid.
Be sure never to share this token where anyone else can see it, or anyone will be able to take control of your bot.

Users will now be required to verify on the Discord server using the `/verify {username}` command before they are allowed to access the Minecraft server.

## Configuration

- `geyserUserPrefix`: Used to support servers running [GeyserMC/Floodgate](https://geysermc.org/). Defaults to "" and does nothing.
- `discordToken`: The Discord bot token generated from the Developer interface.

## Current Shortcomings

1. Any Java user whose name happens to be the combination of the Geyser user prefix and the name of a different verified Java account will be allowed on the server.
2. There is no option to configure how many usernames each Discord user can verify. It's one, and one only.
3. UserManager does not use threadsafe I/O, and it is used potentially simultaneously by both UserManager and DiscordVerifier; however, this is unlikely to *ever* cause a problem.