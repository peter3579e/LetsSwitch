# LetsSwtich

[![GitHub](https://img.shields.io/packagist/l/doctrine/orm)](https://github.com/peter3579e/LetsSwtich/blob/main/LICENSE)  <img
src="https://img.shields.io/badge/platform-Android-brightgreen"/>

A language exchange platform for people who are looking for language exchange partner. By implementing the swipe feature and map functions,
user can see their matched friend’s approximate location with the personal status shows on the marker. Moreover, the app allows user to create
activities on map. To increase the chance of face-to-face meeting.

<img
src ="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/lets_switch_launcher.png?raw=true"  width="150" />

[<img
src ="https://i.imgur.com/6Rsv9wB.png"  width="150" />](https://play.google.com/store/apps/details?id=com.peter.letsswtich)



## Application Tour Guide
*Due to the feature is data required*

**Please follow the instructions below to acquire recommendation of users**

1. Click on **Login with Google** Button
2. Fill your peronsnal detail
3. Click **NEXT**
4. Select the age range you prefer
5. Choose any gender you are looking for, or leave it empty 
6. Choose **English** as your subject
7. Choose **Taipei** as your location


You will be good to start ❤️

<img src="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/login_gif.gif?raw=true" height="500" /> <img src="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/firstQuestion_img.png?raw=true" height="500" /> <img src="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/secondQuestion_img.png?raw=true" height="500" />




## Features

### Home
* List of recommended users based on your preference
* Swipe right or left (as like and dislike) to select prefer users
* The system will check if you guys are matched
<img src="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/swipe_gif.gif?raw=true" height="500" /> 

### Chat list & Chat room
* Chat room allows you to initial a conversation with the person you like
* Message has implemented isRead function to check if user has read your message



<img src="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/chat_gif.gif?raw=true" height="500" /> 

### Map
* Map not only allows you to see your matched friedns location, but also shows the location of the events created by other users
* By clicking its image, the UI will automatically zoom to the location
<img src="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/map_gif.gif?raw=true" height="500" /> 

### Profile
* Profile helps you with creating your unique profile page
* You can make the pictures bigger by clicking it
<img src="https://github.com/peter3579e/LetsSwtich/blob/main/LetsSwtich/ScreenShot/profile_gif.gif?raw=true" height="500" /> 


## Implementation
* **Design Pattern**: `MVVM`, `Observer`, `Factory`
*  **User Interface**: `CoordinatorLayout`, `DatePicker`, `TimePicker`, `RangeSlider`, `Chip`
* **Jetpack:** `ViewModel`, `LiveData`, `Lifecycle`, `Data Bindng`, `Navigation`, `WorkManager`
* **Firebase:** `Authentication`, `Firestore`

## Third Party Library

* [Lottie](https://github.com/airbnb/lottie-android)
* [TimeAgo](https://github.com/marlonlom/timeago)
* [CardStackView](https://github.com/yuyakaido/CardStackView)
* [CircleImage](https://github.com/hdodenhof/CircleImageView)
###### *CREDIT TO THE CREATORS, LINK IS PROVIDED*

## Requirements


* Android Studio 4.0.1+
* SDK Version 23+

## Contact


**Peter Liu**: peter3579e@gmail.com

## License

MIT License

Copyright (c) 2021 Peter Liu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
