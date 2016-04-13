Web Research
============

Concept
-------

This web application is intended to assist a web researcher by managing, storing and reporting on content of interest that is discovered on the world wide web.

Rather than simply bookmark pages, pages are stored in a local database and can be viewed later without an internet connection. With a local database of researched web pages your research can be processed in ways that was previously difficult or impossible just by using an internet search engine. For example:

* Searches are focused on just those pages that have been stored allowing you to easily find relationships
* Comparisons between the current online version of the page and the stored page

Motivation
----------

Web-based research typically revolves around the use of a search engine, creating query strings and navigating to pages returned by the search. Pages of interest are then bookmarked for future reference. Finding the information later is dependant on finding the bookmark and time is often spent re-searching (pun intended) for pages that were never bookmarked. A browser history can be helpful but normally only with recent queries.

The above approach has several problems:

* Web pages change over time and are often removed
* Finding related pages is difficult. The best option available is to file bookmarks in a folder structure
* It is difficult to report on research or visualise how information is connected and has changed over time.
* You need an internet connection.

Rather than bookmark pages I began to save some of them but this introduced a new problem of how to manage the saved pages.

Status
------
### Saving web pages
A basic mechanism for retrieving and persisting web page content has been implemented. The linux **wget** command is used to retrieve content prior to it being persisted. wget  is able to retrieve the majority of pages but can encounter problems when javascript is being used.

The output of the wget command is dynamically displayed in the browser whilst the page is retrieved.

###Displaying saved web pages
Pages persisted in the database can retrieved and redisplayed but the user interface needs to be implemented.

Todo
----

* More unit tests, including [dbunit] (http://dbunit.sourceforge.net/) tests
* Use [elasticsearch] (http://www.elastic.co/) to search saved web pages
* User interface and basic workflow design
* A full basic CSS style
* Tidy up CSS menu
* Page comparison with the current internet version of the page

Development Technologies
------------------------
* [Spring Framework 4] (http://projects.spring.io/spring-framework/)
* [Hibernate 5] (http://hibernate.org/)
* [Thymeleaf] (http://www.thymelead.org)
* JQuery and AJAX/JSON for dynamic page content