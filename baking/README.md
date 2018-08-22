# Baking (Food recipes)
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
