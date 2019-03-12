# Rooted devices

### Disclaimer
The purpose of this example to demonstrate the risk of your app running on a rooted device and how straightforward it can be for a hacker to access your private information.

Defend your app against potential hacks:
* [Detect rooted device](https://stackoverflow.com/questions/1101380/determine-if-running-on-a-rooted-device) and act accordingly (you might decide to block the app from running).
Note, it is not that simple, a hacker might use hooking Xposed to [bypass your root check](https://repo.xposed.info/module/com.devadvance.rootcloak2).
The problem the becomes dealing with Xposed which is much harder. 
* Use [keystore](https://developer.android.com/training/articles/keystore) or [NDK](https://medium.com/skyrise/android-applications-security-part-1-2782d73771e0) to store sensitive information.

This simple example exploits rooted device to get access to another app private data.

# App1
One pager application with an option to set and display a "user name".
The value of the set user name is then saved to `SharedPreferences`.

# App2
A malicious application stealing the "user name" from App1.

### How does it work?

From [Android central](https://www.androidcentral.com/root):
> When you root your Android, you're simply adding a standard Linux function that was removed. A small file called su is placed in the system and given permissions so that another user can run it. It stands for Switch User, and if you run the file without any other parameters it **switches your credentials and permissions from a normal user to that of the superuser**.

App2 will utilize `su` to get access to App1 `SharedPreferences`.

To do this, App2 would grant the corresponding `SharedPreferences` file of App1 read and write access using the command:

```
su -c chmod -R 0777 "/data/user/0/com.dimanem.security.root.example.app1/shared_prefs/com.dimanem.security.root.example.app1_preferences.xml"
```
 

And then read the file using:
```
su -c cat "/data/user/0/com.dimanem.security.root.example.app1/shared_prefs/com.dimanem.security.root.example.app1_preferences.xml"
```

## Notes
1. On a non rooted device we would get the following exception running `su` : 
```
java.io.IOException: Cannot run program "su": error=13, Permission denied
```

2. App2 is using `File` APIs and not `SharedPreferences` APIs to read App1 content. I was not able to find a way to work directly with `SharedPreferences`. Thus, we would have to parse the file ourselves. To avoid dealing with XML parsers, we simply fetch the first value from the file.

