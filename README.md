# etsen
Manual for Source Code:

For java files
\Camera Measure Mobile Application Source Code\app\src\main\java\com\example\memo\camerameasure
CameraFocusActivity.java (main, measure interior)
CameraView (display camera on phone)
CustomOnItemSelectedListener (functions to listen)
ImageAccess (access image from phone)
main_menu (main menu of the application)
Offline (measure object)

For layout
\Camera Measure Mobile Application Source Code\app\src\main\res


For Android Manifest
\Camera Measure Mobile Application Source Code\app\src\main
AndroidManifest.xml (to change settings and versions or libraries, some library might be deprecated or 
have different syntax to be implemented for other phones to be compatible)

Tools:
-Hardware:
--Smartphone with Android version 7.1.1 (Nougat)
--USB (to connect phone with android studio)


-Software and utilities:
--Android Studio software version 3.5
--Sdk version 25
--install JDK or JRE

-Build> Edit Build types > Project Structure > Project:
--Gradle version 5.4.1
--Android Gradle 3.5.3
-Build> Edit Build types > Project Structure > SDK Location:
-- Android SDK Location
-- JDK Location
-Build> Edit Build types > Project Structure > Modules:
--Compile SDK Version

Steps:
1. Install and download all the required tools above
2. Connect your smartphone to the Android Studio
3. Run the code and the app will be installed into your phone
Further errors might be caused by incompatible devices or deprecated syntax.
Modification can be done in AndroidManifest.xml


