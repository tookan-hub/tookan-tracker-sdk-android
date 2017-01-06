# tookan-tracker-sdk-android

#### Tookan agent tracker library. ####

It provides two callbacks:

* onLocationArrive
* onJobComplete

``` Java
private TookanLocationListening tookanListener;

@Override
protected void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (tookanListener == null)
    {
        tookanListener = TookanLocationListening.getInstance(MainActivity.this);  
    }

    tookanListener.startLocationListening(this, jobID, MainActivity.this);
}

@Override
public void onLocationArrive(Location location, String locationData)
{
    // Location provides the lastest location of the agent
    // locationData provides a json array of location data
}

@Override
public void onJobComplete()
{
    // Invoked when an agent completes a Job
    tookanListener.stopLocationListening(this);
}
  
  ```  
# Installation

* Download the tookan-tracker.jar and add it in the libs folder of your android project.
* Use startLocationListening() with a valid Job ID to start location tracking.
* Use stopLocationListening() to stop tracking

# Requirements

Add following lines to the build.gradle:

```Groovy
compile fileTree(dir: 'libs', include: ['*.jar'])
compile 'com.squareup.retrofit2:retrofit:2.1.0'
compile 'com.squareup.okhttp3:logging-interceptor:3.3.1'
compile 'com.squareup.retrofit2:converter-gson:2.1.0'

compile ('org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0')
{
  exclude module: 'support-v4'
}
compile ('org.eclipse.paho:org.eclipse.paho.android.service:1.0.2')
{
  exclude module: 'support-v4'
}

```
  
