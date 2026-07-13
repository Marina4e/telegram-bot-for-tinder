# Telegram Bot for Tinder

[Українська версія](README.md)

A Telegram bot that combines the Telegram Bot API and ChatGPT to generate Tinder profiles, opener messages, reply suggestions, role-play conversations with celebrities, and general AI answers. The project shows how to build a multi-mode Java chatbot with dialog state management, prompt files, and multimedia resources.

## Project Goal

This project was created as a learning pet project to practice building Telegram bots in Java and integrating them with ChatGPT. The idea is not limited to one simple bot response. Instead, the bot manages several conversation scenarios:

- Tinder profile generation;
- first-message generation for dating;
- next-message or date-invitation generation based on chat history;
- role-play training with a celebrity persona;
- a separate general-purpose ChatGPT mode.

## Technologies

- Java 17
- Maven
- TelegramBots `6.9.7.1`
- ChatGPT Java client `com.github.plexpt:chatgpt:4.3.0`
- OpenAI API
- Telegram Bot API
- Resource-based prompts and messages

## Features

- shows a Telegram command menu;
- generates a Tinder profile from user answers;
- generates an opener message for first contact;
- analyzes chat history and suggests the next message;
- helps write a date invitation;
- lets the user practice conversations with a celebrity persona;
- works as a simple ChatGPT assistant in a question-answer mode;
- sends text, inline buttons, and images for different scenarios.

## `src` Structure

### `src/main/java`

Core application logic:

- `com.javarush.telegram.TinderBoltApp` - the main bot class with all application logic and mode routing.
- `com.javarush.telegram.MultiSessionTelegramBot` - a reusable wrapper over Telegram Long Polling Bot with helpers for messages, buttons, images, and command menus.
- `com.javarush.telegram.ChatGPTService` - a service for ChatGPT requests, prompts, and message history.
- `com.javarush.telegram.DialogMode` - enum that defines dialog modes.
- `com.javarush.telegram.UserInfo` - user data model used for profile and opener generation.

### `src/main/resources/messages`

User-facing instruction texts for each mode:

- `main.txt` - bot capabilities overview;
- `profile.txt` - intro for profile generation;
- `opener.txt` - intro for opener generation;
- `message.txt` - explanation of reply-assistant mode;
- `date.txt` - explanation of celebrity role-play mode;
- `gpt.txt` - prompt inviting the user to ask ChatGPT a question.

### `src/main/resources/prompts`

System prompts for ChatGPT:

- `profile.txt` - prompt for Tinder profile generation;
- `opener.txt` - prompt for first-message generation;
- `message_next.txt` - prompt for the next reply suggestion;
- `message_date.txt` - prompt for a date invitation;
- `gpt.txt` - prompt for short direct AI answers;
- `date_*.txt` - persona prompts for celebrity role-play conversations.

### `src/main/resources/images`

Images sent by the bot in each scenario to make the interaction more visual and engaging.

## How the Bot Works

1. The user starts the bot with `/start`.
2. `TinderBoltApp` shows a command menu, a main image, and a text overview of the bot features.
3. The user enters a specific mode: `/profile`, `/opener`, `/message`, `/date`, or `/gpt`.
4. Depending on the mode, the bot either:
   - collects user data step by step;
   - offers inline buttons;
   - immediately treats the user message as input for ChatGPT.
5. `ChatGPTService` builds a request based on the selected prompt and the current user input or chat history.
6. The ChatGPT response is returned to the Telegram chat.

## Working Modes

### `/profile`

The bot asks step by step for:

- name;
- age;
- city;
- occupation;
- hobby;
- dating goals.

Then it converts the collected data into structured text through `UserInfo.toString()` and sends it to ChatGPT with the Tinder profile prompt.

### `/opener`

This mode works similarly to `/profile`, but uses another prompt. Its goal is to collect a short description of a person and generate a strong first message for dating.

### `/message`

The bot accepts conversation history from the user, stores it in `List<String> chat`, and then uses a button action to:

- generate the next message with `message_next`;
- generate a date invitation with `message_date`.

### `/date`

The bot lets the user pick one of the personas:

- Ariana Grande;
- Margot Robbie;
- Zendaya;
- Ryan Gosling;
- Tom Hardy.

After a button is pressed, a dedicated persona prompt is loaded, and the user continues the conversation with ChatGPT in that role.

### `/gpt`

The simplest mode: the user asks a question, and the bot sends it to ChatGPT with a short system prompt.

## Class Overview

### `TinderBoltApp`

Purpose:
the main application bot class that contains commands, modes, and user interaction logic.

What it does:
- extends `MultiSessionTelegramBot`;
- stores the current `DialogMode`;
- creates `ChatGPTService`;
- processes `/start`, `/profile`, `/opener`, `/message`, `/date`, and `/gpt`;
- controls menus, images, texts, and inline buttons;
- collects user data for profile and opener generation;
- works with conversation history;
- starts the Telegram bot in `main()`.

Why this class exists:
to keep all end-user scenarios coordinated in one place and connect the Telegram interface with the ChatGPT service layer.

What it demonstrates:
- Telegram update handling;
- state-based application logic;
- behavior switching through enum modes;
- callback button processing;
- integration of an external AI service into a chatbot.

### `MultiSessionTelegramBot`

Purpose:
provide a reusable foundation for the Telegram bot with helper methods.

What it does:
- extends `TelegramLongPollingBot`;
- receives an update and delegates it to `onUpdateEventReceived()`;
- stores the current update in `ThreadLocal`;
- provides `getCurrentChatId()`, `getMessageText()`, and `getCallbackQueryButtonKey()`;
- sends text, images, and button-based messages;
- updates already sent messages;
- shows or hides the command menu;
- loads text resources, prompts, and images from `resources`.

Why this class exists:
to avoid duplicating low-level Telegram API logic in the application bot class.

What it demonstrates:
- OOP inheritance;
- Telegram API encapsulation;
- inline button handling;
- classpath resource loading;
- building a reusable infrastructure layer.

### `ChatGPTService`

Purpose:
wrap ChatGPT interaction in a dedicated service class.

What it does:
- initializes the ChatGPT client;
- accepts an access token;
- keeps message history in `messageHistory`;
- supports one-shot requests via `sendMessage(prompt, question)`;
- sets a system prompt through `setPrompt(prompt)`;
- continues a dialog through `addMessage(question)`;
- builds `ChatCompletion` requests and returns the model response text.

Why this class exists:
to separate AI integration from Telegram bot logic.

What it demonstrates:
- external API integration;
- conversational context management;
- prompt engineering;
- encapsulating AI client interaction.

### `DialogMode`

Purpose:
define all available bot modes.

Values:
- `MAIN`
- `PROFILE`
- `OPENER`
- `MESSAGE`
- `DATE`
- `GPT`

What it demonstrates:
- using enums for application state management;
- a cleaner alternative to string constants or numeric codes.

### `UserInfo`

Purpose:
store the information used to generate a profile or opener message.

What it contains:
- name;
- sex;
- age;
- city;
- occupation;
- hobby;
- attractiveness;
- wealth/income;
- annoyances;
- dating goals.

What it does:
- accumulates user data;
- converts filled fields into structured prompt text through `toString()`.

What it demonstrates:
- data modeling;
- preparing structured AI context;
- basic object-to-text serialization.

## What You Learned in This Project

This project shows that you practiced and understood:

- building a Telegram bot in Java;
- using Telegram Long Polling;
- building command menus and inline buttons;
- processing different update types: regular messages and callback queries;
- integrating OpenAI/ChatGPT into an application;
- working with prompts stored in resource files;
- managing dialog state with enums and mode variables;
- collecting user data through a step-by-step flow;
- building AI context from conversation history;
- loading texts and images from `resources`;
- organizing a standalone Java project with Maven dependencies.

## What This Project Demonstrates to a Reviewer

From a portfolio perspective, the project shows:

- you can work not only with classic backend code, but also with event-driven bots;
- you understand third-party API integration;
- you can structure application logic into separate classes;
- you think about conversation UX: menus, buttons, scenario design, images, and guided texts;
- you can move configurable content into resources instead of hardcoding everything inside Java classes.

## How to Run

1. Install JDK 17.
2. Install Maven.
3. Create a Telegram bot through `@BotFather`.
4. Set the following constants in `TinderBoltApp`:
   - `TELEGRAM_BOT_NAME`;
   - `TELEGRAM_BOT_TOKEN`;
   - `OPEN_AI_TOKEN`.
5. Build the project:

```bash
mvn clean package
```

6. Run `TinderBoltApp`.

## Technical Notes

- In the current version, tokens are stored directly in code constants. For a production-style setup, they should be moved to environment variables or a config file.
- `UserInfo` contains more fields than the current 6-question flow actually collects. That shows the model was designed with room for future expansion.
- The repository does not include a dedicated automated test suite, so the main value here is API integration, dialog scenarios, and bot interaction logic.

## Summary

This is a strong learning project centered on a Telegram bot with AI integration. It clearly demonstrates skills in Telegram Bot API usage, multi-mode dialog design, prompt-based ChatGPT integration, and building a Java application around a real user-facing scenario.

