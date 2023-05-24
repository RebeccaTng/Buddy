# Buddy
## Table of Contents
- [Context](#context)
- [Project Description](#project-description)
- [Application Description](#application-description)
  * [Activity Connections](#activity-connections)
  * [Interface](#interface)
  * [Explanation of the activities](#explanation-of-the-activities)
    + [Start - Login - Register](#start---login---register)
    + [RequestReset](#requestreset)
    + [Home](#home)
    + [BlufiMain - BlufiConnect - BlufiConfigure](#blufimain---bluficonnect---bluficonfigure)
    + [PlantStatistics - PlantSettings](#plantstatistics---plantsettings)
    + [Overview](#overview)
    + [AddPlant - AddLibrary - AddManual](#addplant---addlibrary---addmanual)
    + [Account - EditAccount](#account---editaccount)
    + [Info](#info)
  * [Notifications](#notifications)
- [Insight to the finished project](#insight-to-the-finished-project)
___
## Context
This app was written for the Engineering Experience 5 course at Group T KU Leuven. This application is only a small part of the full project, which also included hardware.

## Project Description
From ordinary people to plant fanatics, everyone has already killed a plant once in their lifetime. Though they bring a pop of color in any room, plants are often very difficult to take care of. Giving them too much water or too little, both can be fatal. Furthermore, there is a whole array of indoor and outdoor plant species, each with their own care plan. Taking care of a plant just requires a lot of time, dedication, and knowledge. To alleviate the pressure of taking care of your plant and to keep it alive and well, we designed a plant buddy named Buddy™. This handy plant pot regulates and takes care of any plant.

Our plant buddy is mainly software focused. Data is collected through sensors throughout the plant. Our application can distinguish different plant species and calculate the optimal environmental factors for that specific species. With temperature, pollution, light and moisture measurements of the plant, the app can send warnings to the user to change the placement of the plant to prevent it from dying. The data is all stored in our database over time so that you can be informed of which spots in your house are optimal for your specific plant and which you should stay away from. When the soil lacks moisture, a pump connecting to a water reservoir will water it. A water level sensor in the water reservoir also sends a reminder to the user to refill the reservoir when the water is almost used up. The amount of water administered to the plant is based on the moisture levels.

Our plant has its own display. It can show you how it feels, such as making a happy face when it is feeling good. Also, the sensor measurements are displayed here.
***
## Application Description
### Activity Connections
On the next diagram you can see an overview of the activity connections of the app.
![AppLayout](https://user-images.githubusercontent.com/90101184/171037995-c737facf-127c-4c40-af43-1753ed3c0d9a.png)

### Interface
The app is designed in Figma, this is a tool to design the user interface for all the needed activities. You can see the interface below or on my [Figma page](https://www.figma.com/file/uyCdwiU2rQHNsTfw4GtCdB/Buddy?node-id=0%3A1). The names above the screen are the activity names.
![userInterface1](https://user-images.githubusercontent.com/90101184/171037750-fbe6e608-1855-48cb-88d7-07d5e7312b7b.png)
![userInterface2](https://user-images.githubusercontent.com/90101184/171037847-8905b80b-eb7b-4633-9130-a06649e36f32.png)
![userInterface3](https://user-images.githubusercontent.com/90101184/171037860-35477813-b8a1-431d-a259-7c59903e49ed.png)

### Explanation of the activities
#### Start - Login - Register
Self explanatory. A user can login with their email and password. If the email is used or the password and email combination is wrond, an error message is shown. When registering, it is checked if the email is not already in use and if the password meets the requirments (can be seen with the info icon next to password) and if "confirm password" matches the password. Appropiate error messages are generated. If the user forgot their password, they can request to reset it through the Login page.

#### RequestReset
When a user forgets their password, they can request to change it. If the mail given here is linked to a registered account, a mail will be send to it. The mail contains a link to a webpage where they can change their password. The new password still needs to meet some requirement, an error message is given when it does not meet them.

#### Home
An overview with all the plants the user has the number of plants is also displayed in orange. Each plant has their own card which you can scroll through. Every card displays the most important information for the user to see such as the plant name, their species, where it is, when it was last watered and of course the status of the plant is. When the user clicks on a card, more information on the plant is displayed. The user can refresh the page by swiping down from the top of the page. When a plant is connected to an esp, a green circle will appear on the upper left of the plant card.

#### BlufiMain - BlufiConnect - BlufiConfigure
This part of the app is mainly inspired by the [ESPBlufiForAndroid](https://github.com/EspressifApp/EspBlufiForAndroid) git repository. We imported this project into ours, simplified it and adapted the layout.

In this part you can connect your phone to a blufi device. If you are connected, you can configure the wifi settings for that blufi device. The idea is that you can configure the wifi settings of an external device via the app.

#### PlantStatistics - PlantSettings
More extensive information of the plant and the most recent sensor measurements are displayed here. If the plant is not connected to the esp, the last measured data will be shown from when it was connected. The PlantStatistics page can also be refreshed by swiping down from the top of the page. The user can change the settings for this specific plant here such as alerts and the optimal ranges for moisture, light and temperature in PlantSettings. Everything is completely customisable or you can opt to use the standard settings for that plant species for moisture, light and temperature values. This also allows the user to delete this specific plant if they wish (after a confirmation message). When deleting a plant, all sensor measurements are deleted as well.

#### Overview
Here the user can see how the measurement of moisture, light and temperature of that specific plant change over the course of the day/week/month/year. The earliest date you can go back to is the date your first planted your plant that is calculated with the age of the plant you filled in. Of course you can not look in the future so tha latest data is the current date. You can zoom in on the graphs or double tap to see specific values, these features are explained in the info button next to the title. These charts were made with [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)

#### AddPlant - AddLibrary - AddManual
The user can add more plants through the AddPlant page. You can browse the library or add your own plant if it is not in the library. When adding a plant from the library (AddLibrary), the user can choose to use the customise the settings or use the standard settings for that plant species. When adding an entirely new plant (AddManual), that is not in the library, the user needs to define all the settings and upload a picture of the plant. If a picture is not uploaded, a standard picture of the Buddy logo will be used. There are some requirements that need to be met when filling the fields like the minimum being smaller than the maximum and months only going up to 12. These are displayed when pressing the info button next to the title or with an error message when presseing "Add Plant". Because it is difficult to know which values are good for moisture, light and temperature, the info button also gives tips on average percentages/temperatures. Afterwards, this species is added to the library, the settings that were filled in here are set as the standard settings for this species. Take note that a specific user can only have unique species names, if not an error message will be shown. Furthermore an user can only have unique names for plants of the same species.

#### Account - EditAccount
The "Account" page can be accessed through the home page and contains all the information of the current user. You can log out here and also delete your account (there is a pop-up message to confirm the deletion like with all other delete buttons in our app). When deleting your account, all of your plants, species and sensor data will be deleted as well. The username and password can be edited in "EditAccount", as the email is a unique identifier of the user, it cannot be edited. When editing the password, the current password must always be given. Username can be edited without giving the current password.

#### Info
An easter egg page crediting the makers of this project. The Buddy logo appears throughout the app, when clicking on any occurance of the logo, the user will be redirected to here. Clicking the back button leads you to the page where you came from.

### Notifications
Both the Home and PlantStatistics page can generate notifications when a plant's tank level, light or temperate exceed the minimum or maximum set by the user. The user can choose to turn these notifications of in the PlantSettings page for a specific plant.

The Home page generates the "Home Notifications" channel group when the Home page loads. It also generates notifications every time it is refreshed. The notifications are only generated for plants that are connected to the esp. The PlantStatistics page generates the "Plant Specific Notifications" channel group. However, these notifications are only generated on refresh of the page as to not overwhelm the user with notifications because the Home page already generates notifications. The notifications in PlantStatistics are also possible for all plants, not only those connected to an esp. In that case, the notifications and data are based on the last measurements taken when it was connected to an esp. This is again a reason why we chose to only generate notifications on refresh otherwise the user will constantly get notifications that are not relevant anymore. When clicking on the notification, you are redirected to the PlantStatistics page of that specific plant.

Both notification channel groups can be seperately configurated in the settings of the user's phone.
***
## Insight to the finished project
![flyer-front](https://user-images.githubusercontent.com/90101184/171038974-a27512e0-6c5a-4efc-af75-4e8f2acbb64c.png)
![flyer-back](https://user-images.githubusercontent.com/90101184/171038993-43cb9345-93a0-4d04-b4b8-50f7894353b2.png)
