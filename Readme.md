The app starts with a splash screen followed by a screen with a start button. Clicking on the start button, displays a list of article images by consuming the home24 api. The user can click like or dislike button and on reaching a threshold (reviewCount) configured in the gradle.properties,
the user will be presented with the review screen. In the review screen, the user can see all the items he reviewed(liked and disliked both with image and title)in a list view,
or in a grid view ( with only images without title). The user can toggle between the list view and grid view as many no.of times he wants and when he press back he goes back to the start screen
where he can start a fresh review again. To change the no. of articles in each review, you have to change the value of reviewCount in the gradle.properties file and rebuild the project.

The project was developed and tested using Android studio 3.5.3.  
It uses code generation libraries like Dagger2, Databinding, Room. So please kindly gradle-sync the project first and build it once. 
Else you might find unknown symbol references in the code base. If required, "invalidate caches and restart" using the 'File' menu option in the Android studio.

The project features:
Kotlin
Rxjava2
RxRelay
Dagger2 (Dependency injection)
Coroutines (extension)
Glide (Image library)
Navigation (Android architecture component from jetpack)
DataBinding (Android architecture component from jetpack)
ViewModel (Android architecture component from jetpack)
LiveDate, MediatorLivedata, Mutablelivedata (Android architecture component from jetpack)
PagedList (Android architecture component from jetpack)
Room (SQLite backed DB for persistence and the automatic PagedList DataSource support using paging library's BoundaryCallback)
Retrofit2 (For service calls)
okHttp (For Network layer, intercepting http logs and intercepting to add apiKey to query parameters for every service call, we make)
Espresso (AndroidJUnit4ClassRunner for UITests)
JUnit4 (For unit tests)
Instrumentation tests for the RoomDB Dao classes
ViewPager2 (with a custom transform animation for page flipping)
Recyclerview

The project uses the famous uncle Bob's CLEAN architecture.

The project has been split into 10 modules, listed below from top-down order according to the application flow:

1. app  -  Android app module which has a SplashActivity and the UI tests (espresso) and the necessary dependency injection for the entire app,
	It is the module which has application class and provides the dependency injection for all the other modules using Dagger2.
	The android test folder mocks the "Dagger-Modules":  Remote, Local and Domain and overrides the retrofit fit service to provide the data from
	a fake service, "FakeArticleService". It also implements a RepositoryStateIdlingResource to trigger the UI testing only after the network state is LOADED and the UI is in an idle testable state. After a short delay the SplashActivity in this module will launch the FeatureActivity from the feature-selection module.

2. feature-selection - Android Library module. This is the feature module and contains all the UI logic+layouts for the selection screen.

	It consumes the ViewModel from the 'presentation' module and listens  to the various network states that the 'data' module emits and reacts to them by updating the UI appropriately.
	It listens to the LiveData<PagedList> constructed from the DataSourceFactory emitted by the local Room DB layer. The paged list is then used to update the adapter backing the viewpager. There are two bindings generated using the data binding library to bind the UI layouts with the Fragments. The viewpager
	items are bound using the ViewpagerItemViewBinding. Then the whole selection fragment is bound using the FragmentSelectionBinding.  You can find these classes in the appropriately named packages in this module.
	Actually, the DataSourcefactory is generated by the Room sql query based on the initial request from the UI layer and passed via several layers before reaching 
	the view model where it is converted to the LiveData<PagedList<ArticleDomainEntity>. 
	Each layer has its own model class to reduce coupling between layers and support additional transformations in each layer. 
	Example, the 'localdata' module calls the model as ArticleLocalData which has annotated fields for providing meta info for the RoomDB, 
	where as the 'domain' or the 'feature' layer does not need those annotations so they use their own class type. 
	The network layer has a lot of other models to map the response received from the api call and then it transforms the response to a stripped down version 
	just appropriate for the next layer above ('data') to consume.
	If you can notice carefully, there are two different bindings bigImageUrl and imageUrl used for the Imageviews in the selection screen and the review screen respectively. You can see the bindings in the Bindings.kt file.
	This enables us to download and maintain two different image sizes for the two screens so that the review uses a smaller icon size for the images while the 
	selection screen uses a bigger image.
	
	The 'feature-selection' module also uses the navigation component for the navigation of the screens. 
	It uses the data binding to bind the data it got from the viewmodel to the actual view, reducing much of the boiler plate code. 

3. feature-review screen uses two pagelist adapters, one for the list view and one for the grid view. It loads the reviewed articles ( liked and disliked) with an image and title text . It manages the difference in the list and grid view requirements in the layout file itself with data binding expressions.

4. presentation - Android library module. This module has the ViewModel component which survives the lifecycle changes and provides the UI layer ('feature-selection' and 'feature-review' modules)
	with the fresh PagedList for items via LiveData to bind with the UI. It uses coroutines to do the room operations in bgscope. It also maintains the state for the progress bar layout which the UI layer uses to bind using the data binding component. When the "get-articles" action is triggered by the UI, the viewmodel uses the use case from the 'domain' layer to fetch the new DataSourceFactory from the room db. Along with datasource it also fetches the BoundaryCallback implementation from the 'data' module in the same call. The data source together with the boundary callback is then used to create a LivedData of paged list which will be observed by the 'feature-selection' layer. 

5. domain - Kotlin library module. This is the core of the app containing the use cases for the app. If necessary we can have several domain modules for each feature for scaling across the team according to the business or organization need. This layer has the non android specific implementation and defines contracts for the layer above('presentation') and below ('data')
	It calls the 'data' module layer to fetch or trigger the reactive flow of the data from the below layer(data) to the top(UI).  It does not use any android specific api except for DataSourceFactory type used as a return type from the paging library. It uses only RxJava so it is away from any platform dependencies hence following the CLEAN architecture principle.
	It has Junit tests for all the usecases provided in this layer.

6. data - Kotlin library module. This is an abstraction following the Repository pattern. It hides the actual source of the data. It encapsulates the Local and Remote data sources from the layers above.
	It defines the contract for the local and remote repositories.
	It does not use any android API. It is a pure kotlin module. This layer provides the implementation of the BoundaryCallback class required for triggering the fetch of the new data from the PagedListAdapter. But it has no reference to the android specific apis and does its job using the local and remote data source abstractions defined as interfaces for the respective layers to abide and implement. 
	It has unit tests covering all the methods exposed by this layer.

7. localdata - Android library module. This layer implements the LocalRepository interface contract defined in the 'data' module layer. It uses RoomDB API provided by the android for persistence.
	It provides the CRUD operations via Dao's and Entity definitions that the Room compiler understands. It has the instrumentation tests using in-memory db and the necessary unit tests for the API's exposed in this layer. It supports the app to run in offline.

8. remotedata - Kotlin module. This layer implements the RemoteRepository interface of the 'data' module layer. It uses Retrofit api and the okHttp as the client for the API calls.
	The  endpoint of the home24 is used for the serving the articles list. The base url is configured in the gradle.properties file and are available as BuildConfig
	defined in the 'app' module's gradle file. This is a non-android pure kotlin module, the builder, oKHttp client and the service required for the retrofit calls are provided via Dagger from the 'app' module via dependency injection. 
	 It has tests covering all the APIs that this module expose.

9. utils - Kotlin library module. This is a small module for helper classes. It provides the BehaviorRelay wrapper singleton for tracking the repository state and errors and communicating the same across the modules
	(android and non-android).
	
10. android-utils - Android library module. It provides the connectivity state monitoring Livedata which monitors the change of network availability. This live data is used in the 'feature-selection' and 'feature-review' modules to detect the change in the network state and act accordingly. The ConnectivityMonitorLiveData is a singleton injected via dagger to the Activity in the 'feature-home' modules.
    
	The project has been structured with scalability in mind. The feature modules has their own res and nav-graph so that the res folder is not bloated with too many resources to browse from.


The RepositoryStateRelay in the 'domain' layer is a domain level abstraction of the network states that the application should handle.
It is implemented using the RxRelay.
It is injected via dagger. The SelectionFeatureFragment in the 'feature-selection' module and the ReviewFragment in the 'feature-review' module uses the state to change the UI.
The relay is basically pushed from 2 places:
1. FeatureActivity - sets the initial state to EMPTY and then pushes the CONNECTED/ DISCONNECTED state based on the connection availability.
2. ArticlesBoundaryCallback - sets the LOADING/LOADED/ERROR state based on the service API call status. 

The following are the android modules:
app  (includes the UI test for the feature module and dagger dependency injection modules and the application component)
feature-selection (contains the UI for the feature)
feature-review (contains the UI for the feature)
presentation (contains the viewmodel)
android-utils (contains the connetivity state change helper)
localdata ( contains the roomdb. It has both instrumentation test and the unit tests)

The following are the kotlin library modules:
domain (contains the use cases. It has the unit tests covering all the use cases)
local ( It is an implementation of the repository pattern. It supplies data to the domain with out revealing the source of the data. It has unit tests.)
remotedata ( It is the service layer implemented using the retrofit and okhttp library. 
            The okhttp has apiinterceptor and the http logging interceptor. It has unit tests for all the apis it exposes to the data layer)
utils (Contains the utility functions and the Mapper interface which is used in the other layers to convert the models from one layer specific type to another)

I have used the RXJava and Rx BehaviorRelay to communicate between the android and non-android modules.

The versions of all the external libraries used are maintained in the versions.gradle file in the root of the project. 
So we can fiddle with the various library versions, and also the minSdk, targetSdk and compileSdk versions easily.


The application code flow:
The app starts with a splash activity in the 'app' module  and after a delay launches the FeatureActivity in the 'feature-selection' module. 
The activity then sets listeners to the connectivity changes to communicate to the other systems via the RepositoryStateRelay.
It uses the navHostFragment of the navigation component to deal with these fragment transactions.
The FeatureActivity sets the StartFragment as the starting screen. On clicking the start button, the selection screen is launched using navigation component.
Selection fragment calls viewmodel's init() where all the likes in the db are cleared so that a fresh review can start.

The SelectionFeatureFragment listens to the network state changes and as it receives the EMPTY state, it loads the datasource factory with the current query set. The default is to fetch from network, and then to update db which the pagedlist is listening to.
The query is sent to viewmodel in the 'presentation' module, the viewmodel uses the GetArticlesListAction usecase from the 'domain' module.
The GetArticlesListAction uses the ArticlesRepository defined in the 'data' module to get the DatasourceFactory and the BoundaryCallback necessary for generating
the LiveData of Pagelist of ArticleDomainEntity to be displayed in the list view. The BoundaryCallback is defined in the 'data' module itself,
where as the DatasourceFactory from the Room DB is defined in the 'localdata' module. Each query generates a new DatasourceFactory.

Once the LiveData<PageList<ArticleDomainEntity> is received via the articlesListSource in the viewmodel, the SelectionFeatureFragment tries to populate the 
viewpager2's adapter to render the view. Now there are two cases either there is no data immediately or the end of the data is reached.
The BoundaryCallback handles these 2 cases via the onZeroitemLoaded and the onItemAtEndLoaded. Both cases triggers an API call action which is performed via
ArticleService defined in the 'remotedata' layer. The BoundaryCallback is in the 'data' module which is a repository abstraction layer. 
It sets the RepositoryStateRelay to LOADING state so that the SelectionFeatureFragment in the 'feature' module can display the progressbar.
The SelectionFeatureFragment handles this by setting the isLoading Observable in the viewmodel which is bound to the progressbar view in the layout
via data binding.
Once the service call completes the control, comes back to the 'data' module which updates the result in the room db in 'localdb' module. 
And then the network state is set to LOADED state so the ui layer ('feature' module) can stop the progressbar. At the same time, the updating of the result in the
room db triggers an event in the Datasource listened by the viewmodel via the livedata and communicated to the observer in the SelectionFeatureFragment
with the new paged list. The UI updates the viewpager2 adapter and the list is shown one image at a time.
When the user clicks on an like/dislike, onClick listener is triggered. The data binding calls the onCicked method defined in the
ClickListener class class. 
The onClick listener handles the like, dislike and the review button clicks. It calls the viewmodel's handleLikeDislike method to update the like counter displayed in the bottom.
The onClick uses the navigation component to perform the navigation in the navhost fragment layout ofthe FeatureActivity. 
On reaching the reviewCount or when the list is exhausted which ever happens first , "congrats" layout is displayed along with a button to navigate to the review screen. This is  handled using the navigation component.




I hope you understand my effort. Please feel free to reach out to me for any questions. My email id is sunragav@gmail.com. Mobile: +49 15127928882
Linkedin: https://www.linkedin.com/in/sunragav/






