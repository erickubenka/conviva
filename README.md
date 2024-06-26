# conviva

Conviva is a simple Whatsapp Bot based on UI automation with Selenium and Testerra to answer commands in a specific
group chat.

--- 

## How it works

Conviva starts a headless browser session on the host system (for example chromium) and then uses Testerra and Selenium
Automation Tools to automate the UI of web.whatsapp.com as WhatsApp does not provide an open API for non-business
customers.

After startup, you either have to scan the QR code or enter the displayed verification code to connect the web instance
to your local phone.

Finally, Conviva opens up the specified group chat, reads all messages of the last hours (defined in properties) and
then
will listen for incoming new messages. If a message is found that matches a registered bot command, Conviva will run the
specified task and answer into the group chat.

**Recommendation:** Use a dedicated phone number for the bot, as otherwise your own user session will be blocked by the
bot instance and you won't receive notifications fo the group chat anymore.

## Features

- Start as listener into a specific group chat
- Answer commands in the group chat
- Execute threaded tasks to contact third party APIs during the command process

## Default Commands

Conviva provides an interface to add new commands easily to the Bot on Startup or even after startup.  
Just use the `WhatsappUiBot.registerCommand()` method to register a new command.  
Several commands are implemented by default:

- `!help` - Show the list of available commands
- `!bug` - Report a bug
- `!status` - Show the status of the bot
- `!stopbot` - Stop the bot
- `!tldr` - take message history of last 12 hours and let OpenAI summarize it.
- `!sup` - take message history of last 12 hours and let OpenAI summarize it even more.

## Properties

> To be specified.

## Build

Conviva can run as Docker container, to do so you have to build it from the sources or otherwise use the provided docker
images on release page.

```
docker build . -t conviva-bot
docker run -it --rm --name conviva-bot conviva-bot

```

## Usage

Create a file `conviva.properties` on your host machine including all the details and configurations for the bot.  
Then run the docker container and mount the properties file into the container.  
You can also link the host timezone configuration to have a matching timezone inside the container.  
You can link the userdata folder as well to have a persistent chrome browser profile across multiple container
restarts without use of login again and again.

### With image from GitHub Registry

````bash
# pull remote container
docker pull ghcr.io/erickubenka/conviva:main
docker run -d -it -v /root/.conviva/userdata:/root/.conviva/userdata -v /root/.conviva/conviva.properties:/app/conviva.properties -v /etc/localtime:/etc/localtime:ro -v /etc/timezone:/etc/timezone:ro --name conviva-bot ghcr.io/erickubenka/conviva:main
````

### With locally built image

````bash
docker run -d -it -v /root/.conviva/userdata:/root/.conviva/userdata -v /root/.conviva/conviva.properties:/app/conviva.properties -v /etc/localtime:/etc/localtime:ro -v /etc/timezone:/etc/timezone:ro --name conviva-bot conviva-bot
````

Currently, the bot somehow tries to allocate a lot of memory during its execution. To avoid a stuck bot, you can go for
the restart and memory limit options.  
This will restart the bot if more than 3GB of memory is used by the container.

```
docker run -d -it -v /absolute/path/conviva.properties:/app/conviva.properties -v /etc/localtime:/etc/localtime:ro -v /etc/timezone:/etc/timezone:ro -m 3125m --restart always --name conviva-bot ghcr.io/erickubenka/conviva:main
```

