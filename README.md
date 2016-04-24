# Welcome!
My name is Teddy Rodriguez, and I am currently enrolled as a student in Udacity's Android Nanodegree Program. This is my collection of projects I have completed for the nanodegree. This collection will be updated as I go along with the program, and previously submitted projects may also be updated to reflect new concepts learned.
<p/>
This repository currently contains:
<ul>
	<li>Project 0: Nanodegree Portfolio</li>
	<li>Project 1/2: Popular Movies, Stage 1/2</li>
	<li>Project 3: Super Duo (a set of two apps - Football Scores and Alexandria [a books app])</li>
</ul>

## A little about myself
I graduated from the University of California, Santa Barbara with highest honors in June of 2014, holding a Bachelor of Arts degree in Psychology and Music and a minor in Applied Psychology. I learned to program following my graduation with Javascript as I tried to create an interactive spreadsheet in Google Sheets for my fraternity, and I really got into it. Later in the same year, I discovered Udacity, and I had my first, formalized introduction to Computer Science while learning basics of programming with Python. Afterwards, I learned Java with Udacity's Introduction to Java Programming course beginning of the following year, and finally decided to join the August 2015 Android Nanodegre cohort so that I can continue practicing and developing my programming skills by making apps for my favorite mobile platform! I am truly excited to learn about programming and see what cool stuff I come up with!

## About the apps
Information about each app is inclued below, with long descriptions each followed by non-exhaustive lists of what I have used and learned. For sake of space, succeeding lists build upon elements of previous lists.
<p/>
###Nanodegree Portfolio
This will function as a hub for all the projects I will complete in the Nanodegree.
<ul>
	<li>Java</li>
	<li>Android UI basics</li>
	<li>Event listeners and button-click handling</li>
</ul>
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

<!-- While I have been provided with functional "start-up" code that does what the app intents to do
-->
## Extra information
### API keys
For security reasons, API keys used in these projects have been removed from public distributions of the code. If you want to build these projects and test them for yourself, you must obtain an appropriate API key corresponding to the project you want to build. Information about where to get API keys below. In the project, store the key in a string resource file in /res/values/.
<ul>
	<li><em>Popular Movies</em>: Information gathered from the <a href="https://www.themoviedb.org/?language=en">TMDB database</a>. Information on how to get one <a href="https://www.themoviedb.org/documentation/api">here</a>. In the project, name the string "movie_api_key".</li>
	<li><em>Football Scores</em>: All information fetched from <a href="http://football-data.org">http://football-data.org</a>, maintained by Daniel Freitag. In the project, name the string "api_key".</li>
	<li><em>Alexandria</em>: All information and pictures fetched from <a href="https://developers.google.com/books/?hl=en">Google Books's API</a>. In the project, name the string "API_KEY".</li>
</ul>

<!-- ## About myself
I am a graduate from the University of California, Santa Barbara, holding a Bachelor's of Arts degree in Psychology and Music, with a minor in Applied Psychology. I graduated last June of 2014, and currently I am working my first job as a sales associate at Target to help pay off my college loans.
<p/>
<b>The short HTML life</b>&nbsp;When I was younger, during high school, I was exposed to my first language, <b>HTML</b>, when I wanted to create my own website for a mod of a game I was working on at the time. While I was able to learn really simple things like formatting text and creating tables by looking at lots of source code and doing a lot of copy-pasting, I did not really get into it because I was overwhelmed by the amount of effort it was going to take, and I just wanted to focus on designing my mod.
<p/>
<b>Enter Javascript</b>&nbsp;Flash forward a few years to my final year in college, I wanted to create a master spreadsheet for my fraternity that contained data of hourly records, attendance, and finances all in one place. I used Google Sheets because I wanted the document to be easily accessible to everyone in my chapter. I got really into it and was able to create a fully functional, automated spreadsheet that syncs data across individual sheets and automatically formats cells based on their values and surrounding values. This automation was done through VLOOKUP and native conditional formatting features, but more advanced features were done thorugh use of <b>Javascript</b>, the second language I was exposed to. I was driven by countless features I wanted the document to include, as well as the desire to create a presentable document filled with lots of potentially useful information. For months, I was obsessed with updating it with countless of new features while fixing bugs I came across. I had a lot of fun with it, and to this day, my document has proven to be very useful in my fraternity.
<p/>
<b>College aftermath</b>&nbsp;After graduating from college, I needed a job that would help me pay off my college loans. I still did not know what I wanted to do, but I need a job.  I was able to secure a job as a sales associate at Target, with the intention of it being a temporary thing until I find something I like and want to do. The problem was that I had a hard time figuring out what I wanted to do for a career; I would always say "I'm open to try anything out". So I started seeing the issue a little differently by asking myself, "What do I like to do at home that I enjoy?" To my own question, I answered, "I love using and doing stuff on the computer!" On top of that, I reflected upon how much of an enjoyable experience it was working on my fraternity's master spreadsheet, and I decided that I woul
-->
