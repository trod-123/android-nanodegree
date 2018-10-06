# Nanodegree Capstone: ExpiryTracker
_(Submitted: October 2, 2018)_

Tracks when your food and groceries will expire, and stores all data remotely on the cloud for access across all your Android devices

![Sign-in](/website/images/screenshot_capstone_signin-phone-portrait.png)
![At a Glance](/website/images/screenshot_capstone_aag-phone-portrait.png)
![List](/website/images/screenshot_capstone_list-phone-portrait.png)
![Details](/website/images/screenshot_capstone_details-phone-portrait.png)
![Edit](/website/images/screenshot_capstone_edit-phone-portrait.png)
![Capture](/website/images/screenshot_capstone_capture-phone-portrait.png)
![Capture Voice](/website/images/screenshot_capstone_capture_voice-phone-portrait.png)
![Capture Text](/website/images/screenshot_capstone_capture_text-phone-portrait.png)
![Capture Date](/website/images/screenshot_capstone_date-phone-portrait.png)
![Barcode scan result](/website/images/screenshot_capstone_capture_barcode_overlay-phone-portrait.png)
![Image result](/website/images/screenshot_capstone_capture_image_overlay-phone-portrait.png)
![Widget](/website/images/screenshot_capstone_widget-phone-portrait.png)

## Features

- Saves product and date information locally and in the cloud. Access your food information offline and online across your phones and tablets (currently, only Android)
- Barcode scanner to simplify putting food into the database. Pulls your food info quickly so you don’t need to enter it yourself! (requires internet connection) Also fetches images of your food online so you don’t need to take pictures if you scanned a barcode (You can still add your own pictures, of course)
- Voice recognition to speed up food input, instead of typing it in
- Notifications to remind you when your food is about to expire. Customize how often and when you want to be notified before your food expires
- Home screen widget to display a list of foods that are expiring soon

## Skills developed

This Capstone project provided me the experience of owning the full development of an app, from ideation to creation. It was a 2-stage project, where I first had to communicate my app idea through a proposal which had to be reviewed by Udacity staff. The proposal involved a lot of planning the kinks for the app (see specifics below). The second stage involved actually coding the app, based off the specs provided in my proposal.

[Here is my proposal](../Capstone/Proposal.pdf). 

I had given myself a month to write the app. Within this short time-frame, I realized the proposal had promised a bit too much, so halfway in, I had decided to put some features aside to be implemented after submission. The features selected for removal were chosen on the basis of how much relative value they provide to the app.

That said, the whole experience was quite rewarding, and I've tried to learn and implement as much as I could for this app. Here is that skills list:

- Taking an app idea and designing it from scratch. Communicating this idea formally via a written proposal that was submitted, reviewed, and approved before coding the app
- Creating and making design decisions for:
  - The audience
  - Features
  - User flow and UI mocks and behaviors
  - Handling data persistence
  - Choosing 3rd party data sources
  - Identifying and handling edge and corner cases
  - Choosing 3rd party libraries to implement
  - Writing a list of required tasks to complete the app
- Implementing the following Android Architecture Components:
  - Room database
  - LiveData and ViewModel
  - Paging (with RecyclerView)
- Implementing the following Firebase libraries
  - Realtime Database
  - Storage
  - Authentication: E-mail auth + Google auth
  - ML Kit (for barcode scanning)
  - Job Dispatcher
- Displaying data visually, using a charting library (MPAndroidChart)
- Working with ViewPager and PagerAdapter
- Working with User Preferences
- Scheduling and customizing notifications based on user preference, and food dates
- Working with time and dates
- Creating homescreen widgets
- Creating a random data generator
- Supporting different screen sizes and orientations
- Creating custom views
- Handling user speech input
- Creating custom dialogs
- Consuming REST APIs
- Parsing JSON
- Verifying user input
- Handling unexpected and error cases
- Making the app accessible
- Localizing the app and providing RTL layouts
- Designing to Material Design specification
- Deploying a release build
- Configuring different product flavors

## Barcode scanning limits

This app is built under the free plan of the UPC Lookup Database. While this plan does not require use of an API key, this plan limits calls to 100 a day for each user. Toasts are shown to users if they had exceeded their daily call limit.

See the [UPC Lookup Database documentation](https://devs.upcitemdb.com/) for more details.

## Credits

All barcode data, including names, descriptions, item details, and images are provided by the [UPC Lookup Database](https://www.upcitemdb.com/). 
