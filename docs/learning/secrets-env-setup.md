# 🔐 Managing the Application Secret with Environment Variables

To avoid hardcoding secrets like passwords into `application.conf`, I explored how to externalize sensitive values using **environment variables** — a clean and secure industry practice.

## 📚 Based On:

Play Docs – [Application Secret](https://www.playframework.com/documentation/2.9.x/ApplicationSecret)

---

## ✅ What I Implemented

### `application.conf`

```hocon
# Application Secret
play.http.secret.key = "changeme"
play.http.secret.key = ${?APPLICATION_SECRET}
```

### 📝 Why I Wrote It Like This

- The first line is a **default fallback** (`changeme`) — used in development if no override is set.
- The second line allows the Play config system to **inject a secure secret from the OS** if `APPLICATION_SECRET` exists.

> ✅ This setup works flexibly in all environments — dev, test, staging, or production — with no file changes needed.

> 🔐 This secret is used internally by Play to **sign cookies, CSRF tokens, flash messages, sessions**, and more.  
> Later, when I explore **CSRF protection**, form validation, or signed cookies — this is what makes them secure.

---

## 🔑 For Database Configuration

```hocon
# DB Credentials
slick.dbs.default.db.user = "root"
slick.dbs.default.db.user = ${?DB_USER}
slick.dbs.default.db.password = ""
slick.dbs.default.db.password = ${?DB_PASSWORD}
```

### 📝 Why I Wrote It Like This

- The first entries (`root`, `""`) are **default values** that work locally for many dev setups.
- The second lines use `${?ENV_VAR}` to **override values securely at runtime**.

> ✅ This lets you keep the app working in development _and_ inject real secrets in production **without hardcoding anything**.

---

## 🖥️ Terminal Command Example

```bash
APPLICATION_SECRET="mysupersecret" DB_USER="root" DB_PASSWORD="secret123" sbt run
```

✅ This runs the app with all credentials safely injected by the OS shell, not committed to code.

---

## 🧠 Why I Chose This First

- I wanted to avoid `.env` files or hardcoded secrets in version control
- This method is **simple**, **secure**, and **clean**
- Great way to understand Play's config fallback system

---

## ⚠️ Limitation I Noticed

Manually passing many environment variables like this:

```bash
APPLICATION_SECRET=... DB_PASSWORD=... OTHER_KEY=... sbt run
```

…is **not scalable** for larger projects.

---

## 🧭 Next Steps (Later)

I’ll explore:
- `.env` file loading via `sbt-dotenv`
- ✅ Play’s **production configuration override** pattern:

  ```bash
  sbt -Dconfig.file=conf/production.conf run
  ```

For now, this method helps me build a solid foundation in **secure config handling**.