# oauth-register-broker

Automatically register a Cloud Foundry app to an oauth2 provider.

Currently supported drivers:

- uaa
- Github (**Note**: support two factors authentication)
- PingFederate

## Requirements

- [Cloud Foundry](http://cloudfoundry.org/) (>=192) with service broker api 2.6 at least
- A database service to store model (e.g: [p-mysql](http://docs.pivotal.io/p-mysql/), [cleardb](http://docs.pivotal.io/p-mysql/))

## How it works

When a user (a developer) would like to register one of his Cloud Foundry app it will be able to create an oauth-register service and choose in which provider it will be registered by choosing the plan.

This created will do nothing when it will be created, only save `provider_username` and `provider_password` parameters set by user if there is (these values will be encrypted in database).

When the user will bind this new service to an app the service broker will registered his app to the oauth provider he chose.
User can set these parameter (they are all optional) when he binds his app **scopes**, **grant_type**, **calback_urls** and **app_uris** (Only if the admin of oauth-register didn't set `cf_admin_user`, `cf_admin_password` and `cloud_controller_url` and it will not be optional)

The broker will register his app to the oauth provider and will give this json (example for uaa provider):

```json
{
 "VCAP_SERVICES": {
  "oauth-register": [
   {
    "credentials": {
     "tokenUri": "https://uaa.api.net/oauth/token",
     "clientId": "myclient-niKO6CJSKQ8NM7",
     "clientSecret": "KSJLKF5s45e",
     "grantTypes": [
      "authorization_code"
     ],
     "scopes": [
      "openid"
     ],
     "authorizationUri": "https://uaa.api.net/oauth/authorize",
     "userInfoUri": "https://uaa.api.net/userinfo"
    },
    "label": "oauth-register-dev",
    "name": "myoauth",
    "plan": "uaa",
    "provider": null,
    "syslog_drain_url": null,
    "tags": [
     "oauth",
     "sso"
    ]
   }
  ]
 }
}
```

## Installation in 5 Minutes

1. clone this repo: `git clone https://github.com/cloudfoundry-community/oauth-register-broker.git && cd oauth-register-broker`
2. build the app by running `mvn install`
3. Create a database service instance in your cloud foundry instance (e.g for [p-mysql](http://docs.pivotal.io/p-mysql/): `cf cs p-mysql 100mb mysql-db-dumper-service`)
4. Update the manifest (*manifest.yml*) (**Note**: If you don't want to use uaa to login into dashboard, remove *uaa* profile in `spring_profiles_active`
5. Add cloudfoundry user which has access role for looking in orgs and spaces following `cf_admin_user` and `cf_admin_password` var in manifest
(This required to find app passed in binding and retrieve its uri(s), it not set it will ask to user to set a parameter which target the url of his app during binding)
6. Push to your Cloud Foundry (in the manifest.yml folder: `cf push`)
7. Enable service broker by doing:
```
$ cf create-service-broker oauth-register broker-user-from-manifest broker-password-from-manifest https://oauth-register.your.domain
$ cf enable-service-access oauth-register
```


## Create a client for Uaa

If you want to give access to your users to register their app without setting parameters `provider_username` and `provider_password` you will need to create a uaa client
which can create new clients, you can see how to register one here.

When your client will be created you will able to set in *manifest.yml* these variables
- `global_provider_username_uaa`
- `global_provider_password_uaa`

**Note**: To set global config for others provider you just have to add in *manifest.yml* to follow the same pattern (e.g.: `global_provider_username_myprovider: foo` `global_provider_password_myprovider: bar`)

### From the uaa manifest
```
oauth-register:
  override: true
  scope: openid,cloud_controller_service_permissions.read,clients.write,clients.admin,zones.uaa.admin
  authorities: openid,cloud_controller_service_permissions.read,clients.write,clients.admin,zones.uaa.admin,uaa.admin
  secret: mysupersecretcode # change this
  authorized-grant-types: authorization_code,client_credentials,refresh_token,password
  id: oauth-register
```

### From uuac
```
$ uaac client add oauth-register \
    --authorized_grant_types "authorization_code,client_credentials,refresh_token,password" \
    --name "oauth-register" \
    --scope "openid,cloud_controller_service_permissions.read,clients.write,clients.admin,zones.uaa.admin" \
    --authorities "openid,cloud_controller_service_permissions.read,clients.write,clients.admin,zones.uaa.admin,uaa.admin" \
     -s "mysupersecretcode"
```

## How to use

### Create the service

With global username and password set for the chosen provider :

```
cf cs oauth-register uaa myprovider
```

Without:

```
cf cs oauth-register uaa myprovider -c '{"provider_username":"username of provider", "provider_password": "password of the provider"}'
```

### Update the service (useful only when global username and password are **not** set for the chosen provider)

This will permit to update the user and the password of your provider

```
cf update-service myprovider -c '{"provider_username":"username of provider", "provider_password": "password of the provider"}'
```

### Register an app

available parameters:

- `scopes` (*optional*): can be a list, e.g.: *openid,cloud_controller.read*. Defaults: `openid`
- `grant_types` (*optional*): can be a list, e.g.: *authorization_code,password*. Defaults: `authorization_code`
- `callback_url` (*optional*): e.g.: */login*. Defaults: `/`. This control the path where the provider will redirect when login succeeded on your app.
- `app_uris` (*required only when global username and password are **not** set for the chosen provider*): can be a list, e.g.: *http://mysupersite.com,http://myothersupersite*.

Example without parameters:

```
cf bs myapp myprovider
```

Example with parameters:

```
cf bs myapp myprovider -c '{"scopes":"openid,cloud_controller.read", "callback_url": "/login"}'
```

### Unregister an app

```
cf us myapp myprovider
```

## Dashboard

### Login

You can go to the dashboard by going on the route of the broker.
You have multiple way to login:

1. If the admin set the uaa and cloud foundry credentials, users could access through Cloud Foundry login page and registered apps will be filtered by users accessibility on this apps
2. If the admin set the uaa and **didn't set** cloud foundry credentials, users could access through Cloud Foundry login page and registered apps will **not** be filtered by users accessibility on this apps
3. If the admin **didn't set** set the uaa and **didn't set** cloud foundry credentials, users could access by basic auth by using `admin_username` and `admin_password` that you can see in `manifest.yml` and registered apps will **not** be filtered by users accessibility on this apps

### Preview:

![Screenshot preview](https://rawgit.com/ArthurHlt/oauth-register-broker/master/src/main/resources/static/images/preview/preview.png)

## PingFederate

By default, authorization approval is bypassed, because Pingfederate is mostly used in B2E contexts.
This may be configurable in a future release.

As soon as the oauth client is created in PingFederate administration cluster, a replication request is sent, so that you can use it directly.
