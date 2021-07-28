# RESTful-BSG

An extension plugin for [BungeeSafeguard](https://github.com/Luluno01/BungeeSafeguard)
that exposes whitelist/blacklist manipulation APIs as RESTful API.

Tested on Waterfall, version `git:Waterfall-Bootstrap:1.17-R0.1-SNAPSHOT:93773f9:448`.

- [RESTful-BSG](#restful-bsg)
  - [Feature](#feature)
  - [Config](#config)
  - [API](#api)
    - [BungeeSafeguard Status](#bungeesafeguard-status)
    - [List Manipulation](#list-manipulation)
      - [Main List](#main-list)
      - [Lazy List](#lazy-list)

## Feature

Access whitelist/blacklist via RESTful API.

**WARNING**: this plugin includes NO ACCESS CONTROL, you must not expose the server to
untrusted public network.

## Config

```YAML
host: 127.0.0.1  # The host/interface to which the server should listen
port: 3090  # The port to which the server should listen
```

## API

### BungeeSafeguard Status

`GET /status`

Returns current status of BungeeSafeguard on success:

```TypeScript
interface PluginStatus {
    /**
     * The name of the config file in use
     */
    configInUse: string
    /**
     * String representation of the storage backend in use
     */
    backend: string
    /**
     * Enable states of the lists
     */
    listStates: {
        /**
         * The list name: enabled state
         */
        [name: string]: boolean
    }[]
}
```

### List Manipulation

#### Main List

`GET /whitelist`, `GET /blacklist`

Return full whitelist/blacklist on success:

```TypeScript
/**
 * Players' UUIDs
 */
type MainList = string[]
```

`GET /whitelist/{user}[?xbox=true|false|1|0]`, `GET /blacklist/{user}[?xbox=true|false|1|0]`

Return if given UUID or username is in the whitelist/blacklist on success (`boolean`).
Return `404` if given username is not found, or `400` for other errors.

`POST|PUT /whitelist/{user}[?xbox=true|false|1|0]`, `POST|PUT /blacklist/{user}[?xbox=true|false|1|0]`

Add given UUID or username to the whitelist/blacklist, return if given user is added (`true`) or already in the list (`false`).
Return `404` if given username is not found, or `400` for other errors.

`DELETE /whitelist/{user}[?xbox=true|false|1|0]`, `DELETE /blacklist/{user}[?xbox=true|false|1|0]`

Remove given UUID or username from the whitelist/blacklist, return if given user is removed (`true`) or not in the list in the first place (`false`).
Return `404` if given username is not found, or `400` for other errors.

#### Lazy List

`GET /lazy-whitelist`, `GET /lazy-blacklist`

Return full lazy-whitelist/lazy-blacklist on success:

```TypeScript
/**
 * Players' name
 */
type LazyList = string[]
```

`GET /lazy-whitelist/{username}`, `GET /lazy-blacklist/{username}`

Return if given username is in the lazy-whitelist/lazy-blacklist on success (`boolean`).

`POST|PUT /lazy-whitelist/{username}`, `POST|PUT /lazy-blacklist/{username}`

Add given username to the lazy-whitelist/lazy-blacklist, return if given UUID is added (`true`) or already in the list (`false`).

`DELETE /lazy-whitelist/{username}`, `DELETE /lazy-blacklist/{username}`

Remove given username from the lazy-whitelist/lazy-blacklist, return if given username is removed (`true`) or not in the list in the first place (`false`).
