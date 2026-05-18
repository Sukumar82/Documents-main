# How to Run the Banking App Using Docker


##  Step 1 — Download and Install Docker Desktop

1. Go to this website: **https://www.docker.com/products/docker-desktop**
2. Click the big **Download** button for your operating system (Windows / Mac / Linux)
3. Open the downloaded file and follow the installer steps (just keep clicking **Next** / **OK**)
4. When it asks to restart your computer, say **Yes**
5. After restart, open **Docker Desktop** from your Start Menu or Desktop shortcut
6. Wait for the Docker whale icon to appear in your taskbar and show **"Engine running"**

>  Docker Desktop is ready when you see the green dot and the message **"Docker Desktop is running"**.

---

## Step 2 — Check Docker is Working

Open a terminal (see Step 4 below for how to open one) and type this command:

```bash
docker --version
```

You should see something like:

```
Docker version 24.0.5, build ced0996
```

Also check Docker Compose works:

```bash
docker compose version
```

You should see something like:

```
Docker Compose version v2.20.2
```

> If you see version numbers, Docker is ready. If you see an error, go back to Step 1 and make sure Docker Desktop is open and running.

---

## 📁 Step 3 — Download / Get the Project Files

You need the project folder on your computer. The main folder is called **`Documents-main`**.

Inside it you will see these files and folders:

```
Documents-main/
│
├── docker-compose.yml          ← The main file that controls Docker
│
├── team3di-springbootbackendtest_v2/    ← Backend (Java) code
│
├── team3di-reactfrontendtest_v2/        ← Frontend (React) code
│
└── docs/                                ← Documentation (you are reading this!)
```

> ⚠️ **Important:** Make sure the file `docker-compose.yml` exists inside the `Documents-main` folder. Without it, nothing will work.

---

## 💻 Step 4 — Open a Terminal (Command Window)

A terminal is a window where you type commands. Here is how to open one:

### On Windows:
- Press the **Windows key** on your keyboard
- Type **"PowerShell"** or **"Command Prompt"**
- Click on it to open

### On Mac:
- Press **Cmd + Space** to open Spotlight
- Type **"Terminal"** and press Enter

### On Linux:
- Press **Ctrl + Alt + T**

> 💡 You will type all the commands from this guide into this terminal window.

---

##  Step 5 — Go to the Project Folder

In your terminal, type the `cd` command to **navigate into the project folder**.

`cd` means "Change Directory" — it moves you to a different folder.

```bash
cd Documents-main
```

If your project is in a different location, for example on your Desktop:

```bash
# Windows example
cd C:\Users\YourName\Desktop\Documents-main

# Mac / Linux example
cd /Users/YourName/Desktop/Documents-main
```

To check you are in the right folder, type:

```bash
# Windows
dir

# Mac / Linux
ls
```

You should see `docker-compose.yml` listed in the output. ✅

---

##  Step 6 — Start the Application

This is the **magic command**. It will:
- Download all the required software (only on first run)
- Build the backend Java app
- Build the frontend React app
- Start the MySQL database
- Connect everything together

Type this command and press Enter:

```bash
docker compose up --build
```

>  **The first time you run this, it takes about 3 to 5 minutes.** This is normal!
> Docker is downloading things from the internet and compiling the code.
> Future runs will be much faster (under 30 seconds) because Docker remembers what it downloaded.

---

##  Step 7 — Wait for Everything to Start

While Docker is starting, you will see lots of text scrolling on the screen. **Do not close the terminal!**

You are waiting for lines like these to appear:

```
[+] Running 3/3
 ✔ Container banking_mysql     Healthy
 ✔ Container banking_backend   Started
 ✔ Container banking_frontend  Started
```

When you see all three containers show ✔, the application is ready.

You can also check by opening a **second terminal window** and running:

```bash
docker ps
```

You should see three containers listed like this:

```
CONTAINER ID   IMAGE                    STATUS                  PORTS                    NAMES
abc123         documents-main-frontend  Up 2 minutes            0.0.0.0:3000->80/tcp     banking_frontend
def456         documents-main-backend   Up 2 minutes            0.0.0.0:8080->8080/tcp   banking_backend
ghi789         mysql:8.0                Up 3 minutes (healthy)  3306/tcp                 banking_mysql
```

>  All 3 containers should show **"Up"** in the STATUS column.

---

##  Step 8 — Open the App in Your Browser

1. Open any web browser (Chrome, Firefox, Edge — whichever you like)
2. Click in the address bar at the top
3. Type this address and press Enter:

```
http://localhost:3000
```

You should see the **Online Banking Application** website load on your screen!

>  `localhost` means "my own computer". Port `3000` is the door number where the frontend is listening.

---

##  Step 9 — Try the App with Sample Data

The database comes pre-loaded with a test bank account so you can try the app right away.

Fill in the form on the website with these details:

| Field | What to Type |
|-------|-------------|
| **Sort Code** | `53-68-92` |
| **Account Number** | `73084635` |
| **Start Date** | Leave empty (or type `2019-04-01` to filter) |
| **End Date** | Leave empty (or type `2019-06-30` to filter) |

Click the **Submit** button.

You should see:

-  **Current Balance: £1,071.78**
-  A list of **4 transactions** from the year 2019

### To test the date filter:

Fill in Start Date and End Date to only see transactions between those dates:

| Field | Value |
|-------|-------|
| Start Date | `2019-05-01` |
| End Date | `2019-06-30` |

Click Submit. You will see only the transactions that happened between May and June 2019.

---

##  Step 10 — Connect to the MySQL Database

The MySQL database runs inside Docker. Here is how to look inside it.

### Option A — Using a Database GUI Tool (Easiest for Beginners)

The easiest way is to use a free tool called **MySQL Workbench** or **DBeaver**.

Download one of these:
- **MySQL Workbench**: https://dev.mysql.com/downloads/workbench/
- **DBeaver (free)**: https://dbeaver.io/download/

Then connect using these settings:

| Setting | Value |
|---------|-------|
| **Host / Hostname** | `127.0.0.1` |
| **Port** | `3306` |
| **Username** | `root` |
| **Password** | `niceday` |
| **Database** | `online_bank` |

>  **Before connecting with a GUI tool**, you need to expose the MySQL port.
> Open `docker-compose.yml`, find the `mysql:` section, and add these two lines:
>
> ```yaml
> ports:
>   - "3306:3306"
> ```
>
> Then restart with `docker compose down` then `docker compose up --build`.

---

### Option B — Connect Using the Terminal (No Extra Tools Needed)

While Docker is running, open a **new terminal window** and type this command to go inside the MySQL container:

```bash
docker exec -it banking_mysql mysql -u root -pniceday
```

Press Enter. You will now be inside MySQL! You will see:

```
Welcome to the MySQL monitor.
mysql>
```

Now type these commands one by one to look at the data:

```sql
-- Show all databases
SHOW DATABASES;

-- Use our banking database
USE online_bank;

-- Show all tables
SHOW TABLES;

-- See all accounts
SELECT * FROM account;

-- See all transactions
SELECT * FROM transaction;

-- Exit MySQL when done
EXIT;
```

> 💡 Every SQL command ends with a semicolon `;` — do not forget it!

---

### Database Details Summary

| Detail | Value |
|--------|-------|
| Database Name | `online_bank` |
| Username | `root` |
| Password | `niceday` |
| Host (from inside Docker) | `mysql` |
| Host (from your computer) | `127.0.0.1` |
| Port | `3306` |
| Tables | `account`, `transaction` |

---

##  How to Stop the App

When you are done and want to turn everything off:

Go to the terminal where Docker is running and press:

```
Ctrl + C
```

This stops the containers. The data in the database is **saved** — it will still be there next time you start.

Or from any terminal window, type:

```bash
docker compose down
```

### To stop AND delete all saved data (fresh start):

```bash
docker compose down -v
```

>  The `-v` flag deletes the database data too. Use this only if you want a completely clean start.

---

## 🔧 Something Went Wrong? Fix it Here

###  Problem: "Cannot connect to the Docker daemon"

**What it means:** Docker Desktop is not open or not running.

**Fix:**
1. Open **Docker Desktop** from your Start Menu
2. Wait for the green dot and "Engine running" message
3. Try the command again

---

### ❌ Problem: Port 3000 is already in use

```
Error: address already in use :::3000
```

**What it means:** Something else on your computer is already using port 3000.

**Fix (Windows):**
```bash
netstat -ano | findstr :3000
taskkill /PID <the number you see> /F
```

**Fix (Mac / Linux):**
```bash
lsof -ti:3000 | xargs kill
```

---

### ❌ Problem: Port 8080 is already in use

**Fix (Windows):**
```bash
netstat -ano | findstr :8080
taskkill /PID <the number you see> /F
```

---

### ❌ Problem: Banking app shows a blank page or error

**What to check:**

1. Open a terminal and run:
   ```bash
   docker ps
   ```
2. Check all 3 containers are **"Up"** — if any show **"Exited"**, read the next fix.

---

### ❌ Problem: One container shows "Exited"

**Fix:** Check the logs to see what went wrong:

```bash
# Check backend logs
docker compose logs backend

# Check frontend logs
docker compose logs frontend

# Check database logs
docker compose logs mysql
```

Then try a full restart:

```bash
docker compose down
docker compose up --build
```

---

###  Problem: MySQL health check keeps failing / backend won't start

**What it means:** MySQL took too long to start, and the backend gave up waiting.

**Fix:** Just stop and start again. MySQL will be ready this time:

```bash
docker compose down
docker compose up --build
```

---

###  Problem: "image not found" or build fails

**Fix:** Make sure you are in the correct folder (the one containing `docker-compose.yml`) and run:

```bash
docker compose down -v
docker compose up --build
```

---

##  Quick Commands Cheat Sheet

Save this section — you will use these commands all the time!

| What You Want to Do | Command to Type |
|---------------------|----------------|
| Start the app (first time or after code changes) | `docker compose up --build` |
| Start the app (already built, just restart) | `docker compose up` |
| Stop the app (keep data) | `docker compose down` |
| Stop the app AND delete all data | `docker compose down -v` |
| Check what is running | `docker ps` |
| See backend logs | `docker compose logs backend` |
| See frontend logs | `docker compose logs frontend` |
| See database logs | `docker compose logs mysql` |
| Go inside MySQL to run SQL | `docker exec -it banking_mysql mysql -u root -pniceday` |
| Rebuild only the backend | `docker compose up --build backend` |
| Rebuild only the frontend | `docker compose up --build frontend` |
| Full clean restart (delete everything) | `docker compose down -v` then `docker compose up --build` |

---

##  Where to Open Things

| What | Address to Type in Browser | Description |
|------|---------------------------|-------------|
| 🖥️ The Banking Website | `http://localhost:3000` | The main app you use |
| ☕ Backend API (raw) | `http://localhost:8080/api` | The Java server (for developers) |
| 🗄️ MySQL Database | `127.0.0.1:3306` | Connect using MySQL Workbench or DBeaver |

---

##  You Did It!

Here is a quick recap of everything you did:

```
1. Installed Docker Desktop ✅
2. Opened a terminal ✅
3. Navigated to the project folder ✅
4. Ran: docker compose up --build ✅
5. Waited for all 3 containers to start ✅
6. Opened http://localhost:3000 in the browser ✅
7. Typed sort code 53-68-92 and account 73084635 ✅
8. Saw the balance £1,071.78 and transactions ✅
```

Well done! 🎉 The application is running.

---

*📄 Run Configuration Guide · 3Di Assessment · Online Banking Application · 2026*
