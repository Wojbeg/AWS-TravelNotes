# AWS-TravelNotes

The application allows users to register and log in. For successful registration, you need to verify the code sent to the e-mail address provided. All data from forms is verified with clean architecture. The user can upload his profile picture. 
If the user was already logged in, he does not have to log in again, after the splash screen he will be navigated to his home screen. The user can choose photos from the gallery or taken from the camera. User is asked for permissions in runtime. 
Then the user can create his travel notes by providing a title and optionally a photo and description. Notes can be deleted or edited. Everything is saved to the AWS cloud, so user can log in on other device and continue using app.

The project was created to learn <b> AWS Amplify</b> and improve programming skills in Android, Clean MVVM Architecture, Jetpack Compose and Dagger Hilt.<br>
Hope you enjoy the app and maybe it helps someone get started using AWS amplify using MVVM clean architecture and newer technologies like Coroutines and Flow.

## Table of contents:
* [Technologies](#technologies)
* [Illustrations](#illustrations)
* [How to run it](#HowToRunIt)

## Technologies
Project is created with:
* Clean MVVM Architecture
* AWS Amplify
* Jetpack Compose
* Coil
* Dagger - Hilt
* Kotlin Flows
* Kotlin Coroutines

## Illustrations
<p float="left">
 <img src="Illustrations/1.png" height = "300">
 <img src="Illustrations/2.png" height = "300">
 <img src="Illustrations/3.png" height = "300">
 <img src="Illustrations/4.png" height = "300">
 <img src="Illustrations/5.png" height = "300">
 <img src="Illustrations/6.png" height = "300">
 <img src="Illustrations/7.png" height = "300">
 <img src="Illustrations/8.png" height = "300">
 <img src="Illustrations/9.png" height = "300">
 <img src="Illustrations/10.png" height = "300">
 <img src="Illustrations/11.png" height = "300">
 <img src="Illustrations/12.png" height = "300">
 <img src="Illustrations/13.png" height = "300">
 <img src="Illustrations/14.png" height = "300">
 <img src="Illustrations/15.png" height = "300">
  <img src="Illustrations/16.png" height = "300">
   <img src="Illustrations/17.png" height = "300">
    <img src="Illustrations/18.png" height = "300">
     <img src="Illustrations/19.png" height = "300">
      <img src="Illustrations/20.png" height = "300">
       <img src="Illustrations/21.png" height = "300">
        <img src="Illustrations/22.png" height = "300">
         <img src="Illustrations/23.png" height = "300">
          <img src="Illustrations/24.png" height = "300">
          
</p>


## HowToRunIt
t is not that simple in this case. The application is a much more developed version of the guide provided by Amazon [AWS Amplify Tutorial](https://aws.amazon.com/getting-started/hands-on/build-android-app-amplify/). <br>
I created this application adding the latest technologies: Jetpack compose, Coil, Hilt, Coroutines, Flows and pure MVVM architecture. <br>
<b>The tutorial on the website did not work properly by itself, so I had to make a few corrections in AWS.<b> <br>
If, after installation according to the guide in the link above, it does not work, follow these steps:<br>
* First of all, follow the installation and configuration instructions
* If it turns out that the application is unable to download the note list, do this:
  1. Open AWS, next AWS AppSync, and click on the name of your application
  2. On the left bar select: Schema
  3. Now You have to find "type Query" in this text. The problem I had was that it was set to listNoteData and it should be set to listNoteDatas. So change it and it should look like this:
  ```
  type Query {
	  getNoteData(id: ID!): NoteData
	  listNoteDatas(filter: ModelNoteDataFilterInput, limit: Int, nextToken: String): ModelNoteDataConnection
  }
  ```
  4. Now save it with the "Save Schema" button
  5. Now type "Query" in the "Resolvers" section. You should see: Field listNoteDatas (...): ModelNoteDataConnection and Resolver: "NONE_DS" (name may be different). Click on the resolver.
  6. In Data source name you should select NoteDataTable (name may be different) and save "Save Resolver"
  7. Now it's time to configure "mapping templates".
    - Now if you want users of your application to have access to all other notes, just select "List items" from the list.
    Note, then you have to change the way of saving and reading photos in my project to protected or public, otherwise it will cause errors and everyone will see the notes but no photos 
    (except the owner).
    - If you want everyone to have access only to their notes and photos, enter this code:
    ```
      {
      "version" : "2017-02-28",
      "operation" : "Scan",
      "filter": {
        "expression": "#owner = :owner",
          "expressionNames": {
              "#owner": "owner"
          },
          "expressionValues": {
              ":owner": $util.dynamodb.toDynamoDBJson("$ctx.identity.sub::$ctx.identity.username")
              }
          },
      }
    ```
  8. In the Configure the response mapping template section, simply select "Return single item".
  9. Then choose save resolver.
  10. Voilà, that's it. The app should work fine
  
