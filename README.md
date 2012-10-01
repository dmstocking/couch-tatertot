CouchTatertot
=============

CouchTatertot is an open source android app for managing your CouchPotatoV2 server. It is also available on Google Play.

[![Google Play Icon](http://www.android.com/images/brand/android_app_on_play_logo_large.png)](http://play.google.com/store/apps/details?id=org.couchtatertot)

Features:
* Android Ice Cream Sandwich (4.0) look and feel
* Very Fast
* View, Edit, Refresh, and Delete movies
* View, Ignore, and Download releases
* Search and Add movies
* HTTPS Support
* IMDB share support
* imdb.com share support
* View in IMDB Android App

Example Screenshot:

![Example Screenshot](https://github.com/Buttink/couch-tatertot/wiki/Screenshots/couchtatertot-wanted.png)

## Contributing

First, fork the CouchTatertot repository. Downloaded the android support library version 10. Set the environmental
variable ANDROID_HOME to the android sdk location. Then in IntelliJ, go to open a current project. Find the file
pom.xml. It will be located in the top most folder of your repo. It will be with ChangeLog and README.md. After opening
the project, open the Module settings. There should be two ~apklib projects. Add a maven dependency of
com.google.android:support-v4:10 to both. Compile and Run to test your setup. After making your changes, request a pull.

## Contributors

David Stocking - Creator
