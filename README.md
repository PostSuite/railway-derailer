# Railway Chaos Derailer

![Github Repository Header](https://github.com/user-attachments/assets/953eba71-b13f-47bc-bfd9-c6a64db143e7)

---

Derailer is a tool that helps test for how services handle random dependency failures,
on [Railway](https://railway.com). Inspired by [Netflix's Chaos Monkey](https://github.com/Netflix/chaosmonkey).
Derailer randomly removes instances of running services, or remove the service entirely. Allowing you to build more
resilient services by finding missed opportunities to handle failures gracefully.

> [!CAUTION]
> You should avoid deploying this onto your customer facing environment, as it may have visible impact, we suggest
> sticking to staging!

**If you've read the above disclaimer and understand it, click to deploy:**

[![Deploy on Railway](https://railway.com/button.svg)](https://railway.com/template/WRXzro?referralCode=A6ij-A)

---

## ▶️ Derailment Process

Every so often (every `FREQUENCY_MINUTES`), the Derailer lists your Railway services and filters them to ones that
aren't in the configured blacklist.

Using this list, it chooses to 'Derail' a number of your services (using `BLAST_RADIUS`), this is done by **Aborting
Deployments**, it will remove the currently active deployment of your service.

After the defined `DURATION_MINUTES`, it will re-deploy any effected services.

**The derailed services are stored in a persistent volume (on sqlite), meaning re-deploying this service will restore
any effected services when it comes online!**

---

## 👀 User Interface

> [!WARNING]
> This user interface is not password protected, we suggest
> using [a cloudflare tunnel](https://railway.com/template/cf-tunnel), or implementing your own authentication in a
> forked
> version.

Derailer comes with a (very primitive) user interface for controlling how the derailer behaves.

**Features:**

- Viewing an active derailment and what services it's impacting
- Rollback an active derailment if it's causing a headache
- Viewing when the last derailment happened
- Viewing when the next derailment will occur
- Viewing a history of which services were impacted for each past derailment
- Delaying derailments for a certain time period (to allow for some rest!)

---

## 🤖 Rest API

> [!WARNING]
> This API is not password protected, we suggest
> using [a cloudflare tunnel](https://railway.com/template/cf-tunnel), or implementing your own authentication in a
> forked
> version.

For ease of use, all the features mentioned in our user interface are supported within a REST API, check out the [API
documentation](docs/openapi/openapi.json).

---

## 🧩 Configuration Options

### Environment Variables

| Variable 	        | Description 	                                                                           | Default Value 	 |
|-------------------|-----------------------------------------------------------------------------------------|-----------------|
| RAILWAY_API_KEY   | Your Railway API key, required to list your services and fetch your running deployments |                 |
| FREQUENCY_MINUTES | How often (in minutes) to run the chaos, cannot be less than `DURATION_MINUTES`         | 10              |
| DURATION_MINUTES  | How long (in minutes) to keep services removed / deployments aborted	                   | 60              |
| BLAST_RADIUS      | How many services to impact in each run of the Derailer                                 | 2               |

The default configuration also uses `RAILWAY_SERVICE_ID` (automatically added by Railway) to blacklist the Derailer
application from being Derailed.

### Blacklisting services

By default, the only service automatically blacklisted is the Derailer application. However, you may want to blacklist
some UIs, or essential services (think carefully about how reliable they are!) to avoid everything falling on its face.

This can be configured in `src/main/resources/application.properties`

---

## 💻 Running Locally

Running locally allows you to quickly test your changes, using the Quarkus framework we can

Copy `.env-example` into a file called `.env` and fill in all the environment vars
Run `./gradlew quarkusDev` from the gradle quarkus menu to run in dev mode

---

## 🚀 Testing

To run tests, execute:

```shell
./gradlew test
```
