# JAppDater 

Version 1.0.0

-----------------------------------

*A Java Application Updating component. Simple to use, lightweight, no dependencies.*

*Check if your Application needs to update, download your files as ZIP package(s) or separate files. 
Each download can have its own version or checksum, to verify if a download is necessary.*

-----------------------------------

## Screenshots

![Checker Window](https://raw.github.com/HerbertV/JAppDater/master/docs/screen_checker.png)
![Updater Window](https://raw.github.com/HerbertV/JAppDater/master/docs/screen_updater.png)

## Download Binary @ Bintray 

https://bintray.com/herbertv/generic/JAppDater

## Usage

### Setting up your Application

You can compile JAppDater with ant/buildJAppDater.xml.

Include JAppDater.jar in your project.

Create a local and remote index.xml (see schema below)

![Schema Graphic](https://raw.github.com/HerbertV/JAppDater/master/docs/schema.png)

* [Schema](https://raw.github.com/HerbertV/JAppDater/master/data/jappdater/jappdater.xsd)
* [Sample local index file](https://raw.github.com/HerbertV/JAppDater/master/data/jappdater/localsample.xml)
* [Sample remote index file](https://raw.github.com/HerbertV/JAppDater/master/data/jappdater/remotesample.xml)

Put your remote index.xml and your update files on the server. 


### Use custom properties

Put all the entries from the [default properties file]()
into your custom properties file.

## Start from your Application

### Checker

First check if there is a new update. So include one of these lines.

	// check with default properties
	// listener can be null
	Checker.checkForUpdates(UpdateEventListener l);

	// check with your properties
	// listener can be null
	Checker.checkForUpdates(Properties p, UpdateEventListener l);


### Updater

For starting the Update use this snippet:
 
 	// starts the updater in its own runtime.
	Runtime.getRuntime().exec("java -jar jAppDater.jar [optional: properties filename]");
 	// exit your application since you want update your jars as well.
 	System.exit(0);


### Optional - Restarting your Application

Add to your custom properties:

	app.startup=java -jar yourmainjar.jar

If the update was successful the argument -jadfinished is send to your static main


## Start from command line

	// check for updates
	java -cp jAppDater.jar jhv.jappdater.Checker
	// launch update
	java -jar jAppDater.jar [optional your properties]


## License:

[JAppDater is Licensed under MIT License](http://opensource.org/licenses/MIT)