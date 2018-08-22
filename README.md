# Android Developer Nanodegree Projects
I am Teddy Rodriguez, a Grow With Google scholarship recipient in Udacity's Android Nanodegree program based in the Los Angeles area. Below showcases the projects I completed in the program, along with the wide range of skills I learned.

I've originally started the Nanodegree back in 2015, but stopped midway after entering a career in Market Research. After I got accepted into the scholarship program in January 2018, I resumed work on these projects to push for a career change as an Android developer.

I would like to complete my career transition by continuing to develop as a profession. If you're reading this and recruiting, I would love to offer my help and expertise to create Android apps that would help grow your company and satisfy your clients. Even though I'm in Los Angeles, I am open to relocating if need be so I can hone my skills. So let's chat! You can reach me via e-mail at cia.123trod@gmail.com

Thanks for looking through!

Teddy
</p>

### Contents
This repository currently contains:
<ul>
	<li>Movies: An informational app that pulls movie information from an open database API</li>
	<li>Football Scores: An app that shows current scores for your favorite football teams</li>
	<li>Alexandria: Allows users to look up their favorite books, as well as scan barcodes, to get more information about them
<!-- 		<li>Jokes: Integrates a Java library and Google Cloud Endpoints to provide jokes to the user</li>
-->		<li>Baking: A small app that displays simple recipes and step-by-step videos</li>
<li>XYZ Reader: An app that I transformed to utilize Google's Material Design specification</li>
<li><em>Coming soon: Nanodegree Capstone Project</em></li>
</ul>

## About the apps
Information about each app is inclued below, with long descriptions each followed by non-exhaustive lists of what I have used and learned. For sake of space, succeeding lists build upon elements of previous lists.

I hope you find something interesting here and there within the weeds below. Feel free to open up the code and run an app on your device if you need a closer look.

Enjoy!
<p/>

### Popular Movies
<em>(Last updated: November 9, 2015)</em><br>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_movies-tablet-portrait.png"/><br>
The first app for Android with functionality that I've made, demonstrating many firsts, but most prominently my first interaction with open database API's and handling JSON data. This app provides users with current and up-to-date movie information as provided by The Movie Database (TMDB). This also includes links to trailers and reviews posted on the TMDB website. Users can display and sort movies according to popularity, ratings, recency, and upcoming. The app also enables users to favorite a movie and add it to their own favorites list for later display. Users can also share movies with their friends via text-message or e-mail. There is also offline functionality, leveraged through use of content providers, letting users access movie information already downloaded onto their device, even when their device has no network connection. <em>Popular Movies</em> also takes advantage of Android's concept of <em>Responsive Design</em> by providing different user interface layouts for different device sizes such as phones and tablets. For example, for tablets, a well-known 2 column master-detail layout is used, displaying the list of movies on the left column, and movie details on the right.
<ul>
	<li>Intents</li>
	<li>Activities and Fragments and their lifecycles</li>
	<li>Networking</li>
	<li>Interacting with APIs</li>
	<li>Handling and parsing JSON data</li>
	<li>Content providers and offline data persistence</li>
	<li>SQLite databases</li>
	<li>Adapters and cursors</li>
	<li>Libraries</li>
	<li>Image handling</li>
	<li>Responsive design</li>
	<li>Tablet UI layouts: Master-Detail</li>
</ul>
<p/>

### Football Scores
<em>(Last updated: January 12, 2016)</em><br>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_footballscores-tablet-portrait.png"/>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_books-phone-portrait.png"/><br>
The "Super Duo" assignment involves taking two, already-built apps and turning them from a functional to a production-ready state. <em>Football Scores</em> takes information hosted from http://football-data.org, an open database API created and maintained by Daniel Freitag, and provides users of up-to-date schedules and scores of European soccer games for each day. In the app, dates are currently limited to the current day, two days prior, and two days after, for a total of 5 days. Like <em>Popular Movies</em>, users can also share game information with their friends through messaging. Along with providing accessibility features such as content descriptions and RTL functionality, this app also demonstrates one of my first attempts at adhering to Google's <em>Material Design</em> practices. For navigation, this app displays my first exposure to working with tabs.
<ul>
	<li>Integration points and error case handling</li>
	<li>Network sync statuses through shared preferences</li>
	<li>Accessibility: Content descriptions and the Google Talk Back interface; Localization and RTL layouts</li>
	<li>Collection widgets</li>
	<li>Tab layouts</li>
	<li>Recycler views</li>
	<li>Swipe to refresh</li>
</ul>
<p/>

### Alexandria (Books)
<em>(Last updated: January 12, 2016)</em><br>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_books-tablet-landscape.png"/><br>
Also a part of the "Super Duo" assignment, <em>Alexandria</em> interacts with the Google Books API to provide users an opportunity to search for books information and save them into their own local library. To facilitate looking up information, this app also provides barcode scanning functionality so that users can easily scan the ISBN barcodes of their books to look up more information about their books. While <em>Football Scores</em> demonstrates my first use of tabs in an app, <em>Alexandria</em> uses a Navigation Drawer layout. This app also attempts to conform to <em>Material Design</em> standards.
<ul>
	<li>Navigation drawer layouts</li>
	<li>Search queries using cursors</li>
	<li>Coordinator layouts and FABs</li>
	<li>User settings through shared preferences</li>
</ul>
</p>

### Baking (Food recipes)
<em>(Last updated: July 29, 2018)</em><br>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_baking_list-phone-portrait.png"/>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_baking_step-tablet-landscape.png"/>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_baking_widget-tablet-landscape.png"/><br>
While <em>Baking</em> was a refresher exercise for myself to bring a functional app into a production-ready state, it was also an opportunity to implement a video media player (ExoPlayer), create widgets that list ingredients, design a viable user interface, handle error cases gracefully, and perform UI testing through the Espresso framework. The 4 recipes in the app and their videos are provided by Udacity through a JSON file housed in Udacity's server. Note no images are provided in the JSON - the images in the app are frames pulled from the videos themselves. Upon first loading, the recipes are stored in a cache in the user's device. The user can then swipe down the list to refresh the cache.
<ul>
	<li>ExoPlayer implementation with customized controls and "YouTube"-esque full screen implementation</li>
	<li>Homescreen widgets with configuration activity, image loading, and list views</li>
	<li>Espresso user interface testing</li>
	<li>Simple fragment transitions</li>
	<li>Dynamic app bar coloring based on palette from images</li>
	<li>Nested scrollviews</li>
	<li>Missing thumbnail error handling</li>
</ul>
</p>

### XYZ Reader
<em>(Last updated: August 17, 2018)</em><br>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_xyz_list-phone-portrait.png"/>
<img src="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/screenshot_xyz_detail-tablet-landscape.png"/>
<br>
<a href="https://github.com/TROD-123/android-nanodegree/blob/master/website/images/video_xyz_animations-phone_compressed.mp4">Video showing animations, transitions, and features</a>
</p>
<em>XYZ Reader</em>, as what Udacity called it, is a mock RSS feed reader featuring banner photos, headlines, and articles. It was originally an app that garnered many negative feedback about its interface. Reviewers described it as "odd", with "wonky" and "unreadable" text and a "sad" color scheme. My task was to bring the app to life by applying Material Design principles to address this feedback, while also fixing bugs that caused the app to crash for some users. Most of the app was already fully functional, so I while did not have to worry about building its implementation so much per se, but I did have to build upon the code that was provided to me. So this was also an exercise in itself - navigating through someone else's code.
<ul>
	<li>Creating a custom theme</li>
	<li>Using CoordinatorToolbarLayout and AppBarLayout to create collapsible toolbars</li>
	<li>More experience with CardView and FABs</li>
	<li>Parallax effects with image scrolling</li>
	<li>Continuous shared elements transitions with RecyclerView and ViewPager</li>
	<li>Using the Android Transitions and Animations framework for traversing fragments</li>
	<li>Parsing large amounts of text to make it readable without the additional spacing and line breaks provided in the source data</li>
</ul>
</p>


## Extra information
### API keys
For security reasons, API keys used in these projects have been removed from public distributions of the code. If you want to build these projects and test them for yourself, you must obtain an appropriate API key corresponding to the project you want to build. Information about where to get API keys below. In the project, store the key in a string resource file in /res/values/.
<ul>
	<li><em>Popular Movies</em>: Information gathered from the <a href="https://www.themoviedb.org/?language=en">TMDB database</a>. Information on how to get one <a href="https://www.themoviedb.org/documentation/api">here</a>. In the project, name the string "movie_api_key".</li>
	<li><em>Football Scores</em>: All information fetched from <a href="http://football-data.org">http://football-data.org</a>, maintained by Daniel Freitag. In the project, name the string "api_key".</li>
	<li><em>Alexandria</em>: All information and pictures fetched from <a href="https://developers.google.com/books/?hl=en">Google Books's API</a>. In the project, name the string "API_KEY".</li>
</ul>
</p>


## A little about myself
Having explained my work so far, I'll give some more details on where I come from.

I graduated from the University of California, Santa Barbara with highest honors in June of 2014, holding a Bachelor of Arts degree in Psychology and Music and a minor in Applied Psychology. I started programming after graduation with Javascript as I tried to create an interactive Google spreadsheet for my fraternity, and I really got into it. 

Later in the same year, I discovered Udacity and had my first, formalized introduction to Computer Science while learning basics of programming with Python. Afterwards, I learned Java with Udacity's Introduction to Java Programming course beginning of the following year, and finally decided to join the August 2015 Android Nanodegre cohort so that I can continue practicing and developing my programming skills by making apps for my favorite mobile platform! 

Unfortunately I had to drop out of the nanodegree midway through in March 2016 as I entered my first job as a Market Researcher. I wanted to spend my time with the new job. However, after experiencing the industry for a little more than 2 years, while getting some opportunities to code in the company here and there, I decided to get back with coding and work on my porfolio since I thought it was fun and I missed it. Receiving Udacity's and Google's Android Nanodegree scholarship in 2018 was the perfect timing for me to return to tech.

I like building things that help people, and I like challenging myself with problems along the way. There are still a lot more that I need to learn, and I'm embracing that fact with open arms. We'll see what comes next!
