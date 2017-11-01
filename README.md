# jsonviewerdemo
A simple example of OkHttp, Gson, RecyclerView, and Picasso on Android.

The goal of this project was to take some json from a remote server, parse it, and display it within the app using a recycler view. There are a lot of valid ways to go about that, but using these libraries makes it pretty simple, and likelier less buggy than a DIY method.

This only contains 1 Activity, MainActivity. Also, models/data live within that Activity lifecycle, which is not best practice, as any lifecycle changes for that Activity would delete that data, and also makes it harder to write tests separate from android UI. Within MainActivity is a button which triggers an async call to load server data. The response string is passed to Gson which parses a json string into Java objects ready for use. From there, the ServerData object is passed to the RecyclerView/Adapter which can now render the data. Picasso library is used to load the url of the icon to display.


