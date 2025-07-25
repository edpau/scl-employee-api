# 🗂 Starter Template – Folder & File Guide

📎 [Starter Template Source](https://github.com/nology-tech/getting-started-guides/tree/main/play/starter-template)

## 📁 Core Folder Structure

| Path           | Description                                                                                 |
|----------------|---------------------------------------------------------------------------------------------|
| `app/`         | Core backend logic: controllers, models, services                                           |
| `conf/`        | App configuration: routes, `application.conf`, and database evolutions                     |
| `project/`     | SBT build setup: plugins and internal config (required for Play to function)               |
| `build.sbt`    | Main SBT build file: project name, Scala version, dependencies, and plugin activation      |
| `.gitignore`   | Specifies files and folders to exclude from version control                                 |

---

## 🚀 How `sbt run` Spins Up the Server

These files enable the Play server to compile, route requests, and start listening on `localhost:9000`.

| File                             | Purpose                                                                                      |
|----------------------------------|----------------------------------------------------------------------------------------------|
| `project/plugins.sbt`            | Registers the **Play Framework plugin** (adds server, routing, config, lifecycle support)    |
| `build.sbt`                      | Declares app dependencies, enables `PlayScala`, adds Slick + MySQL                          |
| `conf/routes`                    | Maps HTTP routes to controller actions (like a routing table)                               |
| `conf/application.conf`          | Sets DB connection, port, evolutions, logging, DI options                                   |
| `app/controllers/HomeController.scala` | First HTTP entry point — returns a simple JSON response                                 |

---

## 🧠 Tip

If you ever want to recreate this setup from scratch:

1. Start with a blank SBT project
2. Add `project/plugins.sbt` and register Play
3. Add `build.sbt` with `enablePlugins(PlayScala)` and your dependencies
4. Set up `conf/routes` and `application.conf`
5. Add your controller under `app/` and run with `sbt run`

---