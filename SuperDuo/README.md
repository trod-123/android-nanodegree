# Super Duo Project
The "Super Duo" assignment involved turning two unrelated functional apps into a production-ready state. See below for details for each.

## Football Scores
_(Last updated: January 12, 2016)_

An app that shows current scores for your favorite European soccer teams

![Collection](/website/images/screenshot_footballscores_collection.png)
![Widget](/website/images/screenshot_footballscores_widget-phone-portrait.png)

### Features

- Provides up-to-date schedules and scores of European soccer games
- Only supports score tracking for the current day, two days prior, and two days after
- Share current scores with your friends to keep them informed
- Browse through scores quickly on your homescreen with the homescreen widget

### Skills developed

- Integration points and error case handling
- Network sync statuses through shared preferences
- Accessibility: Content descriptions and the Google Talk Back interface; Localization and RTL layouts
- Collection widgets
- Tab layouts
- Recycler view

## Alexandria (Books)
_(Last updated: January 12, 2016)_

Look up details for your favorite books through search and barcode scanning

![Collection](/website/images/screenshot_books_collection.png)

### Features

- Searches for book details online by title, author, or ISBN
- Scan barcodes of books to search for more details for that book
- Save your favorite books to your own local library

### Skills developed

- Navigation drawer layouts
- Search queries using cursors
- Coordinator layouts and FABs
- User settings through shared preferences

## API Keys

For security reasons, API keys used in these projects are not public. To build these projects and test them for yourself, you must first obtain an API key and store the key in a string resource file in `/res/values/`.

For _Football Scores_, click [here](http://football-data.org) to obtain an API key. In the project, name the string `api_key`.

For _Alexandria_, click [here](https://developers.google.com/books/?hl=en) to obtain an API key. In the project, name the string `API_KEY`

## Credits

_Football Scores_ takes information hosted from http://football-data.org, an open database API created and maintained by Daniel Freitag.

Book information for _Alexandria_ comes from Google's own [Books API](https://developers.google.com/books/?hl=en).
