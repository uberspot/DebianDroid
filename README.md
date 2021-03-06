# DebianDroid

This Debian Android app started as part of Google Summer of Code 2013. It's an Android Application for maintainers, uploaders or developers of the Debian community. It communicates with the Debian SOAP and REST api and retrieves various information that can be useful when you're not in front of your workstation at home or at office but still want to be aware of changes in things you maintain or in things that interest you in Debian. It eases a lot of operations like checking the next DInstall time, submitting a new bug report or responding to an existing one, getting info on packages and on pending bugs for those packages etc. 

## Usage Scenarios

 1. Bob takes the train every day, and checks his mail on the ride in, on his Android phone. A lot of Bob's mail is Debian related, containing links to the BTS, PTS, and other related Debian websites. Bob wishes to get the information presented on the site in a way that looks native (and clear to read), as well as cached, since his cell network connection is expensive. Bob may also go through long tunnels without service, where it'd be nice to have an offline copy of things he's been to before.
 2. Anne is a package maintainer of a few Python modules. These modules are not very time intensive, but she wants to keep up with the team. Anne wants to be able to use her phone to get alerts that notify her about the state of the Python modules team, such as new uploads of the Python interpreter, so her packages can be tested for FTBFS bugs. Anne also wishes to use her phone to preform actions on the BTS, such as comment on a bug she knows something about, or mark a bug moreinfo. Anne understands the BTS is based on email.
 3. A sysadmin is in a datacenter and has only their phone. He needs to quickly look up info on bug reports in the kernel because a server won't boot.
 4. The app will also act as a tool to let people quickly find out their overlapping debian interests at debcamp/debconf or other meetups. If two people meet, they can quickly look up whether the packages they maintain are related at all. For example I maintain fooapp and I meet someone, we break Debiandroid and I quickly scan the QRCode generated by the other app. It then shows me that fooapp depends on libbar, and the person I met maintains the libbar package.
 5. A "bug report alarm" button of some sort that will be used when you don't have time to write a full bug report via your smartphone but you are busy and don't want to forget it later on. A quick mobile way to file a pre-bug report so that you have less to do when you get infront of your main workstation.

## Features

 1. Retrieves package/bug info based on package name, bug number, maintainer name and more
 2. Caches retrieved info for specified time and if there is no internet connectivity it returns the cached content automatically
 3. Swipe left/right to move around menu items
 4. Opens links from other apps (e.g. browser or mail app) to http://bugs.debian.org and http://packages.qa.debian.org natively in the debiandroid app in the corresponding app 
 5. Finds overlapping debian interests between maintainers (use case 4)
 6. Widget in homescreen displays next DInstall time in UTC and localtime
 7. Allows you to send a new bug report for a package (by redirecting you to your mail app with some preconfigured fields like from, to, subject etc) or to respond to a mail in an existing bug report by long pressing on that mail and then redirecting you again to your mail app.
 9. Swiping from a package you just searched in the pts menu item to the bts menu item will instantly load the searched packages bugs
 10. Notify user if internet connection stops working and the use of 3g is deactivated.
 11. If "use 3g" settings is deactivated detect if 3g is used and deactivate online searching by setting cache to always return something or null if there isn't any related info cached.
 12. Shows new/removals/deferred queues from https://ftp-master.debian.org/new.822 , https://ftp-master.debian.org/removals.822 and https://ftp-master.debian.org/deferred/status respectively
 13. Allows searching for packages with similar names based on http://sources.debian.net/api/search/vim/ (doc: http://sources.debian.net/doc/api/). 
 14. Languages translated to: Greek, French
 15. Shows info about packages from http://qa.debian.org/madison.php?package=vim&table=debian&a=&c=&s=#
 16. Add option to autocollapse everything and leave only one group expanded in all expandable lists

## TODO
 
 1. Add buildd log info (? https://buildd.debian.org/ ?)
 2. Tell which package a certain file is in: http://packages.debian.org/search?searchon=contents&keywords=&
 3. Show links to mailing list archives: https://lists.debian.org/debian-%s/recent
 4. Alerts you about a bug report you wanted to send, like a "bug report" reminder. Maybe with a button like "report bug in x time"
 5. Add extra parameters to bug search fragment with an +(add) button for more complicated searches
 6. Add clear favourites button in favourites fragment or in options
 7. Notifies user about new mails in bug reports he made, he contributed to or in bugs he is subscribed to and about new package news to packages he maintains or is subscribed to etc
 8. Show partial results in groups of ~20 bug reports to save time in search.
 9. Show Debian Developer Package Overview: http://qa.debian.org/developer.php?login= in a native ui instead of redirecting to browser
 10. Show contents of a package like in http://packages.debian.org/sid/i386//filelist
 11. Get somehow list subscriptions for a mail and compare them to find common ones between two developers (add to cif) so that they don't have to cc unnecessarily in mails to lists.
 12. Add a alarm for wnpp (BTS; packages that are to be removed; watching for ITP bugs, etc.)
 13. Translate to other languages. https://www.transifex.com/projects/p/debiandroid/ 
 14. Add http://udd.debian.org/dmd.cgi

## Permissions explained

 1. INTERNET - To access the debian soap api
 2. WRITE_EXTERNAL_STORAGE - To cache retrieved info to external storage
 3. WAKE_LOCK - Needed for the AlarmManager to update the widget
 4. ACCESS_NETWORK_STATE - To access the state of the wifi and (en)disable the use of internet if 3g usage is not enabled in settings

## Documentation

### Quick code description

![DDiagram](https://raw.github.com/uberspot/DebianDroid/master/DDDiagram.png "DebianDroid Flow Diagram")

The app starts by loading ItemListActivity. That activity detects if the app is running on a tablet or on a smartphone and loads the corresponding fragments. If on a tablet it will load ItemListFragment to display the content menu on the left and PTSFragment which extends ItemDetailFragment to show the pts related info on the right. ItemDetailFragment is a general fragment that all other content displaying fragments should extend. It has several methods useful for multiple fragments (like forwarding to a mail app or hiding the software keyboard) and also implements the gesture detection/swiping. 

All MenuItems that should exist everywhere in the app are added in ItemDetailActivity and ItemListActivity. Every other MenuItem that should be added in a specific menu page (like in pts) should be added in the corresponding fragment. 

To add a new content fragment one should a) add the content entry to Content.java and to ItemDetailFragment.java and b) create a Fragment.java that extends ItemDetailFragment and add a corresponding xml in the res/layout/ for the fragments layout. 

The DDService runs all the time and will run several functions. It checks if connectivity is established and whether it is with wifi or mobile and adjusts the api behaviour accordingly.

All code related to interacting with the Debian API (soap, rest or anything else) and to info related to the api is in the package apiLayer. 

### Libraries used

 1. Joda Time for correct time handling/conversion in the widget code
 2. KSOAP2 for interacting with the Debian pts and bts soap api
 3. ZXing for QRCode reading/writing
 4. ActionBarSherlock for ActionBar compatibility with older versions of Android
 5. AndroidStorageUtils for easier interaction with the devices' storage
 6. ckChangeLog for displaying a changelog in every new release

### ActionBarSherlock installation

 1. Add the actionbarsherlock/ subdirectory downloaded from the official site to the directory of DebianDroid
 2. Import the project as a new android application to eclipse
 3. Add the newly created project as a library to DebianDroid via right click to Debiandroid->properties->Android tab->Add... in Library section
 4. Delete the libs/android-support-v4.jar since it's now in actionbarsherlock/libs/ and change the DebianDroid properties->Java Build Path->Android Dependencies to point to that .jar instead of /libs because otherwise there will be a conflict between the two. Add it as a seperate .jar if no other way works.
 5. Make sure you ticked the correct files to export in the "Order and export" tab in the Java Build Path
 6. Change all occurences of Activity, FragmentActivity, Fragment, ListFragment etc to SherlockActivity, SherlockFragment etc...
 7. Change all styles.xml to use as a parent theme the "@style/Theme.Sherlock.Light.DarkActionBar"
 8. Clean, build, deploy

### ZXing installation

 1. Download core.jar from the official zxing site
 2. Add it to /libs and make sure it's exported along with the other libs in your projects Preferences->Java Build Path->Order and Export
 3. Download the zxing.zip source code
 4. Add the source from the android-integration/ subfolder to /src/com/google/zxing/integration/android/ 
 5. Add code to create/read qrcodes and clean, build and deploy project

### Installation of other libs

 1. Joda time and KSOAP2 are installed by copying their .jar file to /libs/ in the project
 2. AndroidStorageUtils is just added as a package directly to the source code
 3. Check README.md in ckChangeLog directory for installation instructions

## License

    DebianDroid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
